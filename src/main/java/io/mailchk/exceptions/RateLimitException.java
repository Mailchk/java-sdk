package io.mailchk.exceptions;

/**
 * Thrown when the API rate limit has been exceeded.
 */
public class RateLimitException extends MailchkException {
    
    private final Integer retryAfter;
    
    public RateLimitException() {
        this("Rate limit exceeded. Please try again later.", null);
    }
    
    public RateLimitException(String message) {
        this(message, null);
    }
    
    public RateLimitException(String message, Integer retryAfter) {
        super(message, "RATE_LIMIT_EXCEEDED", 429);
        this.retryAfter = retryAfter;
    }
    
    public RateLimitException(String message, Integer retryAfter, Throwable cause) {
        super(message, "RATE_LIMIT_EXCEEDED", 429, cause);
        this.retryAfter = retryAfter;
    }
    
    /**
     * Gets the number of seconds to wait before retrying the request.
     * 
     * @return retry delay in seconds or null if not specified
     */
    public Integer getRetryAfter() {
        return retryAfter;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (retryAfter != null) {
            sb.append(" Retry after ").append(retryAfter).append(" seconds.");
        }
        return sb.toString();
    }
}