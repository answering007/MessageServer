package com.pike.messageserver.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import com.pike.messageserver.results.SetBlockResult;

public class SetBlockData {
    /**
     * The location data of the block.
     */
    public LocationData locationData;

    /**
     * The block data of the block.
     */
    public String blockData;
    /**
     * The items in the block.
     */
    public List<ItemStackData> items = new ArrayList<ItemStackData>();

    @Override
    public String toString() {
        return "SetBlockData{" +
                "locationData=" + locationData +
                ", blockData='" + blockData + '}';                
    }

    /**
     * Converts a SetBlockData object into a SetBlockResult object.
     *
     * @return            the resulting SetBlockResult object
     */
    public SetBlockResult toSetBlockResult() {
        
        SetBlockResult result = new SetBlockResult();

        // Fill main data
        World world = Bukkit.getWorld(locationData.worldName);
        Block block = world.getBlockAt(locationData.toLocation());
        BlockData blockData = Bukkit.createBlockData(this.blockData);
        block.setBlockData(blockData);

        // Fill ItemStack
        if (items != null && !items.isEmpty()) {
            BlockState blockState = block.getState();
            if (blockState instanceof org.bukkit.inventory.InventoryHolder) {
                org.bukkit.inventory.InventoryHolder inventoryHolder = (org.bukkit.inventory.InventoryHolder) blockState;

                // Create ItemStack
                for (ItemStackData itemStackData : items) {
                    ItemStack itemStack = itemStackData.toItemStack();
                    inventoryHolder.getInventory().setItem(itemStackData.Index, itemStack);
                }
                result.success = true;
            }
            else {
                result.success = false;
                result.exceptionMessage = "Block is not an inventory holder";
            }
        }
        else {
            result.success = true;
        }

        return result;
    }
}
