package net.abyssdev.abyssbuilds.listeners;

import net.abyssdev.abyssbuilds.AbyssBuilds;
import net.abyssdev.abyssbuilds.objects.BuildPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    private AbyssBuilds plugin;

    public PlayerListener() {
        this.plugin = AbyssBuilds.getInstance();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        BuildPlayer player = plugin.getPlayerData().getPlayer(p);
        if (!player.getWorlds().contains(p.getWorld().getName())) {
            p.teleport(plugin.getSpawn());
            p.sendMessage(ChatColor.RED + "You are not whitelisted in this world.");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        BuildPlayer buildPlayer = BuildPlayer.getByUUID(p.getUniqueId());
        if (buildPlayer != null) {
            plugin.getPlayerData().savePlayer(buildPlayer);
            BuildPlayer.remove(p);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player p = event.getPlayer();
        BuildPlayer player = BuildPlayer.getByUUID(p.getUniqueId());
        if (event.getTo().getWorld().getName().equalsIgnoreCase("world") || player.getType().getRank() >= 2) return;
        if (!player.getWorlds().contains(event.getTo().getWorld().getName())) {
            event.setCancelled(true);
            p.sendMessage(ChatColor.RED + "You are not whitelisted in this world.");
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
            BuildPlayer player = BuildPlayer.getByUUID(event.getPlayer().getUniqueId());
            if (player.getType().getRank() < 2) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
            BuildPlayer player = BuildPlayer.getByUUID(event.getPlayer().getUniqueId());
            if (player.getType().getRank() < 2) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void blockPlace(PlayerBucketEmptyEvent event) {
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
            BuildPlayer player = BuildPlayer.getByUUID(event.getPlayer().getUniqueId());
            if (player.getType().getRank() < 2) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        BuildPlayer player = BuildPlayer.getByUUID(event.getPlayer().getUniqueId());
        if (event.getMessage().contains("plugman") || event.getMessage().contains("/stop") || event.getMessage().contains("/restart") || event.getMessage().contains("/mv")) {
            if (player.getType().getRank() < 2) {
                player.getPlayer().sendMessage(ChatColor.RED + "Command disabled");
                event.setCancelled(true);
            }
        }
        if (event.getMessage().equalsIgnoreCase("/spawn")) {
            event.setMessage("/abyssbuilds spawn");
        }
    }
}
 