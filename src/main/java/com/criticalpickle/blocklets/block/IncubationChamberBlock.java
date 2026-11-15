package com.criticalpickle.blocklets.block;

import com.criticalpickle.blocklets.Config;
import com.criticalpickle.blocklets.block.entity.IncubationChamberBlockEntity;
import com.criticalpickle.blocklets.entity.custom.BlockletEntity;
import com.criticalpickle.blocklets.registration.ModBlockEntities;
import com.criticalpickle.blocklets.registration.ModEntityTypes;
import com.criticalpickle.blocklets.util.BlockletMaterials;
import com.criticalpickle.blocklets.util.IncubationTubeColor;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class IncubationChamberBlock extends BaseEntityBlock implements LiquidBlockContainer, BucketPickup {
    public static final MapCodec<IncubationChamberBlock> CODEC = simpleCodec(IncubationChamberBlock::new);
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");
    public static final int MAX_GROWTH_STAGE = 10;
    public static final IntegerProperty GROWTH_STAGE = IntegerProperty.create("growth_stage", 0, MAX_GROWTH_STAGE);
    public static final EnumProperty<IncubationTubeColor> TUBE_COLOR = EnumProperty.create("tube_color", IncubationTubeColor.class);
    public static final int GROWTH_INTERVAL = 40; // One stage every 45 seconds (900 ticks)

    private static final VoxelShape BASE = Block.box(0, 0, 0, 16, 3, 16); // Base
    private static final VoxelShape TUBE = Block.box(2, 3, 2, 14, 14, 14); // Pillar
    private static final VoxelShape CAP = Block.box(0, 14, 0, 16, 16, 16); // Top
    private static final VoxelShape SHAPE = Shapes.or(BASE, TUBE, CAP);

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public IncubationChamberBlock(BlockBehaviour.Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(FILLED, false).setValue(GROWTH_STAGE, 0)
                .setValue(TUBE_COLOR, IncubationTubeColor.NONE));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new IncubationChamberBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FILLED, GROWTH_STAGE, TUBE_COLOR);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull VoxelShape getShape(
            @NotNull BlockState state, @NotNull BlockGetter level,
            @NotNull BlockPos pos, @NotNull CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    public @Nullable PathType getBlockPathType(
            @NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @Nullable Mob mob
    ) {
        return PathType.BLOCKED;
    }

    @Override
    public boolean canPlaceLiquid(
            @Nullable LivingEntity livingEntity, @NonNull BlockGetter blockGetter, @NonNull BlockPos pos,
            @NonNull BlockState state, @NonNull Fluid fluid) {
        return !state.getValue(FILLED) && fluid == Fluids.WATER;
    }

    @Override
    public boolean placeLiquid(
            @NotNull LevelAccessor level, @NotNull BlockPos pos, BlockState state, @NotNull FluidState fluidState
    ) {
        if (state.getValue(FILLED) || fluidState.getType() != Fluids.WATER) return false;

        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(FILLED, true), Block.UPDATE_ALL);
        }
        return true;
    }

    @Override
    public @NonNull ItemStack pickupBlock(
            @Nullable LivingEntity livingEntity, @NonNull LevelAccessor level,
            @NonNull BlockPos pos, @NonNull BlockState state
    ) {
        if (!state.getValue(FILLED)) return ItemStack.EMPTY;

        level.setBlock(pos, state.setValue(FILLED, false), Block.UPDATE_ALL);
        return new ItemStack(Items.WATER_BUCKET);
    }

    @Override
    public @NotNull Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }

    @Override
    protected @NotNull InteractionResult useItemOn(
            @NotNull ItemStack stack, @NotNull BlockState state, Level level, @NotNull BlockPos pos,
            @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult
    ) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof IncubationChamberBlockEntity entity) {
            if (stack.is(Tags.Items.BUCKETS_WATER) || stack.is(Tags.Items.BUCKETS_EMPTY)) {
                return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
            }

            if (stack.is(Tags.Items.DYES)) {
                final IncubationTubeColor color = IncubationTubeColor.getColor(stack.getItem());
                if (color != null && !color.equals(state.getValue(TUBE_COLOR))) {
                    level.setBlockAndUpdate(pos, state.setValue(TUBE_COLOR, color));
                    if (!player.isCreative()) stack.shrink(1);
                    level.playSound(
                            null, pos, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1f, 1f
                    );
                    return InteractionResult.SUCCESS;
                }
            }

            if (player.isShiftKeyDown() && !entity.inventoryIsEmpty()) {
                entity.drop();
                level.setBlockAndUpdate(pos, state.setValue(GROWTH_STAGE, 0));
                level.playSound(
                        null, pos, SoundEvents.WITHER_SHOOT, SoundSource.BLOCKS, 0.25f, 1.25f
                );
                return InteractionResult.SUCCESS;
            }

            if (stack.isEmpty()) return InteractionResult.FAIL;
            if (!state.getValue(FILLED)) return InteractionResult.FAIL;

            final ItemStacksResourceHandler entityInventory = entity.getInventory();
            for (BlockletMaterials bodyPart : BlockletMaterials.values()) {
                // Get items
                final int partIndex = bodyPart.getInventoryIndex();
                final boolean slotEmpty = entityInventory.getResource(partIndex).isEmpty();
                if (!slotEmpty) continue;

                // If concrete powder, turn into concrete
                String itemID = BuiltInRegistries.ITEM.getKey(stack.getItem()).toShortLanguageKey();
                final Item item;
                if (itemID.contains("_powder")) {
                    itemID = itemID.replace("_powder", "");
                    final Identifier concreteLocation = Identifier.withDefaultNamespace(itemID);
                    item = BuiltInRegistries.ITEM.get(concreteLocation).map(Holder::value).orElse(null);
                }
                else {
                    item = stack.getItem();
                }

                if (item == null) return InteractionResult.FAIL;

                // Determine validity of items
                final int limit = entityInventory.getCapacityAsInt(partIndex, ItemResource.of(item));
                if (bodyPart.isValidMaterial(item)) {
                    entityInventory.set(partIndex, ItemResource.of(item), limit);
                    if (!player.isCreative()) stack.shrink(limit);
                    level.sendBlockUpdated(pos, state, level.getBlockState(pos), Block.UPDATE_ALL);
                    level.playSound(
                            null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1f, 1f
                    );
                    entity.setBlockletOwner(EntityReference.of(player));
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    /**
     * Finds a valid spawn position within a 1 block radius of the incubation chamber.
     * @param level the level to find the spawn position within.
     * @param pos the position of the incubation chamber.
     * @return The first valid spawn position found or null if none exist.
     */
    private static BlockPos findSpawnPos(Level level, BlockPos pos) {
        final Iterable<BlockPos> possibleSpawnPositions = BlockPos.betweenClosed(
                pos.offset(-1, -1, -1),
                pos.offset(1, 1, 1)
        );

        for (BlockPos spawnPos : possibleSpawnPositions) {
            if (level.getBlockState(spawnPos).isAir() && !level.getBlockState(spawnPos.below()).isAir()
                    && level.getBlockState(spawnPos.below())
                        .getCollisionShape(level, pos).max(Direction.Axis.Y) > 0.0) {
                return spawnPos;
            }
        }
        return null;
    }

    /**
     * Spawns a blocklet for incubation chamber.
     * @param level the level to spawn blocklet in.
     * @param pos the position of the incubation chamber.
     * @param state the state of the incubation chamber.
     */
    private void spawnBlocklet(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof IncubationChamberBlockEntity entity) {
            // Get valid spawn position
            final BlockPos spawnPos = findSpawnPos(level, pos);
            if (spawnPos == null) return;

            // Get blocklet entity instance
            final BlockletEntity blocklet = ModEntityTypes.BLOCKLET_ENTITY.get().create(
                    level, EntitySpawnReason.TRIGGERED
            );
            if (blocklet != null && entity.getBlockletOwner()!= null) {
                // Spawn blocklet
                blocklet.setOwnerReference(entity.getBlockletOwner());
                entity.setBlockletOwner(null);
                blocklet.setBlockStack(entity.getInventory().getResource(
                        BlockletMaterials.BODY.getInventoryIndex()
                ).toStack().copy());
                blocklet.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                level.addFreshEntity(blocklet);

                // Trigger user feedback
                level.playSound(
                        null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1f, 1f
                );
                for (int i = 0; i < 20; i++) {
                    final double xOffset = level.getRandom().nextGaussian() * 0.02;
                    final double yOffset = level.getRandom().nextGaussian() * 0.02;
                    final double zOffset = level.getRandom().nextGaussian() * 0.02;
                    ((ServerLevel) level).sendParticles(
                            ParticleTypes.POOF,
                            (double)spawnPos.getX() + 0.5,
                            spawnPos.getY(),
                            (double)spawnPos.getZ() + 0.5,
                            1,
                            xOffset,
                            yOffset,
                            zOffset,
                            0.15F
                    );
                }
            }

            // Reset incubation chamber
            entity.clearInventory();
            level.setBlockAndUpdate(pos, state.setValue(GROWTH_STAGE, 0).setValue(FILLED, false));
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof IncubationChamberBlockEntity entity
                && Config.BLOCKLET_CAN_INCUBATE.getAsBoolean()) {
            if (state.getValue(GROWTH_STAGE) >= MAX_GROWTH_STAGE) {
                spawnBlocklet(level, pos, state);
            }
            else if (entity.getGrowthCounter() >= GROWTH_INTERVAL) {
                level.setBlockAndUpdate(pos, state.setValue(GROWTH_STAGE, state.getValue(GROWTH_STAGE) + 1));
                entity.resetGrowthCounter();
            }
            else if (entity.inventoryIsFull()) {
                entity.incrementGrowthCounter(1);
            }
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType
    ) {
        return level.isClientSide() ? null : createTickerHelper(
                blockEntityType, ModBlockEntities.INCUBATION_CHAMBER_BE.get(),
                (lvl, pos, _, _)
                    -> tick(lvl, pos, state)
        );
    }
}
