package com.pike.messageserver.runners;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.pike.messageserver.MessageServerPlugin;
import com.pike.messageserver.data.SetBlockData;
import com.pike.messageserver.results.SetBlockResult;

public class SetBlockRunner implements AbstractRunner{

    private MessageServerPlugin plugin;
    private CountDownLatch countDownLatch;
    private List<SetBlockData> setBlockData;
    private List<SetBlockResult> results = new ArrayList<SetBlockResult>();

    public SetBlockResult[] getResults() {
        return results.toArray(new SetBlockResult[results.size()]);
    }

    public SetBlockRunner(MessageServerPlugin plugin, List<SetBlockData> setBlockData, CountDownLatch countDownLatch) {
        this.plugin = plugin;
        this.setBlockData = setBlockData;
        this.countDownLatch = countDownLatch;
    }
    
    @Override
    public void run() {
        // Print get block request to server console if needed
        if (plugin.printDebug) {
            for (SetBlockData setBlock : setBlockData) {
                plugin.debugPrint(setBlock.toString());
            }
        }
        
        // Fill data
        for (SetBlockData setBlock : setBlockData) {
            try {
                results.add(setBlock.toSetBlockResult());
            } catch (Exception e) {
                SetBlockResult result = new SetBlockResult();
                result.success = false;
                result.exceptionMessage = e.getMessage();
                results.add(result);
            }
        }

        countDownLatch.countDown();
    }
}
