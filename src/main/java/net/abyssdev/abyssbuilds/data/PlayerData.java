package net.abyssdev.abyssbuilds.data;

import net.abyssdev.abyssbuilds.objects.BuildPlayer;
import net.abyssdev.abyssbuilds.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import net.abyssdev.abyssbuilds.AbyssBuilds;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

public class PlayerData {

    private AbyssBuilds plugin;

    public PlayerData() {
        this.plugin = AbyssBuilds.getInstance();
        File folder = new File(PlayerData.folder = (plugin.getDataFolder() + "/players/"));
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public static final String KICK_REASON = ColorUtil.color("&cYour player data could not be loaded, please try rejoining.");
    public static String folder;

    public void savePlayer(BuildPlayer player) {
        try {
            File playerFile = new File(folder + player.getUuid().toString() + ".yml");

            if (!playerFile.exists()) {
                playerFile.createNewFile();
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            config.set("name", player.getName());
            config.set("uuid", player.getUuid().toString());
            config.set("type", player.getType().name());
            config.set("worlds", !player.getWorlds().isEmpty() ? new ArrayList<>(player.getWorlds()) : new ArrayList<>());
            config.save(playerFile);
            Bukkit.getLogger().log(Level.INFO, "Saved player data for " + player.getName());
        } catch (Exception ex) {
            ex.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, "There was an issue saving player data for " + player.getName() + ":\n" +
                    "- Name: " + player.getName() + "\n" +
                    "- UUID: " + player.getUuid().toString() + "\n" +
                    "- Type: " + player.getType().name() + "\n" +
                    "- Worlds:" + (!player.getWorlds().isEmpty() ? String.join(",", player.getWorlds()) : "Empty"));
        }
    }

    public BuildPlayer getPlayer(Player player) {
        for (BuildPlayer existing : BuildPlayer.getPlayers()) {
            if (existing.getUuid() == player.getUniqueId()) {
                return existing;
            }
        }
        File playerFile = new File(folder + player.getUniqueId().toString() + ".yml");
        if (playerFile.exists()) {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
                BuildPlayer buildPlayer = new BuildPlayer(player, BuildPlayer.Type.valueOf(config.getString("type")), config.getStringList("worlds"));
                return buildPlayer;
            } catch (Exception ex) {
                ex.printStackTrace();
                Bukkit.getLogger().log(Level.SEVERE, "There was an issue loading player data for " + player.getName() + " from " + player.getUniqueId().toString() + ".yml");
                player.kickPlayer(ColorUtil.color(KICK_REASON));
                return null;
            }
        }
        return new BuildPlayer(player);
    }

    public BuildPlayer getPlayer(OfflinePlayer player) {
        for (BuildPlayer existing : BuildPlayer.getPlayers()) {
            if (existing.getUuid() == player.getUniqueId()) {
                return existing;
            }
        }
        File playerFile = new File(folder + player.getUniqueId().toString() + ".yml");
        if (playerFile.exists()) {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
                BuildPlayer buildPlayer = new BuildPlayer(player, BuildPlayer.Type.valueOf(config.getString("type")), config.getStringList("worlds"));
                return buildPlayer;
            } catch (Exception ex) {
                return null;
            }
        }
        return new BuildPlayer(player);
    }
}
