package me.jeleyka.testtask.commands;

import me.jeleyka.multiutils.Command;
import me.jeleyka.multiutils.UtilAlgo;
import me.jeleyka.testtask.Main;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.command.CommandSender;

import java.util.Date;


public class BossesInfoCommand extends Command {

    Main plugin;

    public BossesInfoCommand(Main plugin) {
        super(plugin, "bossinfo", "Getting info of boss kills in the last time unit.",
                "/bi <boss script/all> [count of time unit] <hour/day/week/month>", "bi");
        this.plugin = plugin;
        needOp();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            notEnoughArguments(sender);
            return;
        }

        long time = 1;

        boolean hasCount = UtilAlgo.isInt(args[1]);

        if (hasCount) {
            if (args.length < 3) {
                notEnoughArguments(sender);
                return;
            }
            time = Integer.parseInt(args[1]);
        }

        switch (args[hasCount ? 2 : 1]) {
            case "day":
                time *= DateUtils.MILLIS_PER_DAY;
                break;
            case "week":
                time *= DateUtils.MILLIS_PER_DAY * 7;
                break;
            case "month":
                time *= DateUtils.MILLIS_PER_DAY * 30;
                break;
            case "hour":
                time *= DateUtils.MILLIS_PER_HOUR;
                break;
            default:
                notEnoughArguments(sender);
                return;
        }


        if ("all".equals(args[0])) {
            plugin.getDatabaseManager().printBossesInfosBetweenDates(
                    sender, new Date(System.currentTimeMillis() - time), new Date());
        } else {
            plugin.getDatabaseManager().printBossesInfosBetweenDates(
                    sender, new Date(System.currentTimeMillis() - time),
                    new Date(), args[0]);
        }

    }
}
