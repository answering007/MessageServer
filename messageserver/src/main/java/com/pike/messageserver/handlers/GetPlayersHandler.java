package com.pike.messageserver.handlers;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import com.pike.messageserver.MessageServerPlugin;
import com.pike.messageserver.StatusCode;
import com.pike.messageserver.runners.AbstractRunner;
import com.pike.messageserver.runners.GetPlayersRunner;
import com.sun.net.httpserver.HttpExchange;
import com.pike.messageserver.Utils;

public class GetPlayersHandler extends AbstractHandler<Boolean> {

    public GetPlayersHandler(MessageServerPlugin plugin) {
        super(plugin);
    }

    @Override
    protected Boolean validateDecodedBody(HttpExchange exchange, String decodedBody) throws IOException {
        try {
            boolean onlineOnly = Utils.deserializeFromJsonString(decodedBody, boolean.class);
            return onlineOnly;
        } catch (Exception e) {
            Utils.sendResponseData(exchange, StatusCode.UNPROCESSABLE_CONTENT, "Unable to parse JSON body data");
            plugin.debugPrint("Unable to parse JSON body data");
            return null;
        }        
    }

    @Override
    protected AbstractRunner createRunner(MessageServerPlugin messagePlugin, Boolean messagesToRun,
            CountDownLatch latch) {
        return new GetPlayersRunner(messagePlugin, messagesToRun, latch);
    }

    @Override
    protected boolean needRequestBody() {
        return true;
    }
}
