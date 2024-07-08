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
import com.pike.messageserver.data.SetBlockData;
import com.pike.messageserver.runners.AbstractRunner;
import com.pike.messageserver.runners.SetBlockRunner;
import com.sun.net.httpserver.HttpExchange;

public class SetBlockHandler extends AbstractHandler<List<SetBlockData>> {

    private Type mapType = new TypeToken<ArrayList<SetBlockData>>() {}.getType();
    
    public SetBlockHandler(MessageServerPlugin plugin) {
        super(plugin);
    }

    @Override
    protected List<SetBlockData> validateDecodedBody(HttpExchange exchange, String decodedBody) throws IOException {
        // Parse the request parameters
        List<SetBlockData> messages = Utils.deserializeFromJsonString(decodedBody, mapType);
        if (messages == null) {
            Utils.sendResponseData(exchange, StatusCode.UNPROCESSABLE_CONTENT, "Unable to parse JSON body data");
            plugin.debugPrint("Unable to parse JSON body data");
            return null;
        }
        plugin.debugPrint("List of SetBlockData contains: " + messages.size() + " items");

        // Validate commands
        for (SetBlockData message : messages) {
            if (message == null) {
                Utils.sendResponseData(exchange, StatusCode.UNPROCESSABLE_CONTENT, "One or more SetBlockData have a null value");
                plugin.debugPrint("One or more SetBlockData have a null value");
                return null;
            } else if (message.worldName == null) {
                Utils.sendResponseData(exchange, StatusCode.UNPROCESSABLE_CONTENT, "The worldName of one or more SetBlockData has a null value");
                plugin.debugPrint("The worldName of one or more SetBlockData has a null value");
                return null;
            }
            else if (message.blockData == null) {
                Utils.sendResponseData(exchange, StatusCode.UNPROCESSABLE_CONTENT, "The blockData of one or more SetBlockData has a null value");
                plugin.debugPrint("The blockData of one or more SetBlockData has a null value");
                return null;
            }
        }

        return messages;
    }

    @Override
    protected AbstractRunner createRunner(MessageServerPlugin messagePlugin, List<SetBlockData> messagesToRun,
            CountDownLatch latch) {
        return new SetBlockRunner(messagePlugin, messagesToRun, latch);
    }

    @Override
    protected boolean needRequestBody() {
        return true;
    }
}
