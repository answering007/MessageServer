package com.pike.messageserver.runners;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.pike.messageserver.MessageServerPlugin;
import com.pike.messageserver.data.GetBlockData;
import com.pike.messageserver.results.GetBlockResult;

public class GetBlockRunner implements AbstractRunner{

    private MessageServerPlugin plugin;
    private CountDownLatch countDownLatch;
    private List<GetBlockData> getBlockData;
    private List<GetBlockResult> results = new ArrayList<GetBlockResult>();

    public GetBlockResult[] getResults() {
        return results.toArray(new GetBlockResult[results.size()]);
    }

    public GetBlockRunner(MessageServerPlugin plugin, List<GetBlockData> getBlockData, CountDownLatch countDownLatch) {
        this.plugin = plugin;
        this.getBlockData = getBlockData;
        this.countDownLatch = countDownLatch;
    }
    
    @Override
    public void run() {
        // Print get block request to server console if needed
        if (plugin.printDebug) {
            for (GetBlockData getBlock : getBlockData) {
                plugin.debugPrint(getBlock.toString());
            }
        }
        
        // Fill data
        for (GetBlockData getBlock : getBlockData) {
            try {
                results.add(GetBlockData.toGetBlockResult(getBlock));
            } catch (Exception e) {
                GetBlockResult result = new GetBlockResult();
                result.success = false;
                result.exceptionMessage = e.getMessage();
                results.add(result);
            }
        }

        countDownLatch.countDown();
    }
}
