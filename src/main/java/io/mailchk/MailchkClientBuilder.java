package io.mailchk;

import java.time.Duration;

/**
 * Builder class for creating MailchkClient instances with fluent configuration.
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * MailchkClient client = MailchkClient.builder()
 *     .apiKey("your-api-key")
 *     .baseUrl("https://api.mailchk.io/v1")
 *     .timeout(Duration.ofSeconds(45))
 *     .build();
 * }</pre>
 */
public class MailchkClientBuilder {
    
    private String apiKey;
    private String baseUrl;
    private Duration timeout;
    
    /**
     * Package-private constructor. Use {@link MailchkClient#builder()} to create instances.
     */
    MailchkClientBuilder() {
    }
    
    /**
     * Sets the Mailchk API key.
     * 
     * @param apiKey the API key (required)
     * @return this builder for method chaining
     * @throws IllegalArgumentException if apiKey is null or empty
     */
    public MailchkClientBuilder apiKey(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }
        this.apiKey = apiKey.trim();
        return this;
    }
    
    /**
     * Sets the base URL for the Mailchk API.
     * 
     * @param baseUrl the API base URL (optional, defaults to https://api.mailchk.io/v1)
     * @return this builder for method chaining
     * @throws IllegalArgumentException if baseUrl is invalid
     */
    public MailchkClientBuilder baseUrl(String baseUrl) {
        if (baseUrl != null) {
            baseUrl = baseUrl.trim();
            if (baseUrl.isEmpty()) {
                throw new IllegalArgumentException("Base URL cannot be empty");
            }
            if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
                throw new IllegalArgumentException("Base URL must start with http:// or https://");
            }
            // Remove trailing slash
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
        }
        this.baseUrl = baseUrl;
        return this;
    }
    
    /**
     * Sets the request timeout duration.
     * 
     * @param timeout the timeout duration (optional, defaults to 30 seconds)
     * @return this builder for method chaining
     * @throws IllegalArgumentException if timeout is null or negative
     */
    public MailchkClientBuilder timeout(Duration timeout) {
        if (timeout != null) {
            if (timeout.isNegative() || timeout.isZero()) {
                throw new IllegalArgumentException("Timeout must be positive");
            }
        }
        this.timeout = timeout;
        return this;
    }
    
    /**
     * Sets the request timeout in seconds.
     * 
     * @param timeoutSeconds the timeout in seconds (optional, defaults to 30)
     * @return this builder for method chaining
     * @throws IllegalArgumentException if timeoutSeconds is negative or zero
     */
    public MailchkClientBuilder timeoutSeconds(int timeoutSeconds) {
        if (timeoutSeconds <= 0) {
            throw new IllegalArgumentException("Timeout seconds must be positive");
        }
        return timeout(Duration.ofSeconds(timeoutSeconds));
    }
    
    /**
     * Sets the request timeout in milliseconds.
     * 
     * @param timeoutMillis the timeout in milliseconds (optional, defaults to 30000)
     * @return this builder for method chaining
     * @throws IllegalArgumentException if timeoutMillis is negative or zero
     */
    public MailchkClientBuilder timeoutMillis(long timeoutMillis) {
        if (timeoutMillis <= 0) {
            throw new IllegalArgumentException("Timeout milliseconds must be positive");
        }
        return timeout(Duration.ofMillis(timeoutMillis));
    }
    
    /**
     * Builds the MailchkClient with the configured settings.
     * 
     * @return a new MailchkClient instance
     * @throws IllegalStateException if required configuration is missing
     */
    public MailchkClient build() {
        if (apiKey == null) {
            throw new IllegalStateException("API key is required. Call apiKey() before build()");
        }
        
        return new MailchkClient(apiKey, baseUrl, timeout);
    }
}