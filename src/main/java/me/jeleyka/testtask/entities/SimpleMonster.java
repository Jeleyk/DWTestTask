package me.jeleyka.testtask.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.jeleyka.multiutils.UtilChat;
import me.jeleyka.testtask.Main;
import me.jeleyka.testtask.entities.types.AIMob;
import me.jeleyka.testtask.game.BossManager;
import me.jeleyka.testtask.utils.MobData;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Behavior of the monster configure in {@link AIMob#initBehavior()}
 * and in override methods.
 * For respawnable monster need constructor without args.
 */

@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class SimpleMonster extends EntityMonster implements AIMob {

    int respawnInSeconds;

    @Getter
    RespawnRunnable respawnRunnable;


    public SimpleMonster(EntityTypes<? extends EntityMonster> type, String name, MobData mobData) {
        super(type, ((CraftWorld) mobData.getLocation().getWorld()).getHandle());

        this.respawnInSeconds = mobData.getRespawnInSeconds();

        this.setCustomNameVisible(true);
        this.canPickUpLoot = false;
        this.craftAttributes.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(mobData.getHealth());
        this.craftAttributes.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(mobData.getDamage());
        if (mobData.getMovementSpeed() != -1) {
            this.craftAttributes.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(mobData.getMovementSpeed());
        }
        this.setHealth((float) mobData.getHealth());
        this.noDamageTicks = 0;
        this.persistent = true;
        this.canPickUpLoot = false;
        this.setCustomName(new ChatComponentText(UtilChat.color(name)));
        initBehavior();
        this.setLocation(mobData.getLocation().getX(), mobData.getLocation().getY(),
                mobData.getLocation().getZ(), mobData.getLocation().getYaw(), mobData.getLocation().getPitch());
        ((CraftWorld) mobData.getLocation().getWorld()).getHandle().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        onSpawn();
    }

    protected void onSpawn() {
    }

    protected void onDamage(Player player, double damage) {
    }

    protected void onDeath(Location loc) {
    }

    protected void onAttack(Player player) {
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {

    }

    @Override
    public final void a(EntityLiving entityliving, net.minecraft.server.v1_16_R3.Entity entity) {
        if (entity instanceof EntityPlayer)
            onAttack(((Player) entity.getBukkitEntity()));
        super.a(entityliving, entity);
    }

    @Override
    public void die() {
        super.die();
        onDeath(getBukkitEntity().getLocation());
        if (Main.getInstance().isEnabled())
            respawnTask();
    }

    @Override
    public boolean damageEntity(DamageSource damageSource, float damage) {
        if (damageSource.getEntity() instanceof EntityPlayer) {
            onDamage((Player) damageSource.getEntity().getBukkitEntity(), damage);
        }
        return super.damageEntity(damageSource, damage);
    }

    public LivingEntity getBukkitLivingEntity() {
        return (LivingEntity) super.getBukkitEntity();
    }

    private void respawnTask() {
        if (respawnInSeconds < 0) return;

        respawnRunnable = createRespawnRunnable();
        respawnRunnable.runTaskTimer(Main.getInstance(), 0L, 20L);
    }


    protected RespawnRunnable createRespawnRunnable() {
        return new RespawnRunnable();
    }

    private void respawn() {
        Class<? extends SimpleMonster> clazz = getClass();
        try {
            BossManager.spawnBoss((SimpleBoss) clazz.getConstructor().newInstance());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public class RespawnRunnable extends BukkitRunnable {

        protected int time = respawnInSeconds;

        @Override
        public void run() {

            if (time == 0) {
                end();
                return;
            }


            time--;

        }

        protected void end() {
            this.cancel();
            respawn();
        }

        public void forcedRespawn() {
            time = 1;
        }

    }


}

