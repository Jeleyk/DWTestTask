package me.jeleyka.testtask.commands;

import me.jeleyka.multiutils.Command;
import me.jeleyka.multiutils.UtilChat;
import me.jeleyka.testtask.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReloadConfigCommand extends Command {

    Main plugin;

    public ReloadConfigCommand(Main plugin) {
        super(plugin, "reloadconfig", "reload configs of DWTestTask.",
                "/rc <mobs/localization>", "rc");
        this.plugin = plugin;
        needOp();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            notEnoughArguments(sender);
            return;
        }
        switch (args[0]) {
            case "mobs":
                plugin.getMobsConfig().reload();
                UtilChat.sendMessage(sender, "&aФайл конфигурации mobs.yml успешно перезагружен.");
                break;
            case "localization":
                plugin.getLocalizationConfig().reload();
                UtilChat.sendMessage(sender, "&aФайл конфигурации localization.yml успешно перезагружен.");
                break;
            default:
                notEnoughArguments(sender);
        }
    }
}
