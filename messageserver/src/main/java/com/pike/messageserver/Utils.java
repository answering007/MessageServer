package com.pike.messageserver;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

public class Utils {
    
    private static final Charset charset = StandardCharsets.UTF_8;
    
    /**
     * Sends the provided data as a response to the given HttpExchange with the specified status code.
     *
     * @param  exchange  the HttpExchange to send the response to
     * @param  status    the status code of the response
     * @param  data      the data to send as the response body
     * @throws IOException if there is an error sending the response data
     */
    public static void sendResponseData(HttpExchange exchange, StatusCode status, String data) throws IOException{
        int dataLenhgt = data.length();          
        exchange.sendResponseHeaders(status.getCode(), dataLenhgt);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(data.getBytes(charset));
        } catch (IOException e) {
            throw new IOException("Failed to send response data", e);
        }  
    }

    /**
     * Sends the provided data as a response to the given HttpExchange with the specified status code.
     *
     * @param  exchange   the HttpExchange to send the response to
     * @param  status     the status code of the response
     * @param  data       the data to send as the response body
     * @throws IOException if there is an error sending the response data
     */
    public static void sendResponseData(HttpExchange exchange, StatusCode status, Object data) throws IOException{
        sendResponseData(exchange, status, new Gson().toJson(data));
    }

    // https://base64.guru/learn/base64-characters
    private static final Pattern base64 = Pattern.compile("^[A-Za-z0-9+/]+={0,2}$");
    public static String decodeText(String encodedText) {
        if (encodedText == null) return null;
        if (!base64.matcher(encodedText).matches()) return null;
        
        byte[] decodedBytes = Base64.getDecoder().decode(encodedText);
        String decodedString = new String(decodedBytes, charset);
		return decodedString;
    }

    /**
     * Encodes the given text string to Base64.
     *
     * @param  text   the text to be encoded
     * @return        the Base64 encoded string
     */
    public static String encodeText(String text) {
        byte[] bytes = text.getBytes(charset);
        String encodedString = Base64.getEncoder().encodeToString(bytes);
        return encodedString;
    }

    /**
     * Retrieves the request body from the given HttpExchange as a string.
     *
     * @param  exchange   the HttpExchange containing the request body
     * @return            the request body as a string
     * @throws IOException if an I/O error occurs
     */
    public static String getRequestBody(HttpExchange exchange) throws IOException {
        // Get the request body as a string
        StringBuilder requestBody = new StringBuilder();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = exchange.getRequestBody().read(buffer)) != -1) {
            requestBody.append(new String(buffer, 0, bytesRead));
        }
        return requestBody.toString();
    }

    /**
     * Deserializes a JSON string into an object of type T using Gson library.
     *
     * @param  objectData  the JSON string to deserialize
     * @param  mapType     the type of object to deserialize into
     * @return              the deserialized object of type T, or null if the input is null
     * @throws JsonSyntaxException if there is a syntax error in the JSON string
     */
    public static <T> T deserializeFromJsonString(String objectData, Type mapType) throws JsonSyntaxException {
        if (objectData == null) return null;
        if (mapType == null) return null;

        T object = new Gson().fromJson(objectData, mapType);
        return object;
    }
}
