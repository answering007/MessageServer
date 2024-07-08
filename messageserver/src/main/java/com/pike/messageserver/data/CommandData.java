package com.pike.messageserver.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandData {
    /**
     * The name of the player to execute the command on. If null, the command will be executed on the server.
     */
    public String playerName;

    /**
     * The command to execute.
     */
    public String commandText;

    @Override
    public String toString() {
        return "CommandData{" +
                "playerName='" + playerName + '\'' +
                ", commandText='" + commandText + '\'' +
                '}';
    }

    /**
     * Executes the command specified in the CommandData object and returns the result.
     *
     * @return true if the command was executed successfully, false otherwise
     */
    public boolean toCommandResult() {
        boolean result = false;
        if (playerName == null) {
            // Run server command and collect result            
            result = Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), commandText);
        } else {
            // Run player command and collect result
            Player player = Bukkit.getPlayerExact(playerName);
            if (player != null){
                result = player.performCommand(commandText);
            }
        }
        return result;
    }
}
