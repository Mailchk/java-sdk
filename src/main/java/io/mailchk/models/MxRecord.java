package io.mailchk.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an MX (Mail Exchange) DNS record.
 */
public class MxRecord {
    
    @JsonProperty("exchange")
    private String exchange;
    
    @JsonProperty("priority")
    private int priority;
    
    public MxRecord() {
    }
    
    public MxRecord(String exchange, int priority) {
        this.exchange = exchange;
        this.priority = priority;
    }
    
    /**
     * Gets the mail server hostname.
     * 
     * @return the exchange hostname
     */
    public String getExchange() {
        return exchange;
    }
    
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
    
    /**
     * Gets the MX record priority (lower values have higher priority).
     * 
     * @return the priority value
     */
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    @Override
    public String toString() {
        return String.format("MxRecord{exchange='%s', priority=%d}", exchange, priority);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        MxRecord mxRecord = (MxRecord) obj;
        return priority == mxRecord.priority && 
               (exchange != null ? exchange.equals(mxRecord.exchange) : mxRecord.exchange == null);
    }
    
    @Override
    public int hashCode() {
        int result = exchange != null ? exchange.hashCode() : 0;
        result = 31 * result + priority;
        return result;
    }
}