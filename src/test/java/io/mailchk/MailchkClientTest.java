package io.mailchk;

import io.mailchk.exceptions.*;
import io.mailchk.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MailchkClient.
 * These tests demonstrate the expected API usage but require a real API key for integration testing.
 */
class MailchkClientTest {
    
    private static final String TEST_API_KEY = "mk_test_1234567890abcdef";
    private MailchkClient client;
    
    @BeforeEach
    void setUp() {
        client = MailchkClient.builder()
            .apiKey(TEST_API_KEY)
            .build();
    }
    
    @Test
    void testBuilderValidation() {
        // Test builder validation
        assertThrows(IllegalArgumentException.class, () -> 
            MailchkClient.builder().apiKey(null).build());
        assertThrows(IllegalArgumentException.class, () -> 
            MailchkClient.builder().apiKey("").build());
        assertThrows(IllegalStateException.class, () -> 
            MailchkClient.builder().build());
    }
    
    @Test
    void testBuilderConfiguration() {
        MailchkClient customClient = MailchkClient.builder()
            .apiKey("test-key")
            .baseUrl("https://custom.api.com/v1")
            .timeout(Duration.ofSeconds(60))
            .build();
        
        assertNotNull(customClient);
        customClient.close();
    }
    
    @Test
    void testBuilderTimeoutMethods() {
        MailchkClient client1 = MailchkClient.builder()
            .apiKey("test-key")
            .timeoutSeconds(30)
            .build();
        
        MailchkClient client2 = MailchkClient.builder()
            .apiKey("test-key")
            .timeoutMillis(30000)
            .build();
        
        assertNotNull(client1);
        assertNotNull(client2);
        
        client1.close();
        client2.close();
    }
    
    @Test
    void testEmailValidation() {
        // Test email parameter validation
        assertThrows(IllegalArgumentException.class, () -> 
            client.validate(null));
        assertThrows(IllegalArgumentException.class, () -> 
            client.validate(""));
        assertThrows(IllegalArgumentException.class, () -> 
            client.validate("invalid-email"));
    }
    
    @Test
    void testBulkValidation() {
        // Test bulk validation parameter validation
        assertThrows(IllegalArgumentException.class, () -> 
            client.validateBulk(null));
        assertThrows(IllegalArgumentException.class, () -> 
            client.validateBulk(List.of()));
        
        // Test too many emails
        List<String> tooManyEmails = Arrays.asList(new String[101]);
        Arrays.fill(tooManyEmails.toArray(), "test@example.com");
        assertThrows(IllegalArgumentException.class, () -> 
            client.validateBulk(tooManyEmails));
        
        // Test invalid email in list
        assertThrows(IllegalArgumentException.class, () -> 
            client.validateBulk(List.of("valid@example.com", "invalid-email")));
    }
    
    @Test
    void testValidationResultHelperMethods() {
        // Test ValidationResult helper methods
        ValidationResult result = new ValidationResult();
        result.setValid(true);
        result.setRiskScore("low");
        result.setDeliverabilityScore(85);
        result.setSpf("pass");
        result.setDmarc("pass");
        
        assertTrue(result.isSafe());
        assertFalse(result.isHighRisk());
        assertTrue(result.isDeliverable());
        assertTrue(result.isDeliverable(80));
        assertFalse(result.isDeliverable(90));
        assertTrue(result.hasValidAuth());
        
        // Test high risk
        result.setRiskScore("critical");
        assertFalse(result.isSafe());
        assertTrue(result.isHighRisk());
    }
    
    @Test
    void testBulkValidationResultHelperMethods() {
        // Counts are computed from the results list, not deserialized from JSON
        ValidationResult valid1 = new ValidationResult();
        valid1.setValid(true);
        valid1.setDisposable(false);

        ValidationResult valid2 = new ValidationResult();
        valid2.setValid(true);
        valid2.setDisposable(false);

        ValidationResult disposable1 = new ValidationResult();
        disposable1.setValid(false);
        disposable1.setDisposable(true);

        BulkValidationResult result = new BulkValidationResult(
            Arrays.asList(valid1, valid2, disposable1)
        );

        assertEquals(3, result.getTotal());
        assertEquals(2, result.getValid());
        assertEquals(1, result.getInvalid());
        assertEquals(1, result.getDisposable());
        assertEquals(66.67, result.getValidPercentage(), 0.01);
        assertEquals(33.33, result.getDisposablePercentage(), 0.01);
    }

    @Test
    void testExceptionHierarchy() {
        // Test exception inheritance
        assertTrue(AuthenticationException.class.isAssignableFrom(AuthenticationException.class));
        assertTrue(MailchkException.class.isAssignableFrom(AuthenticationException.class));
        assertTrue(Exception.class.isAssignableFrom(MailchkException.class));
        
        // Test exception creation
        AuthenticationException authEx = new AuthenticationException();
        assertEquals("AUTHENTICATION_FAILED", authEx.getErrorCode());
        assertEquals(401, authEx.getStatusCode());
        
        RateLimitException rateEx = new RateLimitException("Rate limited", 60);
        assertEquals(60, rateEx.getRetryAfter());
        assertEquals("RATE_LIMIT_EXCEEDED", rateEx.getErrorCode());
        assertEquals(429, rateEx.getStatusCode());
    }
    
    @Test
    void testMxRecordEquality() {
        MxRecord record1 = new MxRecord("mail.example.com", 10);
        MxRecord record2 = new MxRecord("mail.example.com", 10);
        MxRecord record3 = new MxRecord("mail2.example.com", 10);
        
        assertEquals(record1, record2);
        assertNotEquals(record1, record3);
        assertEquals(record1.hashCode(), record2.hashCode());
    }
}