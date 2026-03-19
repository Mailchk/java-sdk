package io.mailchk;

import io.mailchk.exceptions.*;
import io.mailchk.http.HttpClient;
import io.mailchk.models.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Main client for the Mailchk email validation API.
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * MailchkClient client = MailchkClient.builder()
 *     .apiKey("your-api-key")
 *     .build();
 *     
 * ValidationResult result = client.validate("user@example.com");
 * if (result.isValid() && !result.isDisposable()) {
 *     System.out.println("Email is valid!");
 * }
 * }</pre>
 */
public class MailchkClient implements AutoCloseable {
    
    private static final String DEFAULT_BASE_URL = "https://api.mailchk.io/v1";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    
    private final HttpClient httpClient;
    
    /**
     * Creates a new MailchkClient with the specified configuration.
     * 
     * @param apiKey the Mailchk API key
     * @param baseUrl the API base URL
     * @param timeout the request timeout
     * @throws IllegalArgumentException if apiKey is null or empty
     */
    public MailchkClient(String apiKey, String baseUrl, Duration timeout) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }
        
        this.httpClient = new HttpClient(
            apiKey.trim(),
            baseUrl != null ? baseUrl : DEFAULT_BASE_URL,
            timeout != null ? timeout : DEFAULT_TIMEOUT
        );
    }
    
    /**
     * Creates a new builder for configuring the MailchkClient.
     * 
     * @return a new MailchkClientBuilder
     */
    public static MailchkClientBuilder builder() {
        return new MailchkClientBuilder();
    }
    
    /**
     * Validates a single email address.
     * 
     * @param email the email address to validate
     * @return the validation result
     * @throws MailchkException if the validation fails
     * @throws IllegalArgumentException if email is null or invalid format
     */
    public ValidationResult validate(String email) throws MailchkException {
        validateEmailParameter(email);
        
        Map<String, Object> request = new HashMap<>();
        request.put("email", email.trim().toLowerCase());
        
        return httpClient.post("/check", request, ValidationResult.class);
    }
    
    /**
     * Validates a single email address asynchronously.
     * 
     * @param email the email address to validate
     * @return CompletableFuture with the validation result
     * @throws IllegalArgumentException if email is null or invalid format
     */
    public CompletableFuture<ValidationResult> validateAsync(String email) {
        validateEmailParameter(email);
        
        Map<String, Object> request = new HashMap<>();
        request.put("email", email.trim().toLowerCase());
        
        return httpClient.postAsync("/check", request, ValidationResult.class);
    }
    
    /**
     * Validates multiple email addresses in a single request.
     * 
     * @param emails the list of email addresses to validate (max 100)
     * @return the bulk validation result
     * @throws MailchkException if the validation fails
     * @throws IllegalArgumentException if emails list is invalid
     */
    public BulkValidationResult validateBulk(List<String> emails) throws MailchkException {
        validateEmailsParameter(emails);
        
        Map<String, Object> request = new HashMap<>();
        request.put("emails", emails.stream()
                .map(email -> email.trim().toLowerCase())
                .toList());
        
        return httpClient.post("/check/bulk", request, BulkValidationResult.class);
    }
    
    /**
     * Validates multiple email addresses asynchronously.
     * 
     * @param emails the list of email addresses to validate (max 100)
     * @return CompletableFuture with the bulk validation result
     * @throws IllegalArgumentException if emails list is invalid
     */
    public CompletableFuture<BulkValidationResult> validateBulkAsync(List<String> emails) {
        validateEmailsParameter(emails);
        
        Map<String, Object> request = new HashMap<>();
        request.put("emails", emails.stream()
                .map(email -> email.trim().toLowerCase())
                .toList());
        
        return httpClient.postAsync("/check/bulk", request, BulkValidationResult.class);
    }
    
    /**
     * Quick check if an email is from a disposable provider.
     * 
     * @param email the email address to check
     * @return true if disposable, false otherwise
     * @throws MailchkException if the request fails
     * @throws IllegalArgumentException if email is invalid
     */
    public boolean isDisposable(String email) throws MailchkException {
        ValidationResult result = validate(email);
        return result.isDisposable();
    }
    
    /**
     * Quick check if an email is from a disposable provider (async).
     * 
     * @param email the email address to check
     * @return CompletableFuture with true if disposable, false otherwise
     * @throws IllegalArgumentException if email is invalid
     */
    public CompletableFuture<Boolean> isDisposableAsync(String email) {
        return validateAsync(email).thenApply(ValidationResult::isDisposable);
    }
    
    /**
     * Quick check if an email is valid.
     * 
     * @param email the email address to check
     * @return true if valid, false otherwise
     * @throws MailchkException if the request fails
     * @throws IllegalArgumentException if email is invalid
     */
    public boolean isValid(String email) throws MailchkException {
        ValidationResult result = validate(email);
        return result.isValid();
    }
    
    /**
     * Quick check if an email is valid (async).
     * 
     * @param email the email address to check
     * @return CompletableFuture with true if valid, false otherwise
     * @throws IllegalArgumentException if email is invalid
     */
    public CompletableFuture<Boolean> isValidAsync(String email) {
        return validateAsync(email).thenApply(ValidationResult::isValid);
    }
    
    /**
     * Gets the risk score for an email address.
     * 
     * @param email the email address to check
     * @return risk level: "low", "medium", "high", or "critical"
     * @throws MailchkException if the request fails
     * @throws IllegalArgumentException if email is invalid
     */
    public String getRiskScore(String email) throws MailchkException {
        ValidationResult result = validate(email);
        return result.getRiskScore();
    }
    
    /**
     * Gets the risk score for an email address (async).
     * 
     * @param email the email address to check
     * @return CompletableFuture with the risk level
     * @throws IllegalArgumentException if email is invalid
     */
    public CompletableFuture<String> getRiskScoreAsync(String email) {
        return validateAsync(email).thenApply(ValidationResult::getRiskScore);
    }
    
    /**
     * Gets the deliverability score for an email address.
     * 
     * @param email the email address to check
     * @return deliverability score (0-100)
     * @throws MailchkException if the request fails
     * @throws IllegalArgumentException if email is invalid
     */
    public int getDeliverabilityScore(String email) throws MailchkException {
        ValidationResult result = validate(email);
        return result.getDeliverabilityScore();
    }
    
    /**
     * Gets the deliverability score for an email address (async).
     * 
     * @param email the email address to check
     * @return CompletableFuture with the deliverability score
     * @throws IllegalArgumentException if email is invalid
     */
    public CompletableFuture<Integer> getDeliverabilityScoreAsync(String email) {
        return validateAsync(email).thenApply(ValidationResult::getDeliverabilityScore);
    }
    
    /**
     * Gets current API usage and quota information.
     * 
     * @return usage information
     * @throws MailchkException if the request fails
     */
    public UsageInfo getUsage() throws MailchkException {
        return httpClient.get("/usage", UsageInfo.class);
    }
    
    /**
     * Gets current API usage and quota information (async).
     * 
     * @return CompletableFuture with usage information
     */
    public CompletableFuture<UsageInfo> getUsageAsync() {
        return httpClient.getAsync("/usage", UsageInfo.class);
    }
    
    /**
     * Validates email parameter for API calls.
     * 
     * @param email the email to validate
     * @throws IllegalArgumentException if email is invalid
     */
    private void validateEmailParameter(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email must contain @ symbol");
        }
    }
    
    /**
     * Validates emails list parameter for bulk API calls.
     * 
     * @param emails the emails list to validate
     * @throws IllegalArgumentException if emails list is invalid
     */
    private void validateEmailsParameter(List<String> emails) {
        if (emails == null || emails.isEmpty()) {
            throw new IllegalArgumentException("Emails list cannot be null or empty");
        }
        if (emails.size() > 100) {
            throw new IllegalArgumentException("Maximum 100 emails per bulk request");
        }
        for (String email : emails) {
            validateEmailParameter(email);
        }
    }
    
    /**
     * Closes the client and releases resources.
     */
    @Override
    public void close() {
        httpClient.close();
    }
}