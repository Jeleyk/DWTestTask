package me.jeleyka.testtask.entities.types;

import net.minecraft.server.v1_16_R3.*;

public interface Fighter extends AIMob {

    @Override
    default void initBehavior() {
        EntityMonster entityMonster = (EntityMonster) this;

        entityMonster.goalSelector.a(0, new PathfinderGoalFloat(entityMonster));
        entityMonster.goalSelector.a(8, new PathfinderGoalLookAtPlayer(entityMonster, EntityHuman.class, 8.0F));
        entityMonster.goalSelector.a(8, new PathfinderGoalRandomLookaround(entityMonster));
        entityMonster.goalSelector.a(2, new PathfinderGoalMeleeAttack(entityMonster, 1.0D, false));
        entityMonster.goalSelector.a(7, new PathfinderGoalRandomStrollLand(entityMonster, 1.0D));
        entityMonster.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(entityMonster, EntityHuman.class, true));
    }

}
