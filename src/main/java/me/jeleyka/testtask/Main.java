package me.jeleyka.testtask;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.jeleyka.multiutils.PersonalHolo;
import me.jeleyka.multiutils.SQLite;
import me.jeleyka.multiutils.UtilTest;
import me.jeleyka.testtask.commands.ReloadConfigCommand;
import me.jeleyka.testtask.commands.RespawnBossCommand;
import me.jeleyka.testtask.entities.SimpleMonster;
import me.jeleyka.testtask.game.BossManager;
import me.jeleyka.testtask.game.entities.PillagerBoss;
import me.jeleyka.testtask.game.entities.SummonedZombie;
import me.jeleyka.testtask.game.entities.SummonerBoss;
import me.jeleyka.testtask.utils.LocalizationsConfig;
import me.jeleyka.testtask.utils.MobsConfig;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    LocalizationsConfig localizationConfig;
    MobsConfig mobsConfig;
    SQLite sql;

    @Override
    public void onEnable() {
        instance = this;

        localizationConfig = new LocalizationsConfig(this);
        mobsConfig = new MobsConfig(this);

        setupSQL();

        initCommands();

        UtilTest.despawnNonCustomMobs(this);

        BossManager.initDefaultBosses();

        //UtilTest.runWhenChat(this, PillagerBoss::new, "1");
        //UtilTest.runWhenChat(this, SummonerBoss::new, "2");

    }

    @Override
    public void onDisable() {
        sql.disconnect();
        Bukkit.getWorlds().forEach(
                w -> w.getEntities().stream()
                        .filter(e -> ((CraftEntity) e).getHandle() instanceof SimpleMonster)
                .forEach(Entity::remove)
        );
        PersonalHolo.clearHolos();
    }

    private void initCommands() {
        new ReloadConfigCommand(this);
        new RespawnBossCommand(this);
    }

    private void setupSQL() {
        sql = new SQLite(this, "database");
        sql.connect();
        sql.executeUpdate("CREATE TABLE IF NOT EXISTS bossDeaths (bossId TEXT, time DATE, damagers TEXT);");
    }

}