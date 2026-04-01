package io.mailchk.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;

/**
 * Represents the result of a bulk email validation request.
 * The API returns {"results": [...]} only — summary counts are computed from the results list.
 */
public class BulkValidationResult {

    @JsonProperty("results")
    private List<ValidationResult> results;

    public BulkValidationResult() {
    }

    public BulkValidationResult(List<ValidationResult> results) {
        this.results = results != null ? results : Collections.emptyList();
    }

    /**
     * Gets the total number of emails validated.
     *
     * @return total count
     */
    @JsonIgnore
    public int getTotal() {
        return results != null ? results.size() : 0;
    }

    /**
     * Gets the number of valid emails.
     *
     * @return valid count
     */
    @JsonIgnore
    public int getValid() {
        return results != null ? (int) results.stream().filter(ValidationResult::isValid).count() : 0;
    }

    /**
     * Gets the number of invalid emails.
     *
     * @return invalid count
     */
    @JsonIgnore
    public int getInvalid() {
        return results != null ? (int) results.stream().filter(r -> !r.isValid()).count() : 0;
    }

    /**
     * Gets the number of disposable emails.
     *
     * @return disposable count
     */
    @JsonIgnore
    public int getDisposable() {
        return results != null ? (int) results.stream().filter(ValidationResult::isDisposable).count() : 0;
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
        int total = getTotal();
        return total > 0 ? (getValid() * 100.0) / total : 0.0;
    }

    /**
     * Gets the percentage of disposable emails.
     *
     * @return disposable percentage (0.0 to 100.0)
     */
    public double getDisposablePercentage() {
        int total = getTotal();
        return total > 0 ? (getDisposable() * 100.0) / total : 0.0;
    }

    @Override
    public String toString() {
        return String.format(
            "BulkValidationResult{total=%d, valid=%d, invalid=%d, disposable=%d, validPercentage=%.1f%%}",
            getTotal(), getValid(), getInvalid(), getDisposable(), getValidPercentage()
        );
    }
}
