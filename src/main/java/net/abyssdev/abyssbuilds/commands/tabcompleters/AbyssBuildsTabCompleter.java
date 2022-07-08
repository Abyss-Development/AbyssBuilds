package net.abyssdev.abyssbuilds.commands.tabcompleters;

import net.abyssdev.abyssbuilds.objects.BuildPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import net.abyssdev.abyssbuilds.AbyssBuilds;

import java.util.ArrayList;
import java.util.List;

public class AbyssBuildsTabCompleter implements TabCompleter {

    private AbyssBuilds plugin;

    public AbyssBuildsTabCompleter() {
        this.plugin = AbyssBuilds.getInstance();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            BuildPlayer player = BuildPlayer.getByUUID(((Player) sender).getUniqueId());
            List<String> subCommands = new ArrayList<>();
            if (args.length == 1) {
                if ("list".startsWith(args[0]) || args[0].isEmpty()) {
                    subCommands.add("list");
                }
                if ("tp".startsWith(args[0]) || args[0].isEmpty()) {
                    subCommands.add("tp");
                }
                if ("teleport".startsWith(args[0]) || args[0].isEmpty()) {
                    subCommands.add("teleport");
                }
                if ("createworld".startsWith(args[0]) || args[0].isEmpty()) {
                    subCommands.add("createworld");
                }
                if ("addworld".startsWith(args[0]) || args[0].isEmpty()) {
                    subCommands.add("addworld");
                }
                if ("removeworld".startsWith(args[0]) || args[0].isEmpty()) {
                    subCommands.add("removeworld");
                }
                if ("settype".startsWith(args[0]) || args[0].isEmpty()) {
                    subCommands.add("settype");
                }
                if ("spawn".startsWith(args[0]) || args[0].isEmpty()) {
                    subCommands.add("spawn");
                }
                if ("rank".startsWith(args[0]) || args[0].isEmpty()) {
                    subCommands.add("rank");
                }
                if ("zipworld".startsWith(args[0]) || args[0].isEmpty()) {
                    subCommands.add("zipworld");
                }
                if ("drawborder".startsWith(args[0]) || args[0].isEmpty()) {
                    subCommands.add("drawborder");
                }
                return subCommands;
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                    if (player.getType().getRank() >= 2) {
                        for (World w : Bukkit.getWorlds()) {
                            if (w.getName().startsWith(args[1]) || args[1].isEmpty()) {
                                subCommands.add(w.getName());
                            }
                        }
                    } else {
                        for (String s : player.getWorlds()) {
                            if (s.startsWith(args[1]) || args[1].isEmpty()) {
                                subCommands.add(s);
                            }
                        }
                    }
                    return subCommands;
                } else if (args[0].equalsIgnoreCase("zipworld")) {
                    if (player.getType().getRank() >= 2) {
                        for (World w : Bukkit.getWorlds()) {
                            if (w.getName().startsWith(args[1]) || args[1].isEmpty()) {
                                subCommands.add(w.getName());
                            }
                        }
                    }
                    return subCommands;
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("addworld")) {
                    if (player.getType().getRank() >= 2) {
                        for (World w : Bukkit.getWorlds()) {
                            if (w.getName().startsWith(args[2]) || args[2].isEmpty()) {
                                subCommands.add(w.getName());
                            }
                        }
                    } else {
                        for (String s : player.getWorlds()) {
                            if (s.startsWith(args[2]) || args[2].isEmpty()) {
                                subCommands.add(s);
                            }
                        }
                    }
                    return subCommands;
                } else if (args[0].equalsIgnoreCase("removeworld")) {
                    String removing = args[1];
                    BuildPlayer buildPlayer;
                    if (Bukkit.getPlayerExact(removing) == null) {
                        buildPlayer = plugin.getPlayerData().getPlayer(Bukkit.getOfflinePlayer(removing));
                    } else {
                        buildPlayer = plugin.getPlayerData().getPlayer(Bukkit.getPlayer(removing));
                    }
                    if (player.getType().getRank() >= 2) { //Can remove anyone from any world
                        for (String w : buildPlayer.getWorlds()) {
                            if (w.startsWith(args[2]) || args[2].isEmpty()) {
                                subCommands.add(w);
                            }
                        }
                    } else if (player.getType().getRank() >= 1) { //Can remove players from worlds they are in
                        for (String s : buildPlayer.getWorlds()) {
                            if (player.getWorlds().contains(s) && (s.startsWith(args[2]) || args[2].isEmpty())) {
                                subCommands.add(s);
                            }
                        }
                    }
                    return subCommands;
                } else if (args[0].equalsIgnoreCase("settype")) {
                    if (player.getType().getRank() >= 2) {
                        for (BuildPlayer.Type type : BuildPlayer.Type.values()) {
                            if (type.name().startsWith(args[2]) || args[2].isEmpty()) {
                                subCommands.add(type.name());
                            }
                        }
                        return subCommands;
                    }
                }
            }
        }
        return null;
    }
}
