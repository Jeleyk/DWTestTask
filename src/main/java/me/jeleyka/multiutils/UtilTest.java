package me.jeleyka.multiutils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilTest {

    public void despawnNonCustomMobs(JavaPlugin plugin) {

        new Listener(plugin) {
            @EventHandler
            public void onMobSpawn(CreatureSpawnEvent event) {
                if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
                    event.setCancelled(true);
                }
            }
        };

    }

    Listener chatListener;
    HashMap<String, Runnable> chatEvents;

    public void runWhenChat(JavaPlugin plugin, Runnable runnable, String message) {
        if (chatListener == null) {
            chatEvents = new HashMap<>();
            chatListener = new Listener(plugin) {
                @EventHandler
                public void onChat(AsyncPlayerChatEvent e) {
                    String message = e.getMessage();
                    if (chatEvents.containsKey(message)) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, chatEvents.get(message));
                    }
                }
            };
        }
        chatEvents.put(message, runnable);
    }

}
