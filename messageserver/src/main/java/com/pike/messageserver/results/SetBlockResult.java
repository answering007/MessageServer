package com.pike.messageserver.results;

public class SetBlockResult {
    /**
     * Indicates if the operation was successful or not.
     */
    public Boolean success;
    /**
     * The exception message if the operation was not successful.
     */
    public String exceptionMessage;

    @Override
    public String toString() {
        return "SetBlockResult{" +
                "success=" + success +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                '}';
    }
}
