package net.abyssdev.abyssbuilds.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class WorldListener implements Listener {

    /**
     * Disable sheep eating grass it ruins the worlds
     **/
    @EventHandler
    public void sheepEat(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.SHEEP || event.getEntityType() == EntityType.ENDERMAN) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevent fire spread and fire destroy
     */
    @EventHandler
    public void fireSpread(BlockSpreadEvent event) {
        if (event.getSource().getType() == Material.FIRE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }
}
