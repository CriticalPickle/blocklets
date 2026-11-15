package com.criticalpickle.blocklets.util;

import com.criticalpickle.blocklets.Blocklets;
import com.criticalpickle.blocklets.entity.ai.goal.AttackGoal;
import com.criticalpickle.blocklets.entity.ai.goal.HusbandryGoal;
import com.criticalpickle.blocklets.entity.ai.goal.MineAccessibleBlocksGoal;
import com.criticalpickle.blocklets.entity.custom.BlockletEntity;
import com.criticalpickle.blocklets.registration.ModTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.neoforged.neoforge.common.Tags;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manages dynamic goals relating to Blocklets by handling goal bookkeeping
 * relating to a specific Blocklet.<br></br>
 * The main purpose of the manager is to allow for support of swapping the
 * goal with priority 1, or the Blocklet's assigned BlockletAction, based on
 * the provided tool in its main hand.
 */
public class BlockletGoalManager {
    private final BlockletEntity blocklet;
    private final Set<String> blockletActions;
    public boolean setNewGoal;

    public BlockletGoalManager(BlockletEntity blocklet) {
        this.blocklet = blocklet;
        this.blockletActions = Arrays.stream(BlockletAction.values())
                .map(BlockletAction::getClassName)
                .collect(Collectors.toSet());
    }

    /**
     * Logs currently selected action under the context that it has changed.
     */
    public void logAction() {
        Blocklets.LOGGER.info("Blocklet at {} has changed action to \"{}\"!",
                this.blocklet.position(), getActionGoal());
    }

    /**
     * Swaps action with a goal by removing old
     * Blocklet action before adding this goal.
     * @param goal the goal to swap with the current action.
     */
    private void swapAction(Goal goal) {
        blocklet.goalSelector.removeGoal(getActionGoal());
        blocklet.goalSelector.addGoal(2, goal);
        blocklet.targetSelector.removeAllGoals(_ -> true); // Remove all
        setNewGoal = true;
        logAction();
    }

    /**
     * Swaps action with a RandomStrollGoal by removing
     * old Blocklet action before adding this RandomStrollGoal.
     */
    public void setStrollAction() {
        swapAction(new RandomStrollGoal(blocklet, 0.4f));
    }

    /**
     * Swaps action with a MineAccessibleBlocksGoal by removing
     * old Blocklet action before adding this MineAccessibleBlocksGoal.
     */
    public void setMineAction() {
        swapAction(new MineAccessibleBlocksGoal(
                blocklet, Tags.Blocks.ORES, 8, 1, 0.8
        ));
    }

    /**
     * Swaps action with a MeleeAttackGoal by removing old Blocklet
     * action before adding this MeleeAttackGoal. Additionally, it
     * adds the target selector goals for grounded hostile mobs.
     */
    public void setMeleeAttackAction() {
        swapAction(new AttackGoal(blocklet, 1, true));
        blocklet.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(
                blocklet, Mob.class, 10, true, false,
                (target, _) -> target.is(ModTags.Entities.BLOCKLET_MELEE_ENEMIES) // todo - modded support
        ));
    }

    /**
     * Swaps action with a MeleeAttackGoal by removing old Blocklet
     * action before adding this MeleeAttackGoal. Additionally, it
     * adds the target selector goals for grounded husbandry mobs.
     */
    public void setHusbandryAttackAction() {
        swapAction(new HusbandryGoal(blocklet, 0.8f, true));
        blocklet.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(
                blocklet, Mob.class, 10, true, false,
                (target, _) -> target.is(ModTags.Entities.BLOCKLET_HUSBANDRY) // todo - modded support
        ));
    }

    /**
     * Gets the managed Blocklet's current action goal.
     * @return Any goal with a class name that correlates to a blocklet action.
     */
    public Goal getActionGoal() {
        return blocklet.goalSelector.getAvailableGoals().stream()
                .map(WrappedGoal::getGoal)
                .filter(goal -> blockletActions.contains(goal.getClass().getSimpleName()))
                .findAny().orElse(null);
    }

    /**
     * Adds the mining goal depending on status of main hand equipment slot.
     */
    public void updateMiningGoal() {
        final boolean isPickaxeTool = blocklet.getItemBySlot(EquipmentSlot.MAINHAND).is(ItemTags.PICKAXES);
        final Goal actionGoal = getActionGoal();

        if (isPickaxeTool && !(actionGoal instanceof MineAccessibleBlocksGoal)) {
            setMineAction();
        }
    }

    /**
     * Adds the melee attack goal depending on status of main hand equipment slot.
     */
    public void updateMeleeAttackGoal() {
        final boolean isAxe = blocklet.getItemBySlot(EquipmentSlot.MAINHAND).is(ItemTags.AXES);
        final Goal actionGoal = getActionGoal();

        if (isAxe && !(actionGoal instanceof AttackGoal)) {
            setMeleeAttackAction();
        }
    }

    /**
     * Adds the husbandry goal depending on status of main hand equipment slot.
     */
    public void updateHusbandryGoal() {
        final boolean isSword = blocklet.getItemBySlot(EquipmentSlot.MAINHAND).is(ItemTags.SWORDS);
        final Goal actionGoal = getActionGoal();

        if (isSword && !(actionGoal instanceof HusbandryGoal)) {
            setHusbandryAttackAction();
        }
    }

    /**
     * Adds the mining goal depending on status of recently added goals.
     */
    public void updateStrollGoal() {
        final Goal actionGoal = getActionGoal();

        if (!setNewGoal && !(actionGoal instanceof RandomStrollGoal)) {
            setStrollAction();
        }
    }
}
