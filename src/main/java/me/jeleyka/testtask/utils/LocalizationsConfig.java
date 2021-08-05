package me.jeleyka.testtask.utils;

import me.jeleyka.multiutils.UtilChat;
import me.jeleyka.multiutils.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class LocalizationsConfig extends Configuration {

    HashMap<String, String> localizations;

    public LocalizationsConfig(JavaPlugin plugin) {
        super(plugin, "localization");
    }

    public String getLocalization(String key, Placeholder... placeholders) {
        if (localizations.containsKey(key)) {
            String result = localizations.get(key);
            for (Placeholder placeholder : placeholders) {
                result = result.replaceAll("%" + placeholder.getKey() + "%", placeholder.getValue().toString());
            }
            return UtilChat.color(result);
        }
        plugin.getLogger().warning(String.format(
                "Configuration file localizations.yml missing the %s value.", key));
        return "";
    }

    @Override
    public void reload() {
        super.reload();

        if (localizations == null) {
            localizations = new HashMap<>();
        } else {
            localizations.clear();
        }

        for (String key : get().getKeys(false)) {
            localizations.put(key, get().getString(key));
        }
    }

    @Override
    public void setDefaultValues() {
        get().set("BOSS_DEATH_MESSAGE", "&7Босс %name% &7был повержен.");
        get().set("TOP_DAMAGERS_MESSAGE", "&7Игроки, внесшие наибольший вклад в убийство:");
        get().set("TOP_DAMAGER_MESSAGE", "  &7%name% - &c%damage% &7урона.");
        get().set("BOSS_HEALTH", "%name%&7: &c%health%&7/&c%max_health%❤");
        get().set("BOSS_RESPAWN_HEAD", "&7Привет, %name%.");
        get().set("BOSS_RESPAWN_TIME", "%boss_name% &7появится через &a%time%&7.");
        get().set("FORCE_BOSS_RESPAWN", "%boss_name% &aпринудительно возрожден.");
        super.setDefaultValues();
    }
}
