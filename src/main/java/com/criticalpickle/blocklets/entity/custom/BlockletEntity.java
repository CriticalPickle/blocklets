package com.criticalpickle.blocklets.entity.custom;

import com.criticalpickle.blocklets.Blocklets;
import com.criticalpickle.blocklets.entity.ai.goal.EntityFollowOwnerGoal;
import com.criticalpickle.blocklets.util.BlockletAction;
import com.criticalpickle.blocklets.util.BlockletGoalManager;
import com.criticalpickle.blocklets.util.BlockletMaterials;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class BlockletEntity extends PathfinderMob implements OwnableEntity {
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState startActionAnimationState = new AnimationState();
    public final AnimationState mineAnimationState = new AnimationState();
    public final AnimationState swingAnimationState = new AnimationState();

    // Note that inventory stack handler size can be changed later but must
    // override deserializeNBT. Inventory is only currently for the server to
    // bookkeep blocklet items as client doesn't receive reliable updates.
    private final ItemStackHandler inventory = new ItemStackHandler(3);
    private final BlockletGoalManager goalManager;
    public static final double BASE_ARMOR = 0.0;
    public static final double BASE_ARMOR_TOUGHNESS = 0.0;

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(BlockletEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<ItemStack> BLOCKLET_BLOCK =
            SynchedEntityData.defineId(BlockletEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<String> START_ACTION =
            SynchedEntityData.defineId(BlockletEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> MINING =
            SynchedEntityData.defineId(BlockletEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SWINGING =
            SynchedEntityData.defineId(BlockletEntity.class, EntityDataSerializers.BOOLEAN);

    public BlockletEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.goalManager = new BlockletGoalManager(this);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER_UUID, Optional.empty());
        builder.define(BLOCKLET_BLOCK, ItemStack.EMPTY);
        builder.define(START_ACTION, "");
        builder.define(MINING, false);
        builder.define(SWINGING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EntityFollowOwnerGoal<>(
                this, 1.0f, 10.0f, 2.0f
        ));
        // Goal with priority 2 is always considered the Blocklet's
        // action. By default, this is a simple random stroll goal.
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.4f));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    /**
     * Updates blocklet attributes based on a possible new block.
     */
    private void updateBlockletAttributes() {
        final ItemStack blockStack = this.getBlockStack();
        if (blockStack.isEmpty()) return;
        if (!(blockStack.getItem() instanceof BlockItem blockItem)) return;
        final Block block = blockItem.getBlock();

        // Update armor and armor toughness
        if (block.defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            final AttributeInstance armorAttribute = this.getAttribute(Attributes.ARMOR);
            final AttributeInstance toughnessAttribute = this.getAttribute(Attributes.ARMOR_TOUGHNESS);
            final float newArmor;
            final float newToughness;

            if (block.defaultBlockState().is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                newArmor = 8f;
                newToughness = 2f;
            }
            else if (block.defaultBlockState().is(BlockTags.NEEDS_IRON_TOOL)) {
                newArmor = 6f;
                newToughness = 0f;
            }
            else if (block.defaultBlockState().is(BlockTags.NEEDS_STONE_TOOL)) {
                newArmor = 3f;
                newToughness = 0f;
            }
            else {
                newArmor = 1f;
                newToughness = 0f;
            }

            if (armorAttribute != null) armorAttribute.setBaseValue(BASE_ARMOR + newArmor);
            if (toughnessAttribute != null) toughnessAttribute.setBaseValue(BASE_ARMOR_TOUGHNESS + newToughness);
        }
    }

    @Override
    public boolean fireImmune() {
        return this.getBlockStack().is(Tags.Items.ORES_NETHERITE_SCRAP)
                || this.getBlockStack().is(Tags.Items.STORAGE_BLOCKS_NETHERITE);
    }

    @Override
    protected @NotNull Vec3 getLeashOffset() {
        return new Vec3(0.0, this.getEyeHeight() * 0.6, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D) {
                this.walkAnimationState.startIfStopped(tickCount);
            }
            else {
                this.walkAnimationState.stop();
            }

            if (!getStartAction().isEmpty()) {
                this.startActionAnimationState.startIfStopped(tickCount);
            }
            else {
                this.startActionAnimationState.stop();
            }

            if (isMining()) {
                this.mineAnimationState.startIfStopped(tickCount);
            }
            else {
                this.mineAnimationState.stop();
            }

            if (isSwinging()) {
                this.swingAnimationState.startIfStopped(tickCount);
            }
            else {
                this.swingAnimationState.stop();
            }
        }
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot slot, @NotNull ItemStack stack) {
        super.setItemSlot(slot, stack);
        if (slot == EquipmentSlot.MAINHAND && !level().isClientSide) {
            goalManager.setNewGoal = false;
            goalManager.updateMiningGoal();
            goalManager.updateMeleeAttackGoal();
            goalManager.updateHusbandryGoal();
            // todo - additional goals
            goalManager.updateStrollGoal();
        }
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        // Evaluate the condition on both client and server
        if (player.isShiftKeyDown()) {
            final ItemStack interactionStack = player.getItemInHand(hand);

            // Required skip as sometimes air as the
            // interaction stack item leaks into this function.
            if (interactionStack.isEmpty()) return super.mobInteract(player, hand); // todo - unequip item

            // Run the world/data mutation logic strictly on the Server
            if (!level().isClientSide()) {
                final ItemStack toStore = new ItemStack(interactionStack.getItem());
                boolean isSet = false;

                // Check if item is stackable (i.e. a possible tool)
                // or if it is a block to display. This item will be
                // swapped with the returned existing item, if applicable.
                if (!toStore.isStackable()) {
                    if (!getEquippedItem().isEmpty()) {
                        level().addFreshEntity(new ItemEntity(
                                level(), this.getBlockX(), this.getBlockY(), this.getBlockZ(),
                                new ItemStack(getEquippedItem().getItem())
                        ));
                    }
                    this.setItemSlot(EquipmentSlot.MAINHAND, toStore.copy());
                    isSet = true;
                }
                else if (BlockletMaterials.BODY.isValidMaterial(toStore.getItem())) {
                    if (!getBlockStack().isEmpty()) {
                        level().addFreshEntity(new ItemEntity(
                                level(), this.getBlockX(), this.getBlockY(), this.getBlockZ(),
                                new ItemStack(getBlockStack().getItem())
                        ));
                    }
                    this.setBlockStack(toStore.copy());
                    isSet = true;
                }

                if (isSet && !player.isCreative()) interactionStack.shrink(1);
            }

            // Return success on both client and server to keep them in sync
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    @Override
    public @Nullable LivingEntity getOwner() {
        final UUID uuid = getOwnerUUID();
        return uuid == null ? null : this.level().getPlayerByUUID(uuid);
    }

    public void setStartAction(String action) {
        this.entityData.set(START_ACTION, action);
    }

    public String getStartAction() {
        return this.entityData.get(START_ACTION);
    }

    public void setMining(boolean isMining) {
        this.entityData.set(MINING, isMining);
    }

    public boolean isMining() {
        return this.entityData.get(MINING);
    }

    public void setSwinging(boolean isSwinging) {
        this.entityData.set(SWINGING, isSwinging);
    }

    public boolean isSwinging() {
        return this.entityData.get(SWINGING);
    }

    public BlockletGoalManager getGoalManager() {
        return goalManager;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public void setBlockStack(ItemStack stack) {
        this.entityData.set(BLOCKLET_BLOCK, stack);
        this.inventory.setStackInSlot(0, stack);
        updateBlockletAttributes();
    }

    public ItemStack getBlockStack() {
        return this.entityData.get(BLOCKLET_BLOCK);
    }

    public ItemStack getEquippedItem() {
        return this.getMainHandItem();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 1D)
                .add(Attributes.ARMOR, BASE_ARMOR)
                .add(Attributes.ARMOR_TOUGHNESS, BASE_ARMOR_TOUGHNESS);
    }

    @Override
    // Remove was overridden instead of "die" as it is more reliable to ensure drops are always dropped.
    public void remove(@NotNull RemovalReason reason) {
        if (!this.level().isClientSide() && (reason == RemovalReason.KILLED || reason == RemovalReason.DISCARDED)) {
            Blocklets.LOGGER.info("Unnamed {} died: {}", this, this.getCombatTracker().getDeathMessage().getString());

            for (int i = 0; i < this.getInventory().getSlots(); i++) {
                final ItemStack stack = this.getInventory().getStackInSlot(i);
                if (!stack.isEmpty()) {
                    level().addFreshEntity(new ItemEntity(
                            level(), this.getX(), this.getY(), this.getZ(), stack.copy()
                    ));
                    this.getInventory().setStackInSlot(i, ItemStack.EMPTY);
                }
            }

            if (!this.getEquippedItem().isEmpty()) {
                level().addFreshEntity(new ItemEntity(
                        level(), this.getX(), this.getY(), this.getZ(), this.getEquippedItem().copy()
                ));
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }
        }
        super.remove(reason);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        final UUID owner = getOwnerUUID();
        if (owner != null) {
            tag.putUUID("Owner", owner);
        }
        tag.put("inventory", this.getInventory().serializeNBT(this.registryAccess()));
        // Action must be saved as it is purged from Blocklet on world close.
        tag.putString("blockletAction", this.goalManager.getActionGoal().getClass().getSimpleName());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Owner")) {
            setOwnerUUID(tag.getUUID("Owner"));
        }
        this.getInventory().deserializeNBT(this.registryAccess(), tag.getCompound("inventory"));
        this.entityData.set(BLOCKLET_BLOCK, this.getInventory().getStackInSlot(0).copy());
        final BlockletAction action = BlockletAction.find(tag.getString("blockletAction"));
        if (action != null) {
            action.setAction(this);
        }
    }
}
