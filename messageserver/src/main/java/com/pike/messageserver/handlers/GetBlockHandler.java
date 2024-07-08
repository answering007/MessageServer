package com.pike.messageserver.handlers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.google.gson.reflect.TypeToken;
import com.pike.messageserver.MessageServerPlugin;
import com.pike.messageserver.StatusCode;
import com.pike.messageserver.Utils;
import com.pike.messageserver.data.GetBlockData;
import com.pike.messageserver.runners.GetBlockRunner;
import com.pike.messageserver.runners.AbstractRunner;
import com.sun.net.httpserver.HttpExchange;

public class GetBlockHandler extends AbstractHandler<List<GetBlockData>> {
    
    private Type mapType = new TypeToken<ArrayList<GetBlockData>>() {}.getType();

    public GetBlockHandler(MessageServerPlugin plugin) {
        super(plugin);
    }

    @Override
    protected List<GetBlockData> validateDecodedBody(HttpExchange exchange, String decodedBody) throws IOException {
        // Parse the request parameters
        List<GetBlockData> messages = Utils.deserializeFromJsonString(decodedBody, mapType);
        if (messages == null) {
            Utils.sendResponseData(exchange, StatusCode.UNPROCESSABLE_CONTENT, "Unable to parse JSON body data");
            plugin.debugPrint("Unable to parse JSON body data");
            return null;
        }
        plugin.debugPrint("List of GetBlockData contains: " + messages.size() + " items");

        // Validate commands
        for (GetBlockData message : messages) {
            if (message == null) {
                Utils.sendResponseData(exchange, StatusCode.UNPROCESSABLE_CONTENT, "One or more GetBlockData have a null value");
                plugin.debugPrint("One or more GetBlockData have a null value");
                return null;
            } else if (message.worldName == null) {
                Utils.sendResponseData(exchange, StatusCode.UNPROCESSABLE_CONTENT, "The worldName of one or more GetBlockData has a null value");
                plugin.debugPrint("The worldName of one or more GetBlockData has a null value");
                return null;
            }
        }

        return messages;
    }

    @Override
    protected AbstractRunner createRunner(MessageServerPlugin messagePlugin, List<GetBlockData> messagesToRun,
            CountDownLatch latch) {
        return new GetBlockRunner(messagePlugin, messagesToRun, latch);
    }

    @Override
    protected boolean needRequestBody() {
        return true;
    }
}
