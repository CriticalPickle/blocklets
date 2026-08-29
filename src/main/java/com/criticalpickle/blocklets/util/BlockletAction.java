package com.criticalpickle.blocklets.util;

import com.criticalpickle.blocklets.entity.ai.goal.AttackGoal;
import com.criticalpickle.blocklets.entity.ai.goal.HusbandryGoal;
import com.criticalpickle.blocklets.entity.ai.goal.MineAccessibleBlocksGoal;
import com.criticalpickle.blocklets.entity.custom.BlockletEntity;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;

import java.util.function.Consumer;

/**
 * Constant declaration of available actions for Blocklets with their class name and relating set calls.
 */
public enum BlockletAction {
    MINE(
            MineAccessibleBlocksGoal.class.getSimpleName(),
            blocklet -> blocklet.getGoalManager().setMineAction()
    ),
    MELEE_ATTACK(
            AttackGoal.class.getSimpleName(),
            blocklet -> blocklet.getGoalManager().setMeleeAttackAction()
    ),
    HUSBANDRY(
            HusbandryGoal.class.getSimpleName(),
            blocklet -> blocklet.getGoalManager().setHusbandryAttackAction()
    ),
    STROLL(
            RandomStrollGoal.class.getSimpleName(),
            blocklet -> blocklet.getGoalManager().setStrollAction()
    );

    private final String className;
    private final Consumer<BlockletEntity> setGoalConsumer;

    BlockletAction(String className, Consumer<BlockletEntity> setGoalConsumer) {
        this.className = className;
        this.setGoalConsumer = setGoalConsumer;
    }

    public String getClassName() {
        return this.className;
    }

    /**
     * Calls the setGoalConsumer for this BlockletAction using a BlockletEntity.
     * @param blocklet the BlockletEntity to use within the setGoalConsumer.
     */
    public void setAction(BlockletEntity blocklet) {
        this.setGoalConsumer.accept(blocklet);
    }

    /**
     * Finds a BlockletAction for a specific goal class name.
     * @param className the class name String to find a BlockletAction for.
     * @return BlockletAction or null if no action with given class name exists.
     */
    public static BlockletAction find(String className) {
        for (BlockletAction action : BlockletAction.values()) {
            if (action.getClassName().equalsIgnoreCase(className)) {
                return action;
            }
        }
        return null;
    }
}
