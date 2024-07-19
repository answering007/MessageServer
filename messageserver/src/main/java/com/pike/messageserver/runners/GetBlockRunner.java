package com.pike.messageserver.runners;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.pike.messageserver.MessageServerPlugin;
import com.pike.messageserver.data.LocationData;
import com.pike.messageserver.results.GetBlockResult;

public class GetBlockRunner implements AbstractRunner{

    private MessageServerPlugin plugin;
    private CountDownLatch countDownLatch;
    private List<LocationData> locationData;
    private List<GetBlockResult> results = new ArrayList<GetBlockResult>();

    public GetBlockResult[] getResults() {
        return results.toArray(new GetBlockResult[results.size()]);
    }

    public GetBlockRunner(MessageServerPlugin plugin, List<LocationData> locationData, CountDownLatch countDownLatch) {
        this.plugin = plugin;
        this.locationData = locationData;
        this.countDownLatch = countDownLatch;
    }
    
    @Override
    public void run() {
        // Print get block request to server console if needed
        if (plugin.printDebug) {
            for (LocationData location : locationData) {
                plugin.debugPrint(location.toString());
            }
        }
        
        // Fill data
        for (LocationData location : locationData) {
            try {
                results.add(location.toGetBlockResult());
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
