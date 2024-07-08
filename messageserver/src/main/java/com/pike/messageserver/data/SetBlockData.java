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
     * The name of the world the block is in.
     */
    public String worldName;
    /**
     * The x coordinate of the block.
     */
    public int x;
    /**
     * The y coordinate of the block.
     */
    public int y;

    /**
     * The z coordinate of the block.
     */
    public int z;

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
                "worldName='" + worldName + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", blockData='" + blockData + '\'' +
                '}';
    }

    /**
     * Converts a SetBlockData object into a SetBlockResult object.
     *
     * @param  setBlock  the SetBlockData object to convert
     * @return            the resulting SetBlockResult object
     */
    public static SetBlockResult toSetBlockResult(SetBlockData setBlock) {
        
        SetBlockResult result = new SetBlockResult();

        // Fill main data
        World world = Bukkit.getWorld(setBlock.worldName);
        Block block = world.getBlockAt(setBlock.x, setBlock.y, setBlock.z);
        BlockData blockData = Bukkit.createBlockData(setBlock.blockData);
        block.setBlockData(blockData);

        // Fill ItemStack
        if (setBlock.items != null && !setBlock.items.isEmpty()) {
            BlockState blockState = block.getState();
            if (blockState instanceof org.bukkit.inventory.InventoryHolder) {
                org.bukkit.inventory.InventoryHolder inventoryHolder = (org.bukkit.inventory.InventoryHolder) blockState;

                // Create ItemStack
                for (ItemStackData itemStackData : setBlock.items) {
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
