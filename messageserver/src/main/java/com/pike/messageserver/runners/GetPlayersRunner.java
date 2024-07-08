package com.pike.messageserver.runners;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import com.pike.messageserver.MessageServerPlugin;
import com.pike.messageserver.results.GetPlayersResult;

public class GetPlayersRunner implements AbstractRunner {
    
    private CountDownLatch countDownLatch;
    private HashMap<String, GetPlayersResult> results = new HashMap<String, GetPlayersResult>();
    private boolean onlineOnly;

    @Override
    public GetPlayersResult[] getResults() {
        return results.values().toArray(new GetPlayersResult[results.size()]);
    }

    public GetPlayersRunner(MessageServerPlugin plugin, boolean onlineOnly, CountDownLatch countDownLatch) {
        this.onlineOnly = onlineOnly;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        
        for (OfflinePlayer player : Bukkit.getOnlinePlayers()) {
            GetPlayersResult result = GetPlayersResult.toGetPlayersResult(player, true);
            results.put(result.UUID, result);
        }
        if (onlineOnly){
            countDownLatch.countDown();
            return;
        }

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()){
            GetPlayersResult result = GetPlayersResult.toGetPlayersResult(player, false);
            if (!results.containsKey(result.UUID)) {
                results.put(result.UUID, result);
            }
        }
        countDownLatch.countDown();
    }
}
