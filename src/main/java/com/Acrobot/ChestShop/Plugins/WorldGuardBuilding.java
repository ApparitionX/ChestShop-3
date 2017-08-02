package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.Protection.BuildPermissionEvent;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

import static com.Acrobot.ChestShop.Towny.TownyUtils.isInWilderness;
import static com.Acrobot.ChestShop.Towny.TownyUtils.isInsideShopPlot;
import static com.Acrobot.ChestShop.Towny.TownyUtils.isPlotOwner;
import static com.Acrobot.ChestShop.Towny.TownyUtils.isResident;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class WorldGuardBuilding implements Listener {
    private WorldGuardPlugin worldGuard;

    public WorldGuardBuilding(WorldGuardPlugin plugin) {
        this.worldGuard = plugin;
    }

    //LegendsMC
    @EventHandler
    public void canBuild(BuildPermissionEvent event) {
    	Location chest = event.getChest();
        Location sign = event.getSign();

        if (Permission.has(event.getPlayer(), Permission.PROTECTION_BYPASS)) {
            event.allow();
        }

        if (isInWilderness(chest, sign) || (Properties.BUILDING_INSIDE_SHOP_PLOTS && !isInsideShopPlot(chest, sign))) {
            ApplicableRegionSet regions = getApplicableRegions(event.getSign().getBlock().getLocation());

            if (Properties.WORLDGUARD_USE_FLAG) {
                event.allow(regions.allows(DefaultFlag.ENABLE_SHOP));
            } else {
                event.allow(regions.size() != 0);
            }
            return;
        }

        boolean allow;

        if (Properties.SHOPS_FOR_OWNERS_ONLY) {
            allow = isPlotOwner(event.getPlayer(), chest, sign);
        } else {
            allow = isPlotOwner(event.getPlayer(), chest, sign) || isResident(event.getPlayer(), chest, sign);
        }

        event.allow(allow);
    }

    private ApplicableRegionSet getApplicableRegions(Location location) {
        return worldGuard.getGlobalRegionManager().get(location.getWorld()).getApplicableRegions(BukkitUtil.toVector(location));
    }
}
