package me.xTDKx.Main;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerConfig {

    private static FileConfiguration players;
    private static File playersFile;

    public static void setupPlayers(File file) {
        reloadPlayers(file);
    }

    public static void reloadPlayers(File file) {
        playersFile = new File(file, "players.yml");
        players = YamlConfiguration.loadConfiguration(playersFile);
    }

    public static void savePlayers() {
        try {
            getPlayers().save(getPlayersFile());
        } catch (Exception e) {
            Bukkit.getLogger().severe("Couldn't save players, because: " + e.getMessage());
        }
    }

    public static FileConfiguration getPlayers() {
        return players;
    }

    public static File getPlayersFile() {
        return playersFile;
    }

    public static void addPlayer(String team, String uuid) {
        if (!getPlayers().contains(uuid)) {
            getPlayers().set(uuid, team);
            savePlayers();
        }
    }

    public static String getTeamFromUUID(String uuid) {
        return getPlayers().getString(uuid);

    }
}