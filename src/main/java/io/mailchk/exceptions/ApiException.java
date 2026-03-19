package io.mailchk.exceptions;

/**
 * Thrown when a general API error occurs (server errors, network issues, etc.).
 */
public class ApiException extends MailchkException {
    
    public ApiException(String message) {
        super(message, "API_ERROR");
    }
    
    public ApiException(String message, int statusCode) {
        super(message, "API_ERROR", statusCode);
    }
    
    public ApiException(String message, String errorCode) {
        super(message, errorCode);
    }
    
    public ApiException(String message, String errorCode, int statusCode) {
        super(message, errorCode, statusCode);
    }
    
    public ApiException(String message, Throwable cause) {
        super(message, "API_ERROR", 0, cause);
    }
    
    public ApiException(String message, String errorCode, int statusCode, Throwable cause) {
        super(message, errorCode, statusCode, cause);
    }
}