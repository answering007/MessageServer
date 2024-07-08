package com.pike.messageserver.data;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import com.pike.messageserver.results.GetBlockResult;

public class GetBlockData {
    /**
     * The name of the world
     */
    public String worldName;

    /**
     * The x coordinates of the block
     */
    public int x;

    /**
     * The y coordinates of the block
     */
    public int y;

    /**
     * The z coordinates of the block
     */
    public int z;

    @Override
    public String toString() {
        return "GetBlockData{" +
                "worldName='" + worldName + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    /**
     * Converts a GetBlockData object into a GetBlockResult object.
     *
     * @param  getBlock  the GetBlockData object to convert
     * @return            the resulting GetBlockResult object
     */
    public static GetBlockResult toGetBlockResult(GetBlockData getBlock) {
        GetBlockResult result = new GetBlockResult();

        // Fill generic data
        World world = Bukkit.getWorld(getBlock.worldName);
        Block block = world.getBlockAt(getBlock.x, getBlock.y, getBlock.z);
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
