package me.jeleyka.multiutils.configuration;


import java.io.File;
import java.io.IOException;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class Configuration {

    File configFile;
    @NonFinal
    FileConfiguration config;

    JavaPlugin plugin;

    public Configuration(JavaPlugin plugin, String configName) {
        this.configFile = new File(plugin.getDataFolder(), configName + ".yml");
        this.plugin = plugin;
        try {
            if (configFile.createNewFile()) {
                plugin.getLogger().info(String.format("Configuration file %s is empty.",
                        configFile.getName()));
                this.config = YamlConfiguration.loadConfiguration(this.configFile);
                setDefaultValues();
            } else {
                this.config = YamlConfiguration.loadConfiguration(this.configFile);
            }
        } catch (IOException e) {
            plugin.getLogger().warning(String.format("Exception when create file %s.",
                    configFile.getName()));
            e.printStackTrace();
        }
        reload();
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(this.configFile);
        plugin.getLogger().info(String.format("Configuration file %s reloaded.",
                configFile.getName()));
    }

    public void setDefaultValues() {
        plugin.getLogger().info(String.format("Configuration file %s was filled with default values.",
                configFile.getName()));
        save();
    }

    public FileConfiguration get() {
        return this.config;
    }

    public boolean contains(String key) {
        return get().contains(key);
    }

    public <T> T getOrDefault(String key, T d) {
        return contains(key) ? (T) get().get(key) : d;
    }

    public String getString(String key) {
        return get().getString(key);
    }

    public int getInt(String key) {
        return get().getInt(key);
    }

    public double getDouble(String key) {
        return get().getDouble(key);
    }

    public float getFloat(String key) {
        return ((float) get().getDouble(key));
    }

    public boolean getBoolean(String key) {
        return get().getBoolean(key);
    }

    public Location getLocation(String key) {
        if (!contains(key)) return null;

        Location location = new Location(Bukkit.getWorld(getString(key + ".world")),
                getDouble(key + ".x"),
                getDouble(key + ".y"),
                getDouble(key + ".z")
        );
        if (contains(key + ".yaw")) {
            location.setYaw(getFloat(key + ".yaw"));
        }

        if (contains(key + ".pitch")) {
            location.setPitch(getFloat(key + ".pitch"));
        }

        return location;
    }

    public void set(String key, Object object) {
        get().set(key, object);
    }

    public void setLocation(String key, Location location) {
        if (location == null) {
            set(key, null);
            return;
        }
        set(key + ".world", location.getWorld().getName());
        set(key + ".x", location.getX());
        set(key + ".y", location.getY());
        set(key + ".z", location.getZ());
        set(key + ".yaw", location.getYaw());
        set(key + ".pitch", location.getPitch());
    }

    public void save() {
        if (this.config == null || this.configFile == null) {
            return;
        }
        try {
            this.get().save(this.configFile);
        } catch (IOException ex) {
            plugin.getLogger().warning(String.format("Could not save configuration file %s.",
                    configFile.getName()));
            ex.printStackTrace();
        }
    }
}
