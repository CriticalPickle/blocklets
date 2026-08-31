package com.criticalpickle.blocklets.block.entity;

import com.criticalpickle.blocklets.registration.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class IncubationChamberBlockEntity extends BlockEntity {
    protected final ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(4) {
        @Override
        protected int getCapacity(int index, @NonNull ItemResource resource) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int index, @NonNull ItemStack previousContents) {
            setChanged();
            if(level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private int growthCounter = 0;

    public IncubationChamberBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.INCUBATION_CHAMBER_BE.get(), pos, blockState);
    }

    public void incrementGrowthCounter(int increment) {
        this.growthCounter += increment;
    }

    public int getGrowthCounter() {
        return this.growthCounter;
    }

    public void resetGrowthCounter() {
        this.growthCounter = 0;
    }

    public ItemStacksResourceHandler getInventory() {
        return inventory;
    }

    public boolean inventoryIsEmpty() {
        if (getInventory().size() <= 0) return true;
        for (int i = 0; i < getInventory().size(); i++) {
            if (!getInventory().getResource(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void clearInventory() {
        for (int i = 0; i < getInventory().size(); i++) {
            inventory.set(i, ItemResource.EMPTY, 0);
        }
    }

    public boolean inventoryIsFull() {
        boolean isFull = true;
        for (int i = 0; i < inventory.size(); i++) {
            if (getInventory().getResource(i).isEmpty()) {
                isFull = false;
                break;
            }
        }
        return isFull;
    }

    public void drop() {
        final int size = getInventory().size();
        final SimpleContainer containerInv = new SimpleContainer(size);
        for(int i = 0; i < size; i++) {
            containerInv.setItem(i, getInventory().getResource(i).toStack());
        }

        if(this.level != null) {
            Containers.dropContents(this.level, this.worldPosition, containerInv);
            clearInventory();
        }
    }

    @Override
    public void preRemoveSideEffects(@NonNull BlockPos pos, @NonNull BlockState state) {
        final Level level = this.getLevel();
        if (level != null) {
            drop();
            level.updateNeighbourForOutputSignal(pos, state.getBlock());
        }
        super.preRemoveSideEffects(pos, state);
    }

    public void copyInventory(ItemStacksResourceHandler oldInventory) {
        if(oldInventory != null) {
            int minSize = Math.min(getInventory().size(), oldInventory.size());
            for(int i = 0; i < minSize; i++) {
                getInventory().set(i, oldInventory.getResource(i), oldInventory.getAmountAsInt(i));
            }
        }
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        getInventory().serialize(output);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        getInventory().deserialize(input);

        if (input.child("inventory").isPresent()) {
            final NonNullList<ItemStack> stacks = NonNullList.withSize(4, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(input.child("inventory").get(), stacks);
            final ItemStacksResourceHandler temp = new ItemStacksResourceHandler(stacks);
            copyInventory(temp);
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
