package me.jeleyka.multiutils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;

public abstract class Command extends org.bukkit.command.Command {

    private static CommandMap commandMap;

    private final String name;
    public final String usage;
    public boolean canBeUsedFromConsole;
    public boolean needOp;
    public final String description;

    public static void registerCommandMap() {

        try {
            final Field f = CraftServer.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(Bukkit.getServer());
            f.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public Command(JavaPlugin plugin, String name, String description, String usage, String... aliases) {
        super(name, "", "", Arrays.asList(aliases));
        this.canBeUsedFromConsole = true;
        this.needOp = false;
        this.description = description;
        this.name = name;
        this.usage = usage;

        if (commandMap == null) {
            registerCommandMap();
        }
        commandMap.register(plugin.getName(), this);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player) && !this.canBeUsedFromConsole) {
            UtilChat.sendMessage(sender, "This command can not be used from console.");
            return true;
        }
        if (needOp && !sender.isOp()) {
            notEnoughPermissions(sender);
            return true;
        }
        this.onCommand(sender, args);
        return true;
    }

    public abstract void onCommand(final CommandSender sender, final String[] args);

    @Override
    public String getDescription() {
        return this.description;
    }

    protected void notEnoughPermissions(final CommandSender cs) {
        UtilChat.sendMessage(cs, "&cУ вас нет прав на исполнение данной команды.");
    }

    protected void unavailableFromConsole() {
        this.canBeUsedFromConsole = false;
    }

    protected void needOp() {
        this.needOp = true;
    }

    protected void notEnoughArguments(final CommandSender cs, final String usage) {
        UtilChat.sendMessage(cs, "&cНеверные аргументы.");
        UtilChat.sendMessage(cs, String.format("&cИспользование: &a%s&c.", usage));
    }

    protected void notEnoughArguments(final CommandSender cs) {
        this.notEnoughArguments(cs, this.getCommandUsage());
    }

    public String getName() {
        return this.name;
    }

    public String getCommandUsage() {
        return this.usage;
    }

}