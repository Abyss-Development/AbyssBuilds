package net.abyssdev.abyssbuilds.objects;

import lombok.Getter;
import lombok.Setter;
import net.abyssdev.abyssbuilds.AbyssBuilds;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BuildPlayer {

    @Getter
    private static ArrayList<BuildPlayer> players = new ArrayList<>();

    private String name;
    private UUID uuid;
    private Type type;
    private HashSet<String> worlds;

    //New Player
    public BuildPlayer(Player p) {
        this.name = p.getName();
        this.uuid = p.getUniqueId();
        this.type = Type.PLAYER;
        this.worlds = new HashSet<>();
        players.add(this);

        AbyssBuilds.getInstance().getLuckPerms().getUserManager().modifyUser(p.getUniqueId(), user -> user.data().add(Node.builder("group." + this.type.name().toLowerCase()).build()));
    }

    public BuildPlayer(OfflinePlayer p) {
        this.name = p.getName();
        this.uuid = p.getUniqueId();
        this.type = Type.PLAYER;
        this.worlds = new HashSet<>();
        players.add(this);
    }

    //Existing Player
    public BuildPlayer(Player p, Type type, List<String> worlds) {
        this.name = p.getName();
        this.uuid = p.getUniqueId();
        this.type = type;
        this.worlds = worlds.equals("NULL") ? new HashSet<>() : new HashSet<>(worlds);
        players.add(this);
    }

    public BuildPlayer(OfflinePlayer p, Type type, List<String> worlds) {
        this.name = p.getName();
        this.uuid = p.getUniqueId();
        this.type = type;
        this.worlds = worlds.equals("NULL") ? new HashSet<>() : new HashSet<>(worlds);
        players.add(this);
    }

    public static BuildPlayer getByUUID(UUID uuid) {
        for (BuildPlayer buildPlayer : players) {
            if (buildPlayer.getUuid().equals(uuid)) {
                return buildPlayer;
            }
        }
        return null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.getUuid());
    }

    public static void remove(Player p) {
        players.removeIf(buildPlayer -> buildPlayer.getUuid().equals(p.getUniqueId()));
    }

    public static void remove(OfflinePlayer p) {
        players.removeIf(buildPlayer -> buildPlayer.getUuid().equals(p.getUniqueId()));
    }


    public enum Type {

        PLAYER(0),
        BUILDER(1),
        ADMIN(2);

        Type(int rank) {
            this.rank = rank;
        }

        @Getter
        private int rank;

    }
}
