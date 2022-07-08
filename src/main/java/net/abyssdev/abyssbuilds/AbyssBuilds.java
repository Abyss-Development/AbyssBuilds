package net.abyssdev.abyssbuilds;

import lombok.Getter;
import lombok.Setter;
import net.abyssdev.abyssbuilds.commands.AbyssBuildsCommand;
import net.abyssdev.abyssbuilds.data.PlayerData;
import net.abyssdev.abyssbuilds.objects.BuildPlayer;
import net.abyssdev.abyssbuilds.tasks.LocationCheck;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import net.abyssdev.abyssbuilds.listeners.PlayerListener;
import net.abyssdev.abyssbuilds.listeners.WorldListener;

@Getter
@Setter
public class AbyssBuilds extends JavaPlugin {

    private static AbyssBuilds plugin;

    private Location spawn;
    private PlayerData playerData;

    public void onEnable() {
        plugin = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        playerData = new PlayerData();

        spawn = new Location(Bukkit.getWorld("world"), 33.5, 138, -8, 180, 0);
        new LocationCheck();

        registerEvents();
        registerCommands();

        for (Player p : Bukkit.getOnlinePlayers()) {
            playerData.getPlayer(p);
        }

        Bukkit.getServer().getConsoleSender().sendMessage("AbyssBuilds has been enabled");
    }

    public void onDisable() {
        plugin = null;

        for (Player p : Bukkit.getOnlinePlayers()) {
            BuildPlayer buildPlayer = BuildPlayer.getByUUID(p.getUniqueId());
            if (buildPlayer != null) {
                playerData.savePlayer(buildPlayer);
                BuildPlayer.remove(p);
            }
        }

        Bukkit.getServer().getConsoleSender().sendMessage("AbyssBuilds has been disabled");
    }

    public void registerEvents() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new PlayerListener(), this);
        manager.registerEvents(new WorldListener(), this);
    }

    public void registerCommands() {
        getCommand("abyssbuilds").setExecutor(new AbyssBuildsCommand());
    }

    public static AbyssBuilds getInstance() {
        return plugin;
    }

}
