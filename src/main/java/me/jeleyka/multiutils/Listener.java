package me.jeleyka.multiutils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import org.bukkit.plugin.java.JavaPlugin;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Listener implements org.bukkit.event.Listener
{
    final String listenerName;
    boolean registered;
    
    public Listener(JavaPlugin plugin) {
        this.registered = false;
        this.listenerName = this.getClass().getSimpleName();
        this.register(plugin);
    }
    
    public void register(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.registered = true;
    }
    
    public void unregister() {
        HandlerList.unregisterAll(this);
        this.registered = false;
    }
    
    public String getListenerName() {
        return this.listenerName;
    }
    
    public boolean isRegistered() {
        return this.registered;
    }
}
