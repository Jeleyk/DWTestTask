package me.jeleyka.testtask.utils;

import me.jeleyka.multiutils.configuration.Configuration;
import me.jeleyka.testtask.entities.SimpleMonster;
import me.jeleyka.testtask.game.entities.PillagerBoss;
import me.jeleyka.testtask.game.entities.SummonedZombie;
import me.jeleyka.testtask.game.entities.SummonerBoss;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;


public class MobsConfig extends Configuration {

    private HashMap<Class<? extends SimpleMonster>, MobData> data;

    public MobsConfig(JavaPlugin plugin) {
        super(plugin, "mobs");
    }

    public MobData getMobData(Class<? extends SimpleMonster> script) {
        if (data.containsKey(script)) {
            return data.get(script);
        } else {
            plugin.getLogger().warning(String.format(
                    "Configuration file mobs.yml missing MobData with script %s.", script.getName()));
            throw new RuntimeException();
        }
    }

    @Override
    public void reload() {
        super.reload();

        if (data == null) {
            data = new HashMap<>();
        } else {
            data.clear();
        }
        for (String key : get().getKeys(false)) {
            MobData mobData = new MobData(getLocation(key + ".location"),
                    getString(key + ".name"),
                    getOrDefault(key + ".respawnInSeconds", -1),
                    getDouble(key + ".health"),
                    getDouble(key + ".damage"),
                    getOrDefault(key + ".movementSpeed", -1D)
            );
            try {
                data.put((Class<? extends SimpleMonster>) Class.forName("me.jeleyka.testtask.game.entities." + getString(key + ".script")), mobData);
            } catch (Exception exception) {
                plugin.getLogger().warning(String.format(
                        "Configuration file mobs.yml has failed script in mob with key %s.", key));
                exception.printStackTrace();
                throw new RuntimeException();
            }
        }
    }

    @Override
    public void setDefaultValues() {
        saveMob(PillagerBoss.class, new MobData(new Location(
                Bukkit.getWorld("world"), 100, 65, 100),
                "&cРазоритель", 20, 8, 4, -1));
        saveMob(SummonedZombie.class, new MobData(null,
                "&cПризыватель", -1, 5, 1, -1));
        saveMob(SummonerBoss.class, new MobData(new Location(
                Bukkit.getWorld("world"), 105, 65, 100),
                "&7Мелкий зомби",20, 20, 2, -1));
        super.setDefaultValues();
    }

    private void saveMob(Class<? extends SimpleMonster> script, MobData mobData) {
        String key = script.getSimpleName();
        set(key + ".script", key);
        setLocation(key + ".location", mobData.getLocation());
        set(key + ".health", mobData.getHealth());
        set(key + ".damage", mobData.getDamage());
        set(key + ".movementSpeed", mobData.getMovementSpeed());
        set(key + ".respawnInSeconds", mobData.getRespawnInSeconds());
    }

}
