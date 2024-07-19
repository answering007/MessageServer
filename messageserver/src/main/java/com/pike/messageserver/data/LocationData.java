package com.pike.messageserver.data;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import com.pike.messageserver.results.GetBlockResult;

public class LocationData {
    /**
     * Name of the world
     */
    public String worldName;

    /**
     * X coordinate
     */
    public double x;

    /**
     * Y coordinate
     */
    public double y;

    /**
     * Z coordinate
     */
    public double z;

    /**
     * Yaw
     */
    public double yaw;

    /**
     * Pitch
     */
    public double pitch;

    @Override
    public String toString() {
        return "LocationData{" + "world='" + worldName + '\'' + ", x=" + x + ", y=" + y + ", z=" + z + ", yaw=" + yaw + ", pitch=" + pitch + '}';
    }

    /**
     * Creates a LocationData object from a given Location object.
     *
     * @param  location    the Location object to create LocationData from
     * @return             the LocationData object created from the Location object
     */
    public static LocationData fromLocation(Location location) {
        LocationData locationData = new LocationData();
        locationData.worldName = location.getWorld().getName();
        locationData.x = location.getX();
        locationData.y = location.getY();
        locationData.z = location.getZ();
        locationData.yaw = location.getYaw();
        locationData.pitch = location.getPitch();
        return locationData;
    }

    /**
     * Creates a Location object from the world name, x, y, and z coordinates.
     *
     * @return          the Location object created
     */
    public Location toLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    /**
     * Converts the data of a block into a GetBlockResult object.
     *
     * @return          the resulting GetBlockResult object
     */
    public GetBlockResult toGetBlockResult() {
        GetBlockResult result = new GetBlockResult();

        // Fill generic data
        World world = Bukkit.getWorld(worldName);
        Block block = world.getBlockAt(toLocation());
        result.result = block.getBlockData().getAsString();

        // Fill ItemStack
        BlockState blockState = block.getState();
        if (blockState instanceof org.bukkit.inventory.InventoryHolder) {
            org.bukkit.inventory.InventoryHolder container = (org.bukkit.inventory.InventoryHolder) blockState;
            
            // Check if inventory is empty
            ItemStack[] contents = container.getInventory().getContents();
            if (contents == null) {
                return result;
            }

            result.items = new ArrayList<ItemStackData>();            
            for (int i = 0; i < contents.length; i++) {
                ItemStack itemStack = contents[i];
                if (itemStack == null) {
                    continue;
                }
                ItemStackData itemStackData = ItemStackData.fromItemStack(itemStack, i);
                result.items.add(itemStackData);
            }
        }

        result.success = true;
        return result;
    }
}
