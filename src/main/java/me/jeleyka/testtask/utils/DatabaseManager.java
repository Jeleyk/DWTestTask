package me.jeleyka.testtask.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.jeleyka.multiutils.SQLite;
import me.jeleyka.multiutils.UtilChat;
import me.jeleyka.testtask.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DatabaseManager {

    Main plugin;

    public DatabaseManager(Main plugin) {
        this.plugin = plugin;
        createTable();
    }

    private void createTable() {
        plugin.getSql().executeUpdate("CREATE TABLE IF NOT EXISTS bossDeaths (bossId TEXT, time DATE, damagers TEXT);");
    }

    public void saveBossInfo(String bossId, List<Player> topDamagers,
                             HashMap<Player, Double> damagers) {
        StringBuilder topDamagersString = new StringBuilder("[");
        for (int i = 0; i < Math.min(3, topDamagers.size()); i++) {
            Player player = topDamagers.get(i);
            topDamagersString
                    .append("{player: ")
                    .append(player.getName())
                    .append(", damage: ")
                    .append(((int) (damagers.get(player) + 0)))
                    .append("}, ");
        }
        topDamagersString.delete(topDamagersString.length() - 2, topDamagersString.length());
        topDamagersString.append("]");

        PreparedStatement statement = plugin.getSql().prepareStatement(
                "INSERT INTO bossDeaths VALUES (?, ?, ?);");
        try {
            statement.setString(1, bossId);
            statement.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            statement.setString(3, topDamagersString.toString());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        plugin.getSql().executeUpdate(statement);
    }

    private void printBossesInfosBetweenDates(CommandSender sender, PreparedStatement statement) {
        HashMap<String, Integer> kills = new HashMap<>();
        HashMap<String, Integer> damagers = new HashMap<>();

        try {
            ResultSet set = plugin.getSql().executeQuery(statement);
            while (set.next()) {
                kills.merge(set.getString("bossId"), 1, Integer::sum);
                Pattern pattern = Pattern.compile("\\{player: (.*?), damage: (.*?)\\}");
                Matcher matcher = pattern.matcher(set.getString("damagers"));
                while (matcher.find()) {
                    damagers.merge(matcher.group(1), Integer.parseInt(matcher.group(2)), Integer::sum);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        UtilChat.sendMessage(sender, "&aИнформация о боссах за данный период:");
        kills.forEach((boss, kills_count) -> UtilChat.sendMessage(
                sender, plugin.getLocalizationConfig()
                        .getLocalization("BOSS_INFO_COMMAND_KILLS",
                                new Placeholder("kills", kills_count),
                                new Placeholder("boss_name", boss)
                        )
                )
        );
        Map.Entry<String, Integer> topDamager =
                damagers.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get();
        UtilChat.sendMessage(sender, plugin.getLocalizationConfig()
                .getLocalization("BOSS_INFO_COMMAND_TOP_DAMAGER",
                        new Placeholder("name", topDamager.getKey()),
                        new Placeholder("damage", topDamager.getValue())
                )
        );

    }


    public void printBossesInfosBetweenDates(CommandSender sender, Date date1, Date date2, String bossId) {
        PreparedStatement statement = plugin.getSql().prepareStatement("SELECT * FROM bossDeaths " +
                "WHERE bossId = ? AND time BETWEEN ? AND ?;");
        try {
            statement.setString(1, bossId);
            statement.setDate(2, new java.sql.Date(date1.getTime()));
            statement.setDate(3, new java.sql.Date(date2.getTime()));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        try {
            printBossesInfosBetweenDates(sender, statement);
        } catch (Exception exception) {
            UtilChat.sendMessage(sender, plugin.getLocalizationConfig()
                    .getLocalization("BOSS_INFO_COMMAND_ERROR"));
        }
    }

    public void printBossesInfosBetweenDates(CommandSender sender, Date date1, Date date2) {
        PreparedStatement statement = plugin.getSql().prepareStatement("SELECT * FROM bossDeaths " +
                "WHERE time BETWEEN ? AND ?;");
        try {
            statement.setDate(1, new java.sql.Date(date1.getTime()));
            statement.setDate(2, new java.sql.Date(date2.getTime()));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        try {
            printBossesInfosBetweenDates(sender, statement);
        } catch (Exception exception) {
            UtilChat.sendMessage(sender, plugin.getLocalizationConfig()
                    .getLocalization("BOSS_INFO_COMMAND_ERROR"));
        }
    }

}
