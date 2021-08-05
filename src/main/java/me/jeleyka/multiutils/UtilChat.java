package me.jeleyka.multiutils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@UtilityClass
public class UtilChat {

    public String color(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void sendMessageToAll(String message) {
        Bukkit.broadcastMessage(UtilChat.color(message));
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }

}
