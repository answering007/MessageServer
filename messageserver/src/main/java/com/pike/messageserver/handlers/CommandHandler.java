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
import com.pike.messageserver.data.CommandData;
import com.pike.messageserver.runners.CommandRunner;
import com.pike.messageserver.runners.AbstractRunner;
import com.sun.net.httpserver.HttpExchange;

public class CommandHandler extends AbstractHandler<List<CommandData>> {

    private Type mapType = new TypeToken<ArrayList<CommandData>>() {}.getType();

    public CommandHandler(MessageServerPlugin plugin) {
        super(plugin);
    }

    @Override
    protected List<CommandData> validateDecodedBody(HttpExchange exchange, String decodedCommandBody) throws IOException {
        // Parse the request parameters
        List<CommandData> commands = Utils.deserializeFromJsonString(decodedCommandBody, mapType);
        if (commands == null) {
            Utils.sendResponseData(exchange, StatusCode.UNPROCESSABLE_CONTENT, "Unable to parse JSON body data");
            plugin.debugPrint("Unable to parse JSON body data");
            return null;
        }
        plugin.debugPrint("List of commands contains: " + commands.size() + " items");

        // Validate commands
        for (CommandData commandData : commands) {
            if (commandData == null) {
                Utils.sendResponseData(exchange, StatusCode.UNPROCESSABLE_CONTENT, "One or more commands have a null value");
                plugin.debugPrint("One or more commands have a null value");
                return null;
            } else if (commandData.commandText == null) {
                Utils.sendResponseData(exchange, StatusCode.UNPROCESSABLE_CONTENT, "The text of one or more commands has a null value");
                plugin.debugPrint("The text of one or more commands has a null value");
                return null;
            }
        }
        return commands;
    }

    @Override
    protected AbstractRunner createRunner(MessageServerPlugin messagePlugin, List<CommandData> messagesToRun, CountDownLatch latch) {
        return new CommandRunner(messagePlugin, messagesToRun, latch);
    }

    @Override
    protected boolean needRequestBody() {
        return true;
    }
}
