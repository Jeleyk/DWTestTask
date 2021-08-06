package me.jeleyka.testtask.entities.types;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityShootBowEvent;

public interface Bower extends AIMob, IRangedEntity {

    @Override
    default void initBehavior() {
        EntityMonster entityMonster = (EntityMonster) this;

        entityMonster.goalSelector.a(0, new PathfinderGoalFloat(entityMonster));
        entityMonster.goalSelector.a(5, new PathfinderGoalRandomStrollLand(entityMonster, 1.0D));
        entityMonster.goalSelector.a(6, new PathfinderGoalLookAtPlayer(entityMonster, EntityHuman.class, 8.0F));
        entityMonster.goalSelector.a(6, new PathfinderGoalRandomLookaround(entityMonster));
        entityMonster.targetSelector.a(1, new PathfinderGoalHurtByTarget(entityMonster));
        entityMonster.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(entityMonster, EntityHuman.class, true));

        PathfinderGoalBowShoot<? extends Bower> b = new PathfinderGoalBowShoot(entityMonster, 1.0D, 20, 15.0F);
        b.a(getMissedShotValue());
        entityMonster.goalSelector.a(4, b);

        entityMonster.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    /**
     * Responsible for the chance of a miss with a shot.
     * In spigot it is equal to 20 on hard and 40 on other difficulty.
     */
    default int getMissedShotValue() {
        return 30;
    }

    @Override
    default void a(EntityLiving entityLiving, float f) {
        EntityMonster entityMonster = (EntityMonster) this;
        ItemStack itemstack = entityMonster.f(entityMonster.b(ProjectileHelper.a(entityMonster, Items.BOW)));
        EntityArrow entityarrow = ProjectileHelper.a(entityMonster, itemstack, f);
        double d0 = entityLiving.locX() - entityMonster.locX();
        double d1 = entityLiving.e(0.3333333333333333D) - entityarrow.locY();
        double d2 = entityLiving.locZ() - entityMonster.locZ();
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float)(14 - entityMonster.world.getDifficulty().a() * 4));
        EntityShootBowEvent event = CraftEventFactory.callEntityShootBowEvent(entityMonster, entityMonster.getItemInMainHand(), null, entityarrow, EnumHand.MAIN_HAND, 0.8F, true);
        if (event.isCancelled()) {
            event.getProjectile().remove();
        } else {
            if (event.getProjectile() == entityarrow.getBukkitEntity()) {
                entityMonster.world.addEntity(entityarrow);
            }

            entityMonster.playSound(SoundEffects.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (entityMonster.getRandom().nextFloat() * 0.4F + 0.8F));
        }
    }
}
