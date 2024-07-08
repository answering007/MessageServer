package com.pike.messageserver.results;

import java.util.List;

import com.pike.messageserver.data.ItemStackData;

public class GetBlockResult {
    /**
     * True if the request was successful; false otherwise
     */
    public boolean success;
    /**
     * Block data as a string
     */
    public String result;
    /**
     * If the request was not successful, this contains the exception message
     */
    public String exceptionMessage;
    /**
     * If the request was successful, this contains the block data
     */
    public List<ItemStackData> items;

    @Override
    public String toString() {
        return "GetBlockResult{" +
                "success=" + success +
                ", result='" + result + '\'' +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                ", items=" + items +
                '}';
    }
}
