package net.abyssdev.abyssbuilds.tasks;

import net.abyssdev.abyssbuilds.objects.BuildPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.abyssdev.abyssbuilds.AbyssBuilds;

public class LocationCheck {

    private AbyssBuilds plugin;

    public LocationCheck() {
        this.plugin = AbyssBuilds.getInstance();
        run();
    }

    public void run() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                BuildPlayer player = plugin.getPlayerData().getPlayer(p);
                if (p.getWorld().getName().equals("world") || player.getType().getRank() >= 2) continue;
                if (!player.getWorlds().contains(p.getWorld().getName())) {
                    p.teleport(plugin.getSpawn());
                    p.sendMessage(ChatColor.RED + "You are not whitelisted in this world.");
                }
            }
        }, 20L, 20L);
    }

}
