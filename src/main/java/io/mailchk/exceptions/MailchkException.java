package io.mailchk.exceptions;

/**
 * Base exception class for all Mailchk SDK errors.
 */
public class MailchkException extends Exception {
    
    private final String errorCode;
    private final int statusCode;
    
    public MailchkException(String message) {
        this(message, null, 0);
    }
    
    public MailchkException(String message, String errorCode) {
        this(message, errorCode, 0);
    }
    
    public MailchkException(String message, Throwable cause) {
        this(message, null, 0, cause);
    }
    
    public MailchkException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
    
    public MailchkException(String message, String errorCode, int statusCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
    
    /**
     * Gets the error code associated with this exception.
     * 
     * @return error code or null if not available
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Gets the HTTP status code associated with this exception.
     * 
     * @return HTTP status code or 0 if not available
     */
    public int getStatusCode() {
        return statusCode;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        if (errorCode != null) {
            sb.append(" [").append(errorCode).append("]");
        }
        if (statusCode > 0) {
            sb.append(" (HTTP ").append(statusCode).append(")");
        }
        sb.append(": ").append(getMessage());
        return sb.toString();
    }
}