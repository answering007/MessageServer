package com.pike.messageserver.runners;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.pike.messageserver.MessageServerPlugin;
import com.pike.messageserver.data.CommandData;

public class CommandRunner implements AbstractRunner{

    private MessageServerPlugin plugin;
    private CountDownLatch countDownLatch;
    private List<CommandData> commands;
    private List<Boolean> results = new ArrayList<Boolean>();

    @Override
    public Boolean[] getResults() {
        return results.toArray(new Boolean[results.size()]);
    }

    public CommandRunner(MessageServerPlugin plugin, List<CommandData> commands, CountDownLatch countDownLatch) {
        this.plugin = plugin;
        this.commands = commands;
        this.countDownLatch = countDownLatch;
    }
    
    @Override
    public void run() {
        // Print commands
        for (CommandData commandData : commands) {
            if (commandData.playerName == null) {
                plugin.debugPrint("Server; Command:" + commandData.commandText);
            } else {
                plugin.debugPrint("Player:" + commandData.playerName + "; Command:" + commandData.commandText);
            }
        }  
        
        // Run commands
        for (CommandData commandData : commands) {
            results.add(commandData.toCommandResult());
        }

        countDownLatch.countDown();
    }
}
