package me.jeleyka.testtask.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Location;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class MobData {

    @NonFinal
    Location location;
    int respawnInSeconds;
    double health;
    double damage;
    double movementSpeed;

    public MobData setLocation(Location location) {
        this.location = location;
        return this;
    }

}
