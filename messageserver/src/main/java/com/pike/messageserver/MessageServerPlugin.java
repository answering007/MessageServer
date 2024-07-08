package com.pike.messageserver;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.plugin.java.JavaPlugin;

import com.pike.messageserver.handlers.CommandHandler;
import com.pike.messageserver.handlers.GetBlockHandler;
import com.pike.messageserver.handlers.GetPlayersHandler;
import com.pike.messageserver.handlers.SetBlockHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class MessageServerPlugin extends JavaPlugin
{
    public static final String LOGIN_PASSWORD_DELIMINER = ":";
    private static final String DEBUG_PREFIX = "[Debug]: ";
    
    // Plugin settings
    public volatile boolean printDebug = false;
    
    // Webserver settings
    private HttpServer server;
    private int port = 8000;
    private int threadsNo = 3;
    private int stopDelay = 2;
    private volatile boolean needAuthorization = false;
    private volatile String login = "Admin";
    private volatile String password = "123";
    
    public final Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread th, Throwable ex) {
            getLogger().warning("Unhandled exception in thread " + th.getName() + ": " + ex.getMessage());
            getLogger().warning(ExceptionUtils.getStackTrace(ex));
        };
    };
    
    // Handlers
    CommandHandler commandHandler = new CommandHandler(this);
    GetBlockHandler getBlockHandler = new GetBlockHandler(this);
    SetBlockHandler setBlockHandler = new SetBlockHandler(this);
    GetPlayersHandler getPlayersHandler = new GetPlayersHandler(this);

    void loadSavedConfig() {
        // Load configuration
        this.printDebug = this.getConfig().getBoolean("plugin.print_debug", printDebug);
        
        this.port = this.getConfig().getInt("webserver.port", port);
        this.threadsNo = this.getConfig().getInt("webserver.threads_no", threadsNo);
        this.stopDelay = this.getConfig().getInt("webserver.stop_delay", stopDelay);
        this.needAuthorization = this.getConfig().getBoolean("webserver.authorization.need", needAuthorization);
        this.login = this.getConfig().getString("webserver.authorization.user", login);
        this.password = this.getConfig().getString("webserver.authorization.password", password);

        // Save configuration
        this.getConfig().set("plugin.print_debug", printDebug);
        
        this.getConfig().set("webserver.port", port);
        this.getConfig().set("webserver.threads_no", threadsNo);
        this.getConfig().set("webserver.stop_delay", stopDelay);
        this.getConfig().set("webserver.authorization.need", needAuthorization);
        this.getConfig().set("webserver.authorization.user", login);
        this.getConfig().set("webserver.authorization.password", password);

        this.saveConfig();
    }

    void checkLoginPassword() {
        if (login.contains(LOGIN_PASSWORD_DELIMINER)) {
            getLogger().warning("Login can't contain \"" + LOGIN_PASSWORD_DELIMINER + "\"");
        }
        if (password.contains(LOGIN_PASSWORD_DELIMINER)) {
            getLogger().warning("Password can't contain \"" + LOGIN_PASSWORD_DELIMINER + "\"");
        }
    }

    public void onEnable()
    {
        // Load configuration
        loadSavedConfig();
        // Check login and password for ":" characters
        checkLoginPassword();

        getLogger().info("Starting HTTP API Server...");
        try {
            // Setup server port
            server = HttpServer.create(new InetSocketAddress(this.port), 0);
            
            // Setup server routes & handlers
            server.createContext("/runCommand", commandHandler);
            server.createContext("/getBlock", getBlockHandler);
            server.createContext("/setBlock", setBlockHandler);
            server.createContext("/getPlayers", getPlayersHandler);

            // Setup server executor
            server.setExecutor(Executors.newFixedThreadPool(threadsNo));

            // start http server
            server.start();

            // Log message in case of server successful start
            getLogger().info("HTTP API Server successfully started! Listening to port: " + this.port);
        } catch (Exception e) {
            getLogger().warning("Unable to start HTTP API server!");
        }
    }

    public void onDisable()
    {      
        // Stop server
        if (server != null) {
                server.stop(stopDelay);
                server = null;
            }
        getLogger().info("HTTP API Server disabled");
    }

    /**
     * Prints the given message to the server log if debugging is enabled.
     *
     * @param message the message to be printed
     */
    public void debugPrint(String message) {
        // If debugging is not enabled, do nothing
        if (!printDebug) {
            return;
        }
        
        // If the message is null, print a special message to indicate that
        if (message == null) {
            getLogger().info(DEBUG_PREFIX + "null");
            return;
        }
        
        // Print the message to the server log
        getLogger().info(DEBUG_PREFIX + message);
    }

    /**
     * Checks if the provided HttpExchange has valid authorization credentials.
     *
     * @param  exchange  the HttpExchange to check for authorization
     * @return           true if the authorization is valid, false otherwise
     */
    public boolean checkAuthorization(HttpExchange exchange) {
        final String authorizationTocken = "Authorization";
        final String basicTocken = "Basic ";

        debugPrint("Authorization need:" + String.valueOf(this.needAuthorization));
        if (!this.needAuthorization)
            return true;
        if (exchange == null) {
            return false;
        }
        
        String authorization = exchange.getRequestHeaders().getFirst(authorizationTocken);        
        if (authorization == null) {
            debugPrint("Authorization header NOT found");
            return false;
        }
        debugPrint("Authorization header:" + authorization);

        boolean startsWith = authorization.startsWith(basicTocken);
        if (!startsWith) {
            debugPrint("Authorization don't start with \"" + basicTocken + "\"");
            return false;
        }
        
        String credentials = authorization.substring(basicTocken.length());
        if (credentials == null) {
            debugPrint("Credentials header NOT found");
            return false;
        }
        debugPrint("Credentials:" + credentials);
                
        String decodedCredentials = Utils.decodeText(credentials);
        if (decodedCredentials == null) {
            debugPrint("Credentials is null after base64 decoding");
            return false;
        }
        debugPrint("Decoded credentials:" + decodedCredentials);

        String[] parts = decodedCredentials.split(LOGIN_PASSWORD_DELIMINER);
        if (parts == null || parts.length != 2) {
            debugPrint("Credentials don't contain 2 parts when split by \"" + LOGIN_PASSWORD_DELIMINER + "\"");
            return false;
        }
        String input_username = parts[0];
        String input_password = parts[1];
        
        if (input_username == null || input_password == null) {
            debugPrint("Username or password are null after base64 decoding");
            return false;
        }
        debugPrint("input_username:" + input_username + "; input_password:" + input_password);
        debugPrint("server.username:" + this.login + "; server.password:" + this.password);
        debugPrint("username equals:" + this.login.equals(input_username));
        debugPrint("password equals:" + this.password.equals(input_password));

        return this.login.equals(input_username) && this.password.equals(input_password);
    }
}
