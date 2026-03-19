package io.mailchk.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents the result of a bulk email validation request.
 */
public class BulkValidationResult {
    
    @JsonProperty("total")
    private int total;
    
    @JsonProperty("valid")
    private int valid;
    
    @JsonProperty("invalid")
    private int invalid;
    
    @JsonProperty("disposable")
    private int disposable;
    
    @JsonProperty("results")
    private List<ValidationResult> results;
    
    public BulkValidationResult() {
    }
    
    public BulkValidationResult(int total, int valid, int invalid, int disposable, List<ValidationResult> results) {
        this.total = total;
        this.valid = valid;
        this.invalid = invalid;
        this.disposable = disposable;
        this.results = results;
    }
    
    /**
     * Gets the total number of emails validated.
     * 
     * @return total count
     */
    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
    
    /**
     * Gets the number of valid emails.
     * 
     * @return valid count
     */
    public int getValid() {
        return valid;
    }
    
    public void setValid(int valid) {
        this.valid = valid;
    }
    
    /**
     * Gets the number of invalid emails.
     * 
     * @return invalid count
     */
    public int getInvalid() {
        return invalid;
    }
    
    public void setInvalid(int invalid) {
        this.invalid = invalid;
    }
    
    /**
     * Gets the number of disposable emails.
     * 
     * @return disposable count
     */
    public int getDisposable() {
        return disposable;
    }
    
    public void setDisposable(int disposable) {
        this.disposable = disposable;
    }
    
    /**
     * Gets the individual validation results.
     * 
     * @return list of validation results
     */
    public List<ValidationResult> getResults() {
        return results;
    }
    
    public void setResults(List<ValidationResult> results) {
        this.results = results;
    }
    
    /**
     * Gets the percentage of valid emails.
     * 
     * @return valid percentage (0.0 to 100.0)
     */
    public double getValidPercentage() {
        return total > 0 ? (valid * 100.0) / total : 0.0;
    }
    
    /**
     * Gets the percentage of disposable emails.
     * 
     * @return disposable percentage (0.0 to 100.0)
     */
    public double getDisposablePercentage() {
        return total > 0 ? (disposable * 100.0) / total : 0.0;
    }
    
    @Override
    public String toString() {
        return String.format(
            "BulkValidationResult{total=%d, valid=%d, invalid=%d, disposable=%d, validPercentage=%.1f%%}",
            total, valid, invalid, disposable, getValidPercentage()
        );
    }
}