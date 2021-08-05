package me.jeleyka.testtask.game.entities;

import me.jeleyka.multiutils.SimpleItemStack;
import me.jeleyka.multiutils.UtilAlgo;
import me.jeleyka.testtask.Main;
import me.jeleyka.testtask.entities.SimpleBoss;
import me.jeleyka.testtask.entities.types.Fighter;
import me.jeleyka.testtask.utils.MobData;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PillagerBoss extends SimpleBoss implements Fighter, ICrossbow {


    private int phase = 1;

    public PillagerBoss(MobData mobData) {
        super(EntityTypes.PILLAGER, "&cРазоритель", mobData);
        makeCrossbowman();
    }

    public PillagerBoss() {
        this(Main.getInstance().getMobsConfig()
                .getMobData(PillagerBoss.class));
    }


    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.getDataWatcher().register((DataWatcherObject<Boolean>) UtilAlgo.
                getPrivateField(EntityPillager.class, "b", null), false);
    }

    private void makeCrossbowman() {
        this.goalSelector.a(0, new PathfinderGoalCrossbowAttack<>(this, 1.0D, 8.0F));
        giftCrossbow();
    }

    private void giftCrossbow() {
        this.setSlot(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(
                new SimpleItemStack(Material.CROSSBOW)
                        .enchant(Enchantment.MULTISHOT, 1)
                        .enchant(Enchantment.PIERCING, 1)
        ));
    }

    private void giftAxe() {
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
    }

    @Override
    public void onDamage(Player player, double damage) {
        super.onDamage(player, damage);
        if (getHealth() - damage <= getMaxHealth() / 2 && phase < 2) {
            phase++;
            giftAxe();

            new BukkitRunnable() {

                @Override
                public void run() {
                    if (dead) {
                        this.cancel();
                        return;
                    }
                    Player nearestPlayer = getNearestPlayer(6);
                    if (nearestPlayer != null) {
                        Location playerLocation = nearestPlayer.getLocation();
                        Location entityLocation = getBukkitEntity().getLocation();

                        double dx = playerLocation.getX() - entityLocation.getX();
                        double dz = playerLocation.getZ() - entityLocation.getZ();

                        getBukkitEntity().setVelocity(new Vector(dx * 0.2, 0.3, dz * 0.2));

                    }
                    getBukkitLivingEntity().addPotionEffect(new PotionEffect(
                            PotionEffectType.INCREASE_DAMAGE, 200, 1, false, false));
                }

            }.runTaskTimer(Main.getInstance(), 0L, 20L * 60);

        }
    }

    public Player getNearestPlayer(int range) {
        for (org.bukkit.entity.Entity ent : this.getBukkitEntity().getNearbyEntities(range, range, range)) {
            if (ent instanceof Player) {
                return (Player) ent;
            }
        }
        return null;
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
    }

    @Override
    public void b(boolean flag) {
        this.datawatcher.set((DataWatcherObject<Boolean>) UtilAlgo.
                getPrivateField(EntityPillager.class, "b", null), flag);
    }

    @Override
    public void a(EntityLiving entityliving, ItemStack itemstack, IProjectile iProjectile, float f) {
        this.a(this, entityliving, iProjectile, f, 1.6F);
    }

    @Override
    public void U_() {
        this.ticksFarFromPlayer = 0;
    }

    @Override
    public void a(EntityLiving entityliving, float f) {
        this.b(this, 1.6F);
    }
}
