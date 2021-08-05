package me.jeleyka.multiutils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Logger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SQLite {

    @Getter
    Connection connection;

    final Logger logger;
    final String folder;

    public SQLite(JavaPlugin plugin, String fileName) {
        this.logger = plugin.getLogger();
        this.folder = plugin.getDataFolder() + File.separator + fileName + ".db";
        File file = new File(folder);
        try {
            if (file.createNewFile()) {
                plugin.getLogger().info(String.format("Create %s.",
                        file.getName()));
            }
        } catch (IOException e) {
            plugin.getLogger().warning(String.format("Exception when create file %s.",
                    file.getName()));
            e.printStackTrace();
        }
    }


    public void connect() {
        if (!isConnected()) {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + folder);
                this.logger.info("Successful SQLite database connection!");
            } catch (SQLException ex) {
                this.logger.warning("Can not connect to SQLite database.");
                ex.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException ex) {
                this.logger.warning("Can not close SQLite database connection.");
                ex.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return (connection != null);
    }

    public void executeUpdate(String query) {
        new Thread(() -> {
            try {
                connection.prepareStatement(query).executeUpdate();
            } catch (SQLException ex) {
                this.logger.warning("Failed to execute database query.");
                ex.printStackTrace();
            }
        }).start();
    }

    public PreparedStatement prepareStatement(String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw  new RuntimeException();
        }
    }

    public void executeUpdate(PreparedStatement statement) {
        new Thread(() -> {
            try {
                statement.executeUpdate();
            } catch (SQLException ex) {
                this.logger.warning("Failed to execute database query.");
                ex.printStackTrace();
            }
        }).start();
    }

}