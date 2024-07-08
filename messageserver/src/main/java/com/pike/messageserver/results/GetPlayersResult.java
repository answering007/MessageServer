package com.pike.messageserver.results;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.pike.messageserver.data.ItemStackData;
import com.pike.messageserver.data.LocationData;

public class GetPlayersResult {
    /**
     * The name of the player
     */
    public String playerName;
    /**
     * Whether or not the player is online
     */
    public boolean isOnline;
    /**
     * The UUID of the player
     */
    public String UUID;
    /**
     * Socket address of the player
     */
    public String address;
    /**
     * The location of the player
     */
    public LocationData location;
    /**
     * The health of the player
     */
    public double health;
    /**
     * The inventory of the player
     */
    public List<ItemStackData> items;

    /**
     * Converts an OfflinePlayer object to a GetPlayersResult object containing player information.
     *
     * @param  player    the OfflinePlayer object to convert
     * @param  isOnline  a boolean indicating if the player is online
     * @return           a GetPlayersResult object with player information
     */
    public static GetPlayersResult toGetPlayersResult(OfflinePlayer player, boolean isOnline)
    {
        GetPlayersResult result = new GetPlayersResult();
        
        // Name
        result.playerName = player.getName();

        // Is Online
        result.isOnline = isOnline;

        // UUID
        result.UUID = player.getUniqueId().toString();
        
        if (!isOnline) {
            return result;
        }

        Player onlinePlayer = (Player) player;
        // Location
        result.location = LocationData.fromLocation(onlinePlayer.getLocation());

        // Health
        result.health = onlinePlayer.getHealth();

        // Address
        result.address = onlinePlayer.getAddress().toString();

        // Inventory
        ItemStack[] contents = onlinePlayer.getInventory().getContents();
        if (contents != null) {
            result.items = new ArrayList<ItemStackData>();
            for (int i = 0; i < contents.length; i++) {
                ItemStack stack = contents[i];
                if (stack == null) {
                    continue;
                }
                result.items.add(ItemStackData.fromItemStack(stack, i));
            }
        }

        return result;
    }
}
