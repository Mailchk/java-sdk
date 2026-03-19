package io.mailchk.exceptions;

/**
 * Thrown when authentication fails due to invalid API key or unauthorized access.
 */
public class AuthenticationException extends MailchkException {
    
    public AuthenticationException() {
        super("Authentication failed. Please check your API key.", "AUTHENTICATION_FAILED", 401);
    }
    
    public AuthenticationException(String message) {
        super(message, "AUTHENTICATION_FAILED", 401);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, "AUTHENTICATION_FAILED", 401, cause);
    }
}