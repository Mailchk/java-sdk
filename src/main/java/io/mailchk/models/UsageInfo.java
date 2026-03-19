package io.mailchk.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents API usage and quota information.
 */
public class UsageInfo {
    
    @JsonProperty("plan")
    private String plan;
    
    @JsonProperty("used")
    private int used;
    
    @JsonProperty("limit")
    private int limit;
    
    @JsonProperty("remaining")
    private int remaining;
    
    @JsonProperty("reset_date")
    private String resetDate;
    
    public UsageInfo() {
    }
    
    public UsageInfo(String plan, int used, int limit, int remaining, String resetDate) {
        this.plan = plan;
        this.used = used;
        this.limit = limit;
        this.remaining = remaining;
        this.resetDate = resetDate;
    }
    
    /**
     * Gets the current subscription plan.
     * 
     * @return plan name (e.g., "free", "pro", "business")
     */
    public String getPlan() {
        return plan;
    }
    
    public void setPlan(String plan) {
        this.plan = plan;
    }
    
    /**
     * Gets the number of API calls used in the current billing cycle.
     * 
     * @return used count
     */
    public int getUsed() {
        return used;
    }
    
    public void setUsed(int used) {
        this.used = used;
    }
    
    /**
     * Gets the total API calls allowed in the current billing cycle.
     * 
     * @return limit count
     */
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    /**
     * Gets the number of API calls remaining in the current billing cycle.
     * 
     * @return remaining count
     */
    public int getRemaining() {
        return remaining;
    }
    
    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }
    
    /**
     * Gets the date when the usage quota resets.
     * 
     * @return reset date string
     */
    public String getResetDate() {
        return resetDate;
    }
    
    public void setResetDate(String resetDate) {
        this.resetDate = resetDate;
    }
    
    /**
     * Gets the percentage of quota used.
     * 
     * @return usage percentage (0.0 to 100.0)
     */
    public double getPercentageUsed() {
        return limit > 0 ? (used * 100.0) / limit : 0.0;
    }
    
    /**
     * Checks if the quota is nearly exhausted (>90% used).
     * 
     * @return true if quota is nearly exhausted
     */
    public boolean isQuotaNearlyExhausted() {
        return getPercentageUsed() > 90.0;
    }
    
    /**
     * Checks if the quota is exhausted.
     * 
     * @return true if quota is exhausted
     */
    public boolean isQuotaExhausted() {
        return remaining <= 0;
    }
    
    /**
     * Parses the reset date as a LocalDateTime.
     * 
     * @return LocalDateTime or null if parsing fails
     */
    public LocalDateTime getResetDateTime() {
        if (resetDate == null || resetDate.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(resetDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return String.format(
            "UsageInfo{plan='%s', used=%d, limit=%d, remaining=%d, resetDate='%s', percentageUsed=%.1f%%}",
            plan, used, limit, remaining, resetDate, getPercentageUsed()
        );
    }
}