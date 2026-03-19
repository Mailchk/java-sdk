package io.mailchk.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {
    
    @Test
    void testMailchkException() {
        // Test basic constructor
        MailchkException ex1 = new MailchkException("Basic error");
        assertEquals("Basic error", ex1.getMessage());
        assertNull(ex1.getErrorCode());
        assertEquals(0, ex1.getStatusCode());
        
        // Test constructor with error code
        MailchkException ex2 = new MailchkException("Error with code", "ERROR_CODE");
        assertEquals("Error with code", ex2.getMessage());
        assertEquals("ERROR_CODE", ex2.getErrorCode());
        assertEquals(0, ex2.getStatusCode());
        
        // Test constructor with cause
        Exception cause = new RuntimeException("Root cause");
        MailchkException ex3 = new MailchkException("Error with cause", cause);
        assertEquals("Error with cause", ex3.getMessage());
        assertEquals(cause, ex3.getCause());
        assertNull(ex3.getErrorCode());
        assertEquals(0, ex3.getStatusCode());
        
        // Test full constructor
        MailchkException ex4 = new MailchkException("Full error", "FULL_CODE", 500);
        assertEquals("Full error", ex4.getMessage());
        assertEquals("FULL_CODE", ex4.getErrorCode());
        assertEquals(500, ex4.getStatusCode());
        
        // Test full constructor with cause
        MailchkException ex5 = new MailchkException("Full with cause", "CAUSE_CODE", 400, cause);
        assertEquals("Full with cause", ex5.getMessage());
        assertEquals("CAUSE_CODE", ex5.getErrorCode());
        assertEquals(400, ex5.getStatusCode());
        assertEquals(cause, ex5.getCause());
    }
    
    @Test
    void testAuthenticationException() {
        // Test default constructor
        AuthenticationException ex1 = new AuthenticationException();
        assertEquals("Authentication failed. Please check your API key.", ex1.getMessage());
        assertEquals("AUTHENTICATION_FAILED", ex1.getErrorCode());
        assertEquals(401, ex1.getStatusCode());
        
        // Test constructor with message
        AuthenticationException ex2 = new AuthenticationException("Custom auth error");
        assertEquals("Custom auth error", ex2.getMessage());
        assertEquals("AUTHENTICATION_FAILED", ex2.getErrorCode());
        assertEquals(401, ex2.getStatusCode());
        
        // Test constructor with cause
        Exception cause = new RuntimeException("Network error");
        AuthenticationException ex3 = new AuthenticationException("Auth error with cause", cause);
        assertEquals("Auth error with cause", ex3.getMessage());
        assertEquals("AUTHENTICATION_FAILED", ex3.getErrorCode());
        assertEquals(401, ex3.getStatusCode());
        assertEquals(cause, ex3.getCause());
        
        // Test inheritance
        assertTrue(ex1 instanceof MailchkException);
        assertTrue(ex1 instanceof Exception);
    }
    
    @Test
    void testRateLimitException() {
        // Test default constructor
        RateLimitException ex1 = new RateLimitException();
        assertEquals("Rate limit exceeded. Please try again later.", ex1.getMessage());
        assertEquals("RATE_LIMIT_EXCEEDED", ex1.getErrorCode());
        assertEquals(429, ex1.getStatusCode());
        assertNull(ex1.getRetryAfter());
        
        // Test constructor with message
        RateLimitException ex2 = new RateLimitException("Custom rate limit error");
        assertEquals("Custom rate limit error", ex2.getMessage());
        assertEquals("RATE_LIMIT_EXCEEDED", ex2.getErrorCode());
        assertEquals(429, ex2.getStatusCode());
        assertNull(ex2.getRetryAfter());
        
        // Test constructor with retry after
        RateLimitException ex3 = new RateLimitException("Rate limited", 60);
        assertEquals("Rate limited", ex3.getMessage());
        assertEquals("RATE_LIMIT_EXCEEDED", ex3.getErrorCode());
        assertEquals(429, ex3.getStatusCode());
        assertEquals(60, ex3.getRetryAfter());
        
        // Test constructor with cause
        Exception cause = new RuntimeException("HTTP error");
        RateLimitException ex4 = new RateLimitException("Rate limit with cause", 120, cause);
        assertEquals("Rate limit with cause", ex4.getMessage());
        assertEquals("RATE_LIMIT_EXCEEDED", ex4.getErrorCode());
        assertEquals(429, ex4.getStatusCode());
        assertEquals(120, ex4.getRetryAfter());
        assertEquals(cause, ex4.getCause());
        
        // Test inheritance
        assertTrue(ex1 instanceof MailchkException);
        assertTrue(ex1 instanceof Exception);
    }
    
    @Test
    void testValidationException() {
        // Test basic constructor
        ValidationException ex1 = new ValidationException("Validation failed");
        assertEquals("Validation failed", ex1.getMessage());
        assertEquals("VALIDATION_ERROR", ex1.getErrorCode());
        assertEquals(400, ex1.getStatusCode());
        
        // Test constructor with cause
        Exception cause = new IllegalArgumentException("Invalid input");
        ValidationException ex2 = new ValidationException("Validation error with cause", cause);
        assertEquals("Validation error with cause", ex2.getMessage());
        assertEquals("VALIDATION_ERROR", ex2.getErrorCode());
        assertEquals(400, ex2.getStatusCode());
        assertEquals(cause, ex2.getCause());
        
        // Test constructor with custom error code
        ValidationException ex3 = new ValidationException("Custom validation", "CUSTOM_CODE");
        assertEquals("Custom validation", ex3.getMessage());
        assertEquals("CUSTOM_CODE", ex3.getErrorCode());
        assertEquals(400, ex3.getStatusCode());
        
        // Test inheritance
        assertTrue(ex1 instanceof MailchkException);
        assertTrue(ex1 instanceof Exception);
    }
    
    @Test
    void testApiException() {
        // Test basic constructor
        ApiException ex1 = new ApiException("API error");
        assertEquals("API error", ex1.getMessage());
        assertEquals("API_ERROR", ex1.getErrorCode());
        assertEquals(0, ex1.getStatusCode());
        
        // Test constructor with status code
        ApiException ex2 = new ApiException("Server error", 500);
        assertEquals("Server error", ex2.getMessage());
        assertEquals("API_ERROR", ex2.getErrorCode());
        assertEquals(500, ex2.getStatusCode());
        
        // Test constructor with custom error code
        ApiException ex3 = new ApiException("Custom error", "CUSTOM_ERROR");
        assertEquals("Custom error", ex3.getMessage());
        assertEquals("CUSTOM_ERROR", ex3.getErrorCode());
        assertEquals(0, ex3.getStatusCode());
        
        // Test full constructor
        ApiException ex4 = new ApiException("Full error", "FULL_ERROR", 503);
        assertEquals("Full error", ex4.getMessage());
        assertEquals("FULL_ERROR", ex4.getErrorCode());
        assertEquals(503, ex4.getStatusCode());
        
        // Test constructor with cause
        Exception cause = new RuntimeException("Network failure");
        ApiException ex5 = new ApiException("API error with cause", cause);
        assertEquals("API error with cause", ex5.getMessage());
        assertEquals("API_ERROR", ex5.getErrorCode());
        assertEquals(0, ex5.getStatusCode());
        assertEquals(cause, ex5.getCause());
        
        // Test full constructor with cause
        ApiException ex6 = new ApiException("Full with cause", "CAUSE_ERROR", 502, cause);
        assertEquals("Full with cause", ex6.getMessage());
        assertEquals("CAUSE_ERROR", ex6.getErrorCode());
        assertEquals(502, ex6.getStatusCode());
        assertEquals(cause, ex6.getCause());
        
        // Test inheritance
        assertTrue(ex1 instanceof MailchkException);
        assertTrue(ex1 instanceof Exception);
    }
    
    @Test
    void testToString() {
        // Test MailchkException toString
        MailchkException ex1 = new MailchkException("Test error", "TEST_CODE", 400);
        String str1 = ex1.toString();
        assertTrue(str1.contains("MailchkException"));
        assertTrue(str1.contains("[TEST_CODE]"));
        assertTrue(str1.contains("(HTTP 400)"));
        assertTrue(str1.contains("Test error"));
        
        // Test without error code
        MailchkException ex2 = new MailchkException("Simple error");
        String str2 = ex2.toString();
        assertTrue(str2.contains("MailchkException"));
        assertFalse(str2.contains("["));
        assertFalse(str2.contains("HTTP"));
        assertTrue(str2.contains("Simple error"));
        
        // Test RateLimitException toString with retry after
        RateLimitException ex3 = new RateLimitException("Rate limited", 30);
        String str3 = ex3.toString();
        assertTrue(str3.contains("RateLimitException"));
        assertTrue(str3.contains("Retry after 30 seconds"));
        
        // Test RateLimitException toString without retry after
        RateLimitException ex4 = new RateLimitException("Rate limited", null);
        String str4 = ex4.toString();
        assertTrue(str4.contains("RateLimitException"));
        assertFalse(str4.contains("Retry after"));
    }
    
    @Test
    void testInheritanceChain() {
        // Verify all custom exceptions inherit from MailchkException
        assertTrue(MailchkException.class.isAssignableFrom(AuthenticationException.class));
        assertTrue(MailchkException.class.isAssignableFrom(RateLimitException.class));
        assertTrue(MailchkException.class.isAssignableFrom(ValidationException.class));
        assertTrue(MailchkException.class.isAssignableFrom(ApiException.class));
        
        // Verify MailchkException inherits from Exception
        assertTrue(Exception.class.isAssignableFrom(MailchkException.class));
        
        // Verify all exceptions can be caught as MailchkException
        MailchkException[] exceptions = {
            new AuthenticationException(),
            new RateLimitException(),
            new ValidationException("test"),
            new ApiException("test")
        };
        
        for (MailchkException ex : exceptions) {
            assertTrue(ex instanceof MailchkException);
            assertTrue(ex instanceof Exception);
        }
    }
}