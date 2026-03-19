package io.mailchk.exceptions;

/**
 * Thrown when request validation fails due to invalid input parameters.
 */
public class ValidationException extends MailchkException {
    
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", 400);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, "VALIDATION_ERROR", 400, cause);
    }
    
    public ValidationException(String message, String errorCode) {
        super(message, errorCode, 400);
    }
}