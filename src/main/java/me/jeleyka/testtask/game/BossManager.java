package me.jeleyka.testtask.game;

import lombok.experimental.UtilityClass;
import me.jeleyka.testtask.entities.SimpleBoss;
import me.jeleyka.testtask.game.entities.PillagerBoss;
import me.jeleyka.testtask.game.entities.SummonerBoss;

import java.util.HashMap;

@UtilityClass
public class BossManager {

    private HashMap<Class<? extends SimpleBoss>, SimpleBoss> defaultBosses = new HashMap<>();

    public void initDefaultBosses() {
        spawnBoss(new PillagerBoss());
        spawnBoss(new SummonerBoss());
    }

    public void respawnDefaultBoss(Class<? extends SimpleBoss> clazz) throws Exception {
        defaultBosses.get(clazz).getRespawnRunnable().forcedRespawn();
    }

    public void spawnBoss(SimpleBoss boss) {
        defaultBosses.put(boss.getClass(), boss);
    }

}
