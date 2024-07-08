package com.pike.messageserver.handlers;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;

import com.pike.messageserver.MessageServerPlugin;
import com.pike.messageserver.StatusCode;
import com.pike.messageserver.Utils;
import com.pike.messageserver.runners.AbstractRunner;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class AbstractHandler<T> implements HttpHandler{

    protected MessageServerPlugin plugin;

    public AbstractHandler(MessageServerPlugin plugin) {
        this.plugin = plugin;
    }    
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Set the uncaught exception handler
        Thread.currentThread().setUncaughtExceptionHandler(plugin.exceptionHandler);
        
        plugin.debugPrint("=====================NEW HANDLE=====================");
        plugin.debugPrint("Handler name is: " + this.getClass().getName());
        plugin.debugPrint("HttpExchange handle: " + 
            "Host: " + exchange.getRemoteAddress().toString() + 
            " Method: " + exchange.getRequestMethod() +
            " Path: " + exchange.getRequestURI().toString());
        
        // Validate POST method
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")){
            Utils.sendResponseData(exchange, StatusCode.METHOD_NOT_ALLOWED, "Require POST method");
            return;
        }

        // Validate authorization
        plugin.debugPrint("Checking authorization...");        
        if (!plugin.checkAuthorization(exchange)) {
            exchange.getResponseHeaders().set("WWW-Authenticate", "Basic realm=\"Username and password required\"");
            Utils.sendResponseData(exchange, StatusCode.UNAUTHORIZED, "Authorization required. Format: Basic base64(username):base64(password))");
            return;
        }
        plugin.debugPrint("Checking authorization done");

        // Get the request body
        try {
            
            // Default input data
            T inputData = null;

            // Check if need request body
            if (needRequestBody()) {
                // Parce request body
                String encodedMessageBody = Utils.getRequestBody(exchange);
                if (encodedMessageBody == null) {
                    Utils.sendResponseData(exchange, StatusCode.UNPROCESSABLE_CONTENT, "Body is Null");
                    plugin.debugPrint("Encoded body is null");
                    return;
                }
                plugin.debugPrint("Encoded body:" + encodedMessageBody);

                // Decode the request body
                String decodedMessageBody = Utils.decodeText(encodedMessageBody);
                if (decodedMessageBody == null) {
                    Utils.sendResponseData(exchange, StatusCode.UNPROCESSABLE_CONTENT, "Unable to decode body");
                    plugin.debugPrint("Unable to decode body");
                    return;
                }
                plugin.debugPrint("Decoded body:" + decodedMessageBody);

                inputData = validateDecodedBody(exchange, decodedMessageBody);

                // Check if result is null
                if (inputData == null) {
                    plugin.debugPrint("Input data is null after validation");
                    return;
                }
                plugin.debugPrint("Input data validated");
            }
            else {
                plugin.debugPrint("Don't need request body");
            }

            // Execute the commands
            CountDownLatch latch = new CountDownLatch(1);            
            AbstractRunner runner = createRunner(plugin, inputData, latch);            
            plugin.debugPrint("Running task...");
            Bukkit.getScheduler().runTask(plugin, runner);
            latch.await();
                        
            plugin.debugPrint("Sending results...");
            // Set the response headers and results
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            Utils.sendResponseData(exchange, StatusCode.OK, runner.getResults());
            plugin.debugPrint("Done!");

        } catch (Exception e) {
            Utils.sendResponseData(exchange, StatusCode.BAD_REQUEST, e.getMessage());
            plugin.debugPrint("Something went wrong: " + e.getMessage());
            plugin.debugPrint("StackTrace: " + ExceptionUtils.getStackTrace(e));
        }
    }

    protected abstract T validateDecodedBody(HttpExchange exchange, String decodedBody) throws IOException;

    protected abstract AbstractRunner createRunner(MessageServerPlugin plugin, T messagesToRun, CountDownLatch latch);

    protected abstract boolean needRequestBody();
}
