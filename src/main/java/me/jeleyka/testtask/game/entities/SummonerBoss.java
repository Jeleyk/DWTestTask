package me.jeleyka.testtask.game.entities;

import me.jeleyka.multiutils.SimpleItemStack;
import me.jeleyka.testtask.Main;
import me.jeleyka.testtask.entities.SimpleBoss;
import me.jeleyka.testtask.entities.types.Fighter;
import me.jeleyka.testtask.utils.MobData;
import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class SummonerBoss extends SimpleBoss implements Fighter {

    private Random random = new Random();
    private MobData babiesData;

    public SummonerBoss(MobData mobData) {
        super(EntityTypes.ZOMBIE, mobData);
    }

    public SummonerBoss() {
        this(Main.getInstance().getMobsConfig()
                .getMobData(SummonerBoss.class));
    }


    public void setBabiesData(MobData mobData) {
        this.babiesData = mobData;
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();

        equip();

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::unequip, 30 * 20L);

        new BukkitRunnable() {

            @Override
            public void run() {
                if (dead) {
                    this.cancel();
                    return;
                }
                for (int i = 0; i <= random.nextInt(3); i++) {
                    if (babiesData != null) {
                        new SummonedZombie(babiesData
                                .setLocation(getBukkitEntity().getLocation()));
                    } else {
                        new SummonedZombie(getBukkitEntity().getLocation());
                    }
                }
            }

        }.runTaskTimer(Main.getInstance(), 0L, 20L * 60);
    }

    private void equip() {
        EntityEquipment equipment = getBukkitLivingEntity().getEquipment();
        equipment.setChestplate(new SimpleItemStack(Material.LEATHER_CHESTPLATE).enchant(
                Enchantment.PROTECTION_ENVIRONMENTAL, 1));
        equipment.setLeggings(new SimpleItemStack(Material.LEATHER_LEGGINGS).enchant(
                Enchantment.PROTECTION_ENVIRONMENTAL, 1));
        equipment.setBoots(new SimpleItemStack(Material.LEATHER_BOOTS).enchant(
                Enchantment.PROTECTION_ENVIRONMENTAL, 1));
        equipment.setHelmet(new SimpleItemStack(Material.LEATHER_HELMET).enchant(
                Enchantment.PROTECTION_ENVIRONMENTAL, 1));
        equipment.setItemInMainHand(new SimpleItemStack(Material.STONE_SWORD).enchant(
                Enchantment.DAMAGE_ALL, 1));

    }

    private void unequip() {
        if (dead) return;
        getBukkitLivingEntity().getEquipment().clear();
    }

}
