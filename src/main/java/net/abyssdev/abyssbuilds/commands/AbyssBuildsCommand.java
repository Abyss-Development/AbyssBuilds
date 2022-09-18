package net.abyssdev.abyssbuilds.commands;

import com.google.common.collect.ImmutableList;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import net.abyssdev.abyssbuilds.objects.BuildPlayer;
import net.abyssdev.abyssbuilds.utils.ColorUtil;
import net.luckperms.api.node.Node;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.abyssdev.abyssbuilds.AbyssBuilds;
import net.abyssdev.abyssbuilds.commands.tabcompleters.AbyssBuildsTabCompleter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AbyssBuildsCommand implements CommandExecutor {

    private static final String[] HELP_MESSAGE = ImmutableList.of(
            ColorUtil.color("&3&m-------------------------------")
            , ColorUtil.color(" ")
            , ColorUtil.color("&b&lAbyssDev &8&l┃ &bBuilds Help")
            , ColorUtil.color("&7&oCredit to &f&ojacob#0001&7&o.")
            , ColorUtil.color(" ")
            , ColorUtil.color("&7<> = Required")
            , ColorUtil.color("&7[] = Optional")
            , ColorUtil.color(" ")
            , ColorUtil.color("&3&l» &b/abyssbuilds spawn")
            , ColorUtil.color("&3&l» &b/abyssbuilds rank")
            , ColorUtil.color("&3&l» &b/abyssbuilds list [player]")
            , ColorUtil.color("&3&l» &b/abyssbuilds teleport")
            , ColorUtil.color("&3&l» &b/abyssbuilds createworld <name/seed> [flat]")
            , ColorUtil.color("&3&l» &b/abyssbuilds zipworld <world>")
            , ColorUtil.color("&3&l» &b/abyssbuilds addworld <player> <name/seed>")
            , ColorUtil.color("&3&l» &b/abyssbuilds removeworld <player> <name/see>")
            , ColorUtil.color("&3&l» &b/abyssbuilds settype <player <type>")
            , ColorUtil.color("&3&l» &b/abyssbuilds drawborder")
            , ColorUtil.color(" ")
            , ColorUtil.color("&3&m------------------------------")
    ).toArray(new String[0]);

    private final List<String> worlds = new LinkedList<>();

    private final MVWorldManager manager;
    private final AbyssBuilds plugin;

    public AbyssBuildsCommand() {
        this.plugin = AbyssBuilds.getInstance();
        plugin.getCommand("abyssbuilds").setTabCompleter(new AbyssBuildsTabCompleter());
        this.manager = ((MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core")).getMVWorldManager();
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length > 0) {
            BuildPlayer buildPlayer = null;
            if (sender instanceof Player) {
                buildPlayer = BuildPlayer.getByUUID(((Player) sender).getUniqueId());
            }
            if (args[0].equalsIgnoreCase("spawn")) {
                if (buildPlayer == null) {
                    sender.sendMessage(ChatColor.RED + "Only players may use this command");
                    return true;
                }
                ((Player) sender).teleport(plugin.getSpawn());
                sender.sendMessage(ChatColor.GREEN + "Teleported to spawn");
                return true;
            } else if (args[0].equalsIgnoreCase("rank")) {
                if (buildPlayer == null) {
                    sender.sendMessage(ChatColor.RED + "Only players may use this command");
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "Your rank is " + buildPlayer.getType().name());
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                if (buildPlayer == null) {
                    sender.sendMessage(ChatColor.RED + "Only players may use this command");
                    return true;
                }
                if (args.length > 1) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    BuildPlayer listing = plugin.getPlayerData().getPlayer(player);
                    sender.sendMessage(ColorUtil.color("&b&l" + listing.getPlayer().getName() + "'s worlds:"));
                    if (listing.getWorlds().isEmpty()) {
                        sender.sendMessage(ColorUtil.color("&7&oNone"));
                    } else {
                        for (String world : listing.getWorlds()) {
                            sender.sendMessage(ColorUtil.color("&f- &7" + world));
                        }
                    }
                    return true;
                } else {
                    sender.sendMessage(ColorUtil.color("&b&lYour worlds:"));
                    if (buildPlayer.getWorlds().isEmpty()) {
                        sender.sendMessage(ColorUtil.color("&7&oNone"));
                    } else {
                        for (String world : buildPlayer.getWorlds()) {
                            sender.sendMessage(ColorUtil.color("&f- &7" + world));
                        }
                    }
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                if (buildPlayer == null) {
                    sender.sendMessage(ChatColor.RED + "Only players may use this command");
                    return true;
                }
                String world = args[1];
                boolean passed = false;
                if (buildPlayer.getType().getRank() < 2) {
                    for (String w : buildPlayer.getWorlds()) {
                        if (w.equalsIgnoreCase(world)) {
                            passed = true;
                            break;
                        }
                    }
                } else {
                    passed = true;
                }
                if (passed) {
                    try {
                        World w = Bukkit.getWorld(args[1]);

                        if (w == null) {
                            if (!this.manager.isMVWorld(world)) {
                                sender.sendMessage(ChatColor.RED + "World is not a Multiverse-Core world or a normal world");
                                return true;
                            }

                            this.manager.loadWorld(world);

                            final MultiverseWorld multiverse = this.manager.getMVWorld(world);

                            buildPlayer.getPlayer().teleport(this.getNearestSafeLocation(multiverse.getSpawnLocation()));
                            sender.sendMessage(ChatColor.GREEN + "Teleported to " + args[1]);
                            return true;
                        }

                        buildPlayer.getPlayer().teleport(this.getNearestSafeLocation(w.getSpawnLocation()));
                        sender.sendMessage(ChatColor.GREEN + "Teleported to " + args[1]);
                        return true;
                    } catch (Exception ex) {
                        sender.sendMessage(ChatColor.RED + "There was en error while teleporting you to " + args[1]);
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You aren't whitelisted on " + args[1]);
                    return true;
                }

            } else if (args[0].equalsIgnoreCase("import")) {
                if (buildPlayer == null) {
                    sender.sendMessage(ChatColor.RED + "Only players may use this command");
                    return true;
                }

                String world = args[1];

                if (buildPlayer.getType().getRank() > 2) {

                    if (!this.manager.isMVWorld(world)) {
                        sender.sendMessage(ChatColor.RED + "World is not a Multiverse-Core world");
                        return true;
                    }

                    this.manager.loadWorld(world);

                    this.worlds.add(world);


                } else {
                    sender.sendMessage(ChatColor.RED + "You aren't allowed to import worlds");
                    return true;
                }

            } else if (args[0].equalsIgnoreCase("createworld")) {
                if (buildPlayer != null) {
                    if (buildPlayer.getType().getRank() < 2) {
                        sender.sendMessage(ColorUtil.color("&cYou aren't allowed to create worlds"));
                        return true;
                    }
                }
                if (args.length > 1) {
                    try {
                        long seed = Long.parseLong(args[1]);
                        for (World w : Bukkit.getWorlds()) {
                            if (w.getName().equalsIgnoreCase(args[1])) {
                                sender.sendMessage(ColorUtil.color("&cA world with this name already exists"));
                                return true;
                            }
                            if (w.getSeed() == seed) {
                                sender.sendMessage(ColorUtil.color("&cA world with this seed already exists"));
                                return true;
                            }
                        }
                        boolean flat = false;
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("-f") || args[2].equalsIgnoreCase("f") || args[2].equalsIgnoreCase("flat") || args[2].equalsIgnoreCase("-flat")) {
                                flat = true;
                            }
                        }
                        sender.sendMessage(ColorUtil.color("&aCreating" + (flat ? " flat" : "") + " world with seed " + seed + " and name " + args[1]));
                        createWorld(buildPlayer, seed, args[1], flat);
                        return true;
                    } catch (Exception ex) {
                        for (World w : Bukkit.getWorlds()) {
                            if (w.getName().equalsIgnoreCase(args[1])) {
                                sender.sendMessage(ColorUtil.color("&cA world with this name already exists"));
                                return true;
                            }
                        }
                        boolean flat = false;
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("-f") || args[2].equalsIgnoreCase("f") || args[2].equalsIgnoreCase("flat") || args[2].equalsIgnoreCase("-flat")) {
                                flat = true;
                            }
                        }
                        sender.sendMessage(ColorUtil.color("&aCreating" + (flat ? " flat" : "") + " world with name " + args[1]));
                        createWorld(buildPlayer, null, args[1], flat);
                        return true;
                    }
                } else {
                    sender.sendMessage(ColorUtil.color("&cYou need to specify a world name or seed (/abyssbuilds createworld <name/seed>)"));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("zipworld")) {
                if (buildPlayer != null) {
                    if (buildPlayer.getType().getRank() < 2) {
                        sender.sendMessage(ColorUtil.color("&cYou aren't allowed to zip worlds"));
                        return true;
                    }
                }
                if (args.length > 1) {

                    String world = args[1];

                    for (World w : Bukkit.getWorlds()) {
                        if (w.getName().equalsIgnoreCase(world)) {
                            sender.sendMessage(ColorUtil.color("&aZipping world " + args[1] + "..."));
                            boolean success = zipWorld(args[1]);
                            if (success) {
                                sender.sendMessage(ColorUtil.color("&aZipped world " + args[1]));
                            } else {
                                sender.sendMessage(ColorUtil.color("&cThere was an error zipping world " + args[1]));
                            }
                            return true;
                        }
                    }

                    for (final String name : this.worlds) {

                        if (!name.equalsIgnoreCase(world)) {
                            continue;
                        }

                        sender.sendMessage(ColorUtil.color("&aZipping world " + args[1] + "..."));
                        boolean success = zipMultiverseWorld(args[1]);
                        if (success) {
                            sender.sendMessage(ColorUtil.color("&aZipped world " + args[1]));
                        } else {
                            sender.sendMessage(ColorUtil.color("&cThere was an error zipping world " + args[1]));
                        }
                        return true;
                    }
                    sender.sendMessage(ColorUtil.color("&cThat world does not exist"));
                    return true;

                } else {
                    sender.sendMessage(ColorUtil.color("&cYou need to specify a world name (/abyssbuilds zipworld <world>)"));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("addworld")) {
                if (buildPlayer != null) {
                    if (buildPlayer.getType().getRank() < 1) {
                        sender.sendMessage(ColorUtil.color("&cYou aren't allowed to add players to worlds"));
                        return true;
                    }
                }
                if (args.length > 2) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    BuildPlayer adding = plugin.getPlayerData().getPlayer(player);
                    try {

                        long seed = Long.parseLong(args[2]);

                        for (World w : Bukkit.getWorlds()) {
                            if (w.getName().equalsIgnoreCase(args[2])) {
                                if (buildPlayer != null && buildPlayer.getType().getRank() < 2 && !buildPlayer.getWorlds().contains(w.getName())) {
                                    sender.sendMessage(ColorUtil.color("&cYou don't have access to this world"));
                                    return true;
                                }
                                sender.sendMessage(ColorUtil.color("&aAdded " + player.getName() + " to " + w.getName()));
                                adding.getWorlds().add(args[2]);
                                plugin.getPlayerData().savePlayer(adding);
                                if (!player.isOnline()) {
                                    BuildPlayer.remove(player);
                                }
                                return true;
                            }
                            if (w.getSeed() == seed) {
                                if (buildPlayer != null && buildPlayer.getType().getRank() < 2 && !buildPlayer.getWorlds().contains(w.getName())) {
                                    sender.sendMessage(ColorUtil.color("&cYou don't have access to this world"));
                                    return true;
                                }
                                sender.sendMessage(ColorUtil.color("&aAdded " + player.getName() + " to " + w.getName()));
                                adding.getWorlds().add(args[2]);
                                plugin.getPlayerData().savePlayer(adding);
                                if (!player.isOnline()) {
                                    BuildPlayer.remove(player);
                                }
                                return true;
                            }
                        }

                        for (final String world : this.worlds) {

                            final MultiverseWorld w = this.manager.getMVWorld(world);

                            if (world.equalsIgnoreCase(args[2])) {

                                if (buildPlayer != null && buildPlayer.getType().getRank() < 2 && !buildPlayer.getWorlds().contains(w.getName())) {
                                    sender.sendMessage(ColorUtil.color("&cYou don't have access to this world"));
                                    return true;
                                }
                                sender.sendMessage(ColorUtil.color("&aAdded " + player.getName() + " to " + w.getName()));
                                adding.getWorlds().add(args[2]);
                                plugin.getPlayerData().savePlayer(adding);
                                if (!player.isOnline()) {
                                    BuildPlayer.remove(player);
                                }
                                return true;

                            }

                            if (w.getSeed() == seed) {

                                if (buildPlayer != null && buildPlayer.getType().getRank() < 2 && !buildPlayer.getWorlds().contains(w.getName())) {
                                    sender.sendMessage(ColorUtil.color("&cYou don't have access to this world"));
                                    return true;
                                }
                                sender.sendMessage(ColorUtil.color("&aAdded " + player.getName() + " to " + w.getName()));
                                adding.getWorlds().add(args[2]);
                                plugin.getPlayerData().savePlayer(adding);
                                if (!player.isOnline()) {
                                    BuildPlayer.remove(player);
                                }
                                return true;

                            }

                        }

                        sender.sendMessage(ColorUtil.color("&cThat world does not exist"));
                        return true;
                    } catch (Exception ex) {
                        for (World w : Bukkit.getWorlds()) {
                            if (w.getName().equalsIgnoreCase(args[2])) {
                                if (buildPlayer != null && buildPlayer.getType().getRank() < 2 && !buildPlayer.getWorlds().contains(w.getName())) {
                                    sender.sendMessage(ColorUtil.color("&cYou don't have access to this world"));
                                    return true;
                                }
                                sender.sendMessage(ColorUtil.color("&aAdded " + player.getName() + " to " + w.getName()));
                                adding.getWorlds().add(args[2]);
                                plugin.getPlayerData().savePlayer(adding);
                                if (!player.isOnline()) {
                                    BuildPlayer.remove(player);
                                }
                                return true;
                            }
                        }
                        sender.sendMessage(ColorUtil.color("&cThat world does not exist"));
                        return true;
                    }
                } else {
                    sender.sendMessage(ColorUtil.color("&cYou need to specify a world and player (/abyssbuilds addworld <player> <name/seed>)"));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("removeworld")) {
                if (buildPlayer != null) {
                    if (buildPlayer.getType().getRank() < 1) {
                        sender.sendMessage(ColorUtil.color("&cYou aren't allowed to remove players from worlds"));
                        return true;
                    }
                }
                if (args.length > 2) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    BuildPlayer removing = plugin.getPlayerData().getPlayer(player);
                    try {

                        long seed = Long.parseLong(args[2]);

                        for (World w : Bukkit.getWorlds()) {
                            if (w.getName().equalsIgnoreCase(args[2])) {
                                if (buildPlayer != null && buildPlayer.getType().getRank() < 2 && !buildPlayer.getWorlds().contains(w.getName())) {
                                    sender.sendMessage(ColorUtil.color("&cYou don't have access to this world"));
                                    return true;
                                }
                                if (!removing.getWorlds().contains(args[2])) {
                                    sender.sendMessage(ColorUtil.color("&c" + player.getName() + " doesn't have access to " + w.getName()));
                                    return true;
                                }
                                sender.sendMessage(ColorUtil.color("&cRemoved " + player.getName() + " from " + w.getName()));
                                removing.getWorlds().remove(args[2]);
                                plugin.getPlayerData().savePlayer(removing);
                                if (!player.isOnline()) {
                                    BuildPlayer.remove(player);
                                }
                                return true;
                            }
                            if (w.getSeed() == seed) {
                                if (buildPlayer != null && buildPlayer.getType().getRank() < 2 && !buildPlayer.getWorlds().contains(w.getName())) {
                                    sender.sendMessage(ColorUtil.color("&cYou don't have access to this world"));
                                    return true;
                                }
                                if (!removing.getWorlds().contains(args[2])) {
                                    sender.sendMessage(ColorUtil.color("&c" + player.getName() + " doesn't have access to " + w.getName()));
                                    return true;
                                }
                                sender.sendMessage(ColorUtil.color("&cRemoved " + player.getName() + " from " + w.getName()));
                                removing.getWorlds().remove(args[2]);
                                plugin.getPlayerData().savePlayer(removing);
                                if (!player.isOnline()) {
                                    BuildPlayer.remove(player);
                                }
                                return true;
                            }
                        }

                        for (final String world : this.worlds) {

                            final MultiverseWorld w = this.manager.getMVWorld(world);

                            if (world.equalsIgnoreCase(args[2])) {

                                if (buildPlayer != null && buildPlayer.getType().getRank() < 2 && !buildPlayer.getWorlds().contains(w.getName())) {
                                    sender.sendMessage(ColorUtil.color("&cYou don't have access to this world"));
                                    return true;
                                }
                                if (!removing.getWorlds().contains(args[2])) {
                                    sender.sendMessage(ColorUtil.color("&c" + player.getName() + " doesn't have access to " + w.getName()));
                                    return true;
                                }
                                sender.sendMessage(ColorUtil.color("&cRemoved " + player.getName() + " from " + w.getName()));
                                removing.getWorlds().remove(args[2]);
                                plugin.getPlayerData().savePlayer(removing);
                                if (!player.isOnline()) {
                                    BuildPlayer.remove(player);
                                }
                                return true;

                            }

                            if (w.getSeed() == seed) {

                                if (buildPlayer != null && buildPlayer.getType().getRank() < 2 && !buildPlayer.getWorlds().contains(w.getName())) {
                                    sender.sendMessage(ColorUtil.color("&cYou don't have access to this world"));
                                    return true;
                                }
                                if (!removing.getWorlds().contains(args[2])) {
                                    sender.sendMessage(ColorUtil.color("&c" + player.getName() + " doesn't have access to " + w.getName()));
                                    return true;
                                }
                                sender.sendMessage(ColorUtil.color("&cRemoved " + player.getName() + " from " + w.getName()));
                                removing.getWorlds().remove(args[2]);
                                plugin.getPlayerData().savePlayer(removing);
                                if (!player.isOnline()) {
                                    BuildPlayer.remove(player);
                                }
                                return true;

                            }

                        }

                        sender.sendMessage(ColorUtil.color("&cThat world does not exist"));
                        return true;
                    } catch (Exception ex) {
                        for (World w : Bukkit.getWorlds()) {
                            if (w.getName().equalsIgnoreCase(args[2])) {
                                if (buildPlayer != null && buildPlayer.getType().getRank() < 2 && !buildPlayer.getWorlds().contains(w.getName())) {
                                    sender.sendMessage(ColorUtil.color("&cYou don't have access to this world"));
                                    return true;
                                }
                                if (!removing.getWorlds().contains(args[2])) {
                                    sender.sendMessage(ColorUtil.color("&c" + player.getName() + " doesn't have access to " + w.getName()));
                                    return true;
                                }
                                sender.sendMessage(ColorUtil.color("&cRemoved " + player.getName() + " from " + w.getName()));
                                removing.getWorlds().remove(args[2]);
                                plugin.getPlayerData().savePlayer(removing);
                                if (!player.isOnline()) {
                                    BuildPlayer.remove(player);
                                }
                                return true;
                            }
                        }
                        sender.sendMessage(ColorUtil.color("&cThat world does not exist"));
                        return true;
                    }
                } else {
                    sender.sendMessage(ColorUtil.color("&cYou need to specify a world and player (/abyssbuilds addworld <player> <name/seed>)"));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("settype")) {
                if (buildPlayer != null) {
                    if (buildPlayer.getType().getRank() < 2) {
                        sender.sendMessage(ColorUtil.color("&cYou aren't allowed to change player types"));
                        return true;
                    }
                }
                if (args.length > 2) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    BuildPlayer changing = plugin.getPlayerData().getPlayer(player);
                    try {
                        BuildPlayer.Type type = BuildPlayer.Type.valueOf(args[2]);
                        changing.setType(type);

                        this.plugin.getLuckPerms().getUserManager().modifyUser(player.getUniqueId(), user -> user.data().add(Node.builder("group." + type.name().toLowerCase()).build()));

                        sender.sendMessage(ColorUtil.color("&aSet " + player.getName() + " to type " + type.name()));
                        plugin.getPlayerData().savePlayer(changing);
                        if (!player.isOnline()) {
                            BuildPlayer.remove(player);
                        }
                        return true;
                    } catch (Exception ex) {
                        sender.sendMessage(ColorUtil.color("&cThat type does not exist"));
                        return true;
                    }
                } else {
                    sender.sendMessage(ColorUtil.color("&cYou need to specify a type and a player (/abyssbuilds settype <player> <type>)"));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("drawborder")) {
                if (buildPlayer != null) {
                    if (buildPlayer.getType().getRank() < 2) {
                        sender.sendMessage(ColorUtil.color("&cYou aren't allowed to draw map borders"));
                        return true;
                    }
                }
                Location spawn = buildPlayer.getPlayer().getWorld().getSpawnLocation();
                spawn.setY(135);
                Location current = spawn.clone(); //Start in south east corner
                current.setX(spawn.getX() + 192);
                current.setZ(spawn.getZ() + 192);

                for (int i = 0; i < 192 + 177; i++) { //Move to north east
                    current.getBlock().setType(Material.RED_WOOL);
                    Block current2 = current.getBlock();
                    for (int j = 0; j < 5; j++) {
                        current2 = current2.getRelative(BlockFace.DOWN);
                        current2.setType(Material.RED_WOOL);
                    }
                    current.setZ(current.getZ() - 1);
                }
                for (int i = 0; i < 192 + 177; i++) { //Move to north west
                    current.getBlock().setType(Material.RED_WOOL);
                    Block current2 = current.getBlock();
                    for (int j = 0; j < 5; j++) {
                        current2 = current2.getRelative(BlockFace.DOWN);
                        current2.setType(Material.RED_WOOL);
                    }
                    current.setX(current.getX() - 1);
                }
                for (int i = 0; i < 177 + 192; i++) { //Move to south west
                    current.getBlock().setType(Material.RED_WOOL);
                    Block current2 = current.getBlock();
                    for (int j = 0; j < 5; j++) {
                        current2 = current2.getRelative(BlockFace.DOWN);
                        current2.setType(Material.RED_WOOL);
                    }
                    current.setZ(current.getZ() + 1);
                }
                for (int i = 0; i < 177 + 192; i++) { //Move back to south east
                    current.getBlock().setType(Material.RED_WOOL);
                    Block current2 = current.getBlock();
                    for (int j = 0; j < 5; j++) {
                        current2 = current2.getRelative(BlockFace.DOWN);
                        current2.setType(Material.RED_WOOL);
                    }
                    current.setX(current.getX() + 1);
                }
                sender.sendMessage(ChatColor.GREEN + "NFT border drawn with red wool");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid syntax");
                return true;
            }
        } else {
            sender.sendMessage(AbyssBuildsCommand.HELP_MESSAGE);
            return true;
        }

        return true;
    }

    public void createWorld(BuildPlayer buildPlayer, Long seed, String name, boolean flat) {

        if (seed == null) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (flat ? ("mv create " + name + " normal -t flat") : ("mv create " + name + " normal")));
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (flat ? ("mv create " + name + " normal -s " + seed + " -t flat") : ("mv create " + name + " normal -s " + seed)));
        }

        if (buildPlayer != null) {
            buildPlayer.getWorlds().add(name);
            plugin.getPlayerData().savePlayer(buildPlayer);
            if (!buildPlayer.getPlayer().isOnline()) {
                BuildPlayer.remove(buildPlayer.getPlayer());
            }
        }

    }

    public boolean zipWorld(String name) {

        World w = Bukkit.getWorld(name);

        try {
            File dir = w.getWorldFolder();
            List<String> filesListInDir = new ArrayList<String>();
            populateFilesList(dir, filesListInDir);

            FileOutputStream fos = new FileOutputStream(new File(plugin.getServer().getWorldContainer(), w.getName() + ".zip"));
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (String filePath : filesListInDir) {
                plugin.getLogger().info("Zipping " + filePath);
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
            plugin.getLogger().info("Successfully built zip!");
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Could not zip world: " + e.getClass().getSimpleName());
            e.printStackTrace();
            return false;
        }

    }

    public boolean zipMultiverseWorld(String name) {

        if (!this.manager.isMVWorld(name)) {
            return false;
        }

        this.manager.loadWorld(name);

        final MultiverseWorld world = manager.getMVWorld(name);

        try {
            File dir = world.getCBWorld().getWorldFolder();
            List<String> filesListInDir = new ArrayList<String>();
            populateFilesList(dir, filesListInDir);

            FileOutputStream fos = new FileOutputStream(new File(plugin.getServer().getWorldContainer(), world.getName() + ".zip"));
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (String filePath : filesListInDir) {
                plugin.getLogger().info("Zipping " + filePath);
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
            plugin.getLogger().info("Successfully built zip!");
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Could not zip world: " + e.getClass().getSimpleName());
            e.printStackTrace();
            return false;
        }

    }

    private Location getNearestSafeLocation(final Location location) {

        if (this.isSafeLocation(location) == 0) {
            return location;
        }

        final Location clone = location.clone();

        while (this.isSafeLocation(clone) != 0) {

            if (this.isSafeLocation(clone) == 0) {
                return location;
            }

            clone.add(0, -1, 0);

        }

        return clone;
    }

    private int isSafeLocation(final Location location) {

        final Block feet = location.getBlock();

        if (!feet.getType().isTransparent() && !feet.getLocation().add(0, 1, 0).getBlock().getType().isTransparent()) {
            return 1;
        }

        final Block head = feet.getRelative(BlockFace.UP);

        if (!head.getType().isTransparent()) {
            return 1;
        }

        final Block ground = feet.getRelative(BlockFace.DOWN);

        return ground.getType().isSolid() ? 0 : -1;
    }

    public void populateFilesList(File dir, List<String> filesListInDir) {
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isFile()) filesListInDir.add(f.getAbsolutePath());
            else populateFilesList(f, filesListInDir);
        }
    }
}