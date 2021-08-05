package me.jeleyka.testtask.entities;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.jeleyka.multiutils.PersonalHolo;
import me.jeleyka.multiutils.SQLite;
import me.jeleyka.multiutils.UtilAlgo;
import me.jeleyka.testtask.Main;
import me.jeleyka.testtask.utils.LocalizationsConfig;
import me.jeleyka.testtask.utils.MobData;
import me.jeleyka.testtask.utils.Placeholder;
import me.jeleyka.multiutils.UtilChat;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.EntityMonster;
import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class SimpleBoss extends SimpleMonster {

    HashMap<Player, Double> damagers;
    final String name;
    Location respawnLocation;

    public SimpleBoss(EntityTypes<? extends EntityMonster> type, String name, MobData mobData) {
        super(type, name, mobData);
        this.name = name;
        this.respawnLocation = mobData.getLocation();
    }

    @Override
    public void onDamage(Player player, double damage) {
        if (damagers == null) {
            damagers = new HashMap<>();
        }
        damage = Math.min(damage, getHealth());
        damagers.merge(player, damage, Double::sum);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent(Main.getInstance().getLocalizationConfig()
                        .getLocalization("BOSS_HEALTH",
                                new Placeholder("name", name),
                                new Placeholder("health", (int) Math.max(getHealth() - damage, 0)),
                                new Placeholder("max_health", (int) getMaxHealth()))));
    }

    @Override
    public void die() {
        super.die();

        if (damagers != null) {
            List<Player> topDamagers = new ArrayList<>(damagers.keySet());
            topDamagers.sort(Comparator.comparingDouble(damagers::get));
            topDamagers = Lists.reverse(topDamagers);

            printDeathInfo(topDamagers);
            saveInfoToSql(topDamagers);

            damagers.clear();
        }

    }


    private void saveInfoToSql(List<Player> topDamagers) {

        StringBuilder topDamagersString = new StringBuilder("[");
        for (int i = 0; i < Math.min(3, topDamagers.size()); i++) {
            Player player = topDamagers.get(i);
            topDamagersString
                    .append("{player: ")
                    .append(player.getName())
                    .append(", damage:")
                    .append(((int) (damagers.get(player) + 0)))
                    .append("}, ");
        }
        topDamagersString.delete(topDamagersString.length() - 2, topDamagersString.length());
        topDamagersString.append("]");

        SQLite sql = Main.getInstance().getSql();
        PreparedStatement statement = sql.prepareStatement(
                "INSERT INTO bossDeaths VALUES (?, ?, ?);");
        try {
            statement.setString(1, getClass().getSimpleName());
            statement.setDate(2, new Date(System.currentTimeMillis()));
            statement.setString(3, topDamagersString.toString());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        sql.executeUpdate(statement);
    }

    private void printDeathInfo(List<Player> topDamagers) {
        LocalizationsConfig localization = Main.getInstance().getLocalizationConfig();
        UtilChat.sendMessageToAll(localization.getLocalization("BOSS_DEATH_MESSAGE",
                new Placeholder("name", name)));
        UtilChat.sendMessageToAll(localization.getLocalization("TOP_DAMAGERS_MESSAGE"));

        for (int i = 0; i < Math.min(3, topDamagers.size()); i++) {
            Player player = topDamagers.get(i);
            UtilChat.sendMessageToAll(localization.getLocalization("TOP_DAMAGER_MESSAGE",
                    new Placeholder("name", player.getName()),
                    new Placeholder("damage", (int) (damagers.get(player) + 0D))));
        }

    }

    @Override
    protected RespawnRunnable createRespawnRunnable() {
        return new BossRespawnRunnable();
    }

    private class BossRespawnRunnable extends RespawnRunnable {

        PersonalHolo timer;
        PersonalHolo header;

        private BossRespawnRunnable() {
            header = new PersonalHolo((player) ->
                    Main.getInstance()
                            .getLocalizationConfig()
                            .getLocalization("BOSS_RESPAWN_HEAD",
                                    new Placeholder("name", player.getName())),
                    respawnLocation.clone().add(0, 0.21, 0)
            );

            timer = new PersonalHolo((player) -> Main.getInstance()
                    .getLocalizationConfig()
                    .getLocalization("BOSS_RESPAWN_TIME",
                            new Placeholder("boss_name", name),
                            new Placeholder("time", UtilAlgo.getTimeBySeconds(time))),
                    respawnLocation
            );

        }

        @Override
        public void run() {
            super.run();
            timer.update();
            header.update();
        }

        @Override
        protected void end() {
            super.end();
            timer.killEntity();
            header.killEntity();
        }
    }

}
