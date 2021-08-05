package me.jeleyka.testtask.commands;

import me.jeleyka.multiutils.Command;
import me.jeleyka.multiutils.UtilChat;
import me.jeleyka.testtask.Main;
import me.jeleyka.testtask.entities.SimpleBoss;
import me.jeleyka.testtask.entities.SimpleMonster;
import me.jeleyka.testtask.game.BossManager;
import me.jeleyka.testtask.utils.Placeholder;
import org.bukkit.command.CommandSender;

public class RespawnBossCommand extends Command {

    Main plugin;

    public RespawnBossCommand(Main plugin) {
        super(plugin, "forcedrespawn", "Force respawn of boss.",
                "/fr <boss script>", "fr");
        this.plugin = plugin;
        needOp();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            notEnoughArguments(sender);
            return;
        }

        try {
            Class<? extends SimpleBoss> clazz =
                    (Class<? extends SimpleBoss>) Class.forName("me.jeleyka.testtask.game.entities." + args[0]);
            UtilChat.sendMessage(sender, plugin
                    .getLocalizationConfig()
                    .getLocalization("FORCE_BOSS_RESPAWN",
                    new Placeholder("boss_name", clazz.getSimpleName())));
            BossManager.respawnDefaultBoss(clazz);
        } catch (Exception exception) {
            notEnoughArguments(sender);
        }
    }
}
