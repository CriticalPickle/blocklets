package com.criticalpickle.blocklets.block.entity;

import com.criticalpickle.blocklets.registration.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IncubationChamberBlockEntity extends BlockEntity {
    protected final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
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

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public boolean inventoryIsEmpty() {
        if (getInventory().getSlots() <= 0) return true;
        for (int i = 0; i < getInventory().getSlots(); i++) {
            if (!getInventory().getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void clearInventory() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public boolean inventoryIsFull() {
        boolean isFull = true;
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (getInventory().getStackInSlot(i).isEmpty()) {
                isFull = false;
                break;
            }
        }
        return isFull;
    }

    public void drop() {
        SimpleContainer containerInv = new SimpleContainer(getInventory().getSlots());
        for(int i = 0; i < getInventory().getSlots(); i++) {
            containerInv.setItem(i, getInventory().getStackInSlot(i));
        }

        if(this.level != null) {
            Containers.dropContents(this.level, this.worldPosition, containerInv);
            clearInventory();
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", getInventory().serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        getInventory().deserializeNBT(registries, tag.getCompound("inventory"));
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
