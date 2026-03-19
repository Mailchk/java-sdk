package io.mailchk.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.mailchk.MailchkClient;
import io.mailchk.exceptions.*;
import io.mailchk.models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

class MailchkClientIntegrationTest {
    
    private WireMockServer wireMockServer;
    private MailchkClient client;
    private String baseUrl;
    
    @BeforeEach
    void setUp() {
        // Start WireMock server on a random port
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        
        baseUrl = "http://localhost:" + wireMockServer.port();
        
        // Create client pointing to WireMock server
        client = MailchkClient.builder()
            .apiKey("test-api-key")
            .baseUrl(baseUrl)
            .timeout(Duration.ofSeconds(5))
            .build();
        
        // Configure WireMock
        WireMock.configureFor("localhost", wireMockServer.port());
    }
    
    @AfterEach
    void tearDown() {
        if (client != null) {
            client.close();
        }
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
    
    @Test
    void testSuccessfulValidation() throws MailchkException {
        // Mock successful validation response
        stubFor(post(urlEqualTo("/check"))
            .withHeader("X-API-Key", equalTo("test-api-key"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withRequestBody(containing("user@example.com"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "email": "user@example.com",
                        "domain": "example.com",
                        "valid": true,
                        "disposable": false,
                        "scam_domain": false,
                        "mx_exists": true,
                        "mx_records": [
                            {"exchange": "mail.example.com", "priority": 10},
                            {"exchange": "mail2.example.com", "priority": 20}
                        ],
                        "blacklisted_mx": false,
                        "free_email": false,
                        "did_you_mean": "",
                        "risk_score": "low",
                        "risk_factors": [],
                        "reason": null,
                        "email_provider": "Custom",
                        "deliverability_score": 95,
                        "spf": "pass",
                        "dmarc": "pass",
                        "normalized_email": "user@example.com",
                        "is_aliased": false,
                        "alias_type": null
                    }
                    """)));
        
        ValidationResult result = client.validate("user@example.com");
        
        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
        assertEquals("example.com", result.getDomain());
        assertTrue(result.isValid());
        assertFalse(result.isDisposable());
        assertFalse(result.isScamDomain());
        assertTrue(result.isMxExists());
        assertEquals(2, result.getMxRecords().size());
        assertEquals("mail.example.com", result.getMxRecords().get(0).getExchange());
        assertEquals(10, result.getMxRecords().get(0).getPriority());
        assertFalse(result.isBlacklistedMx());
        assertFalse(result.isFreeEmail());
        assertEquals("", result.getDidYouMean());
        assertEquals("low", result.getRiskScore());
        assertTrue(result.getRiskFactors().isEmpty());
        assertNull(result.getReason());
        assertEquals("Custom", result.getEmailProvider());
        assertEquals(95, result.getDeliverabilityScore());
        assertEquals("pass", result.getSpf());
        assertEquals("pass", result.getDmarc());
        assertEquals("user@example.com", result.getNormalizedEmail());
        assertFalse(result.isAliased());
        assertNull(result.getAliasType());
        
        // Test helper methods
        assertTrue(result.isSafe());
        assertFalse(result.isHighRisk());
        assertTrue(result.isDeliverable());
        assertTrue(result.hasValidAuth());
        
        // Verify the request was made correctly
        verify(postRequestedFor(urlEqualTo("/check"))
            .withHeader("X-API-Key", equalTo("test-api-key"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withHeader("User-Agent", matching("mailchk-java/.*")));
    }
    
    @Test
    void testDisposableEmailValidation() throws MailchkException {
        // Mock disposable email response
        stubFor(post(urlEqualTo("/check"))
            .withRequestBody(containing("temp@tempmail.com"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "email": "temp@tempmail.com",
                        "domain": "tempmail.com",
                        "valid": false,
                        "disposable": true,
                        "scam_domain": false,
                        "mx_exists": true,
                        "mx_records": [],
                        "blacklisted_mx": false,
                        "free_email": false,
                        "did_you_mean": "",
                        "risk_score": "critical",
                        "risk_factors": ["disposable_domain"],
                        "reason": "Disposable email provider",
                        "email_provider": "TempMail",
                        "deliverability_score": 10,
                        "spf": "none",
                        "dmarc": "none",
                        "normalized_email": "temp@tempmail.com",
                        "is_aliased": false,
                        "alias_type": null
                    }
                    """)));
        
        ValidationResult result = client.validate("temp@tempmail.com");
        
        assertNotNull(result);
        assertEquals("temp@tempmail.com", result.getEmail());
        assertFalse(result.isValid());
        assertTrue(result.isDisposable());
        assertEquals("critical", result.getRiskScore());
        assertEquals(1, result.getRiskFactors().size());
        assertEquals("disposable_domain", result.getRiskFactors().get(0));
        assertEquals("Disposable email provider", result.getReason());
        assertEquals(10, result.getDeliverabilityScore());
        
        // Test helper methods
        assertFalse(result.isSafe());
        assertTrue(result.isHighRisk());
        assertFalse(result.isDeliverable());
        assertFalse(result.hasValidAuth());
    }
    
    @Test
    void testBulkValidation() throws MailchkException {
        // Mock bulk validation response
        stubFor(post(urlEqualTo("/check/bulk"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "total": 3,
                        "valid": 2,
                        "invalid": 1,
                        "disposable": 1,
                        "results": [
                            {
                                "email": "user1@gmail.com",
                                "domain": "gmail.com",
                                "valid": true,
                                "disposable": false,
                                "scam_domain": false,
                                "mx_exists": true,
                                "mx_records": [],
                                "blacklisted_mx": false,
                                "free_email": true,
                                "did_you_mean": "",
                                "risk_score": "low",
                                "risk_factors": ["free_provider"],
                                "reason": null,
                                "email_provider": "Gmail",
                                "deliverability_score": 90,
                                "spf": "pass",
                                "dmarc": "pass",
                                "normalized_email": "user1@gmail.com",
                                "is_aliased": false,
                                "alias_type": null
                            },
                            {
                                "email": "user2@company.com",
                                "domain": "company.com",
                                "valid": true,
                                "disposable": false,
                                "scam_domain": false,
                                "mx_exists": true,
                                "mx_records": [],
                                "blacklisted_mx": false,
                                "free_email": false,
                                "did_you_mean": "",
                                "risk_score": "low",
                                "risk_factors": [],
                                "reason": null,
                                "email_provider": "Custom",
                                "deliverability_score": 95,
                                "spf": "pass",
                                "dmarc": "fail",
                                "normalized_email": "user2@company.com",
                                "is_aliased": false,
                                "alias_type": null
                            },
                            {
                                "email": "invalid@tempmail.org",
                                "domain": "tempmail.org",
                                "valid": false,
                                "disposable": true,
                                "scam_domain": false,
                                "mx_exists": false,
                                "mx_records": [],
                                "blacklisted_mx": false,
                                "free_email": false,
                                "did_you_mean": "",
                                "risk_score": "critical",
                                "risk_factors": ["disposable_domain", "no_mx_records"],
                                "reason": "No MX records found",
                                "email_provider": null,
                                "deliverability_score": 5,
                                "spf": "none",
                                "dmarc": "none",
                                "normalized_email": "invalid@tempmail.org",
                                "is_aliased": false,
                                "alias_type": null
                            }
                        ]
                    }
                    """)));
        
        List<String> emails = List.of("user1@gmail.com", "user2@company.com", "invalid@tempmail.org");
        BulkValidationResult result = client.validateBulk(emails);
        
        assertNotNull(result);
        assertEquals(3, result.getTotal());
        assertEquals(2, result.getValid());
        assertEquals(1, result.getInvalid());
        assertEquals(1, result.getDisposable());
        assertEquals(66.67, result.getValidPercentage(), 0.01);
        assertEquals(33.33, result.getDisposablePercentage(), 0.01);
        
        List<ValidationResult> results = result.getResults();
        assertEquals(3, results.size());
        
        // Check first result
        ValidationResult first = results.get(0);
        assertEquals("user1@gmail.com", first.getEmail());
        assertTrue(first.isValid());
        assertFalse(first.isDisposable());
        assertTrue(first.isFreeEmail());
        assertEquals("Gmail", first.getEmailProvider());
        
        // Check second result
        ValidationResult second = results.get(1);
        assertEquals("user2@company.com", second.getEmail());
        assertTrue(second.isValid());
        assertFalse(second.isDisposable());
        assertFalse(second.isFreeEmail());
        assertEquals("Custom", second.getEmailProvider());
        
        // Check third result
        ValidationResult third = results.get(2);
        assertEquals("invalid@tempmail.org", third.getEmail());
        assertFalse(third.isValid());
        assertTrue(third.isDisposable());
        assertEquals("critical", third.getRiskScore());
    }
    
    @Test
    void testUsageInfo() throws MailchkException {
        // Mock usage response
        stubFor(get(urlEqualTo("/usage"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "plan": "pro",
                        "used": 750,
                        "limit": 1000,
                        "remaining": 250,
                        "reset_date": "2024-12-01T10:30:00"
                    }
                    """)));
        
        UsageInfo usage = client.getUsage();
        
        assertNotNull(usage);
        assertEquals("pro", usage.getPlan());
        assertEquals(750, usage.getUsed());
        assertEquals(1000, usage.getLimit());
        assertEquals(250, usage.getRemaining());
        assertEquals("2024-12-01T10:30:00", usage.getResetDate());
        assertEquals(75.0, usage.getPercentageUsed(), 0.01);
        assertFalse(usage.isQuotaNearlyExhausted());
        assertFalse(usage.isQuotaExhausted());
    }
    
    @Test
    void testAuthenticationError() {
        // Mock authentication error
        stubFor(post(urlEqualTo("/check"))
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "error": "Invalid API key",
                        "code": "AUTHENTICATION_FAILED"
                    }
                    """)));
        
        assertThrows(AuthenticationException.class, () -> 
            client.validate("user@example.com"));
    }
    
    @Test
    void testRateLimitError() {
        // Mock rate limit error
        stubFor(post(urlEqualTo("/check"))
            .willReturn(aResponse()
                .withStatus(429)
                .withHeader("Content-Type", "application/json")
                .withHeader("Retry-After", "60")
                .withBody("""
                    {
                        "error": "Rate limit exceeded",
                        "code": "RATE_LIMIT_EXCEEDED"
                    }
                    """)));
        
        RateLimitException exception = assertThrows(RateLimitException.class, () -> 
            client.validate("user@example.com"));
        
        assertEquals(60, exception.getRetryAfter());
    }
    
    @Test
    void testValidationError() {
        // Mock validation error
        stubFor(post(urlEqualTo("/check"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "message": "Invalid email format",
                        "code": "VALIDATION_ERROR"
                    }
                    """)));
        
        ValidationException exception = assertThrows(ValidationException.class, () -> 
            client.validate("invalid-email"));
        
        assertEquals("Invalid email format", exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
    }
    
    @Test
    void testServerError() {
        // Mock server error
        stubFor(post(urlEqualTo("/check"))
            .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "error": "Internal server error"
                    }
                    """)));
        
        ApiException exception = assertThrows(ApiException.class, () -> 
            client.validate("user@example.com"));
        
        assertEquals(500, exception.getStatusCode());
    }
    
    @Test
    void testAsyncValidation() throws ExecutionException, InterruptedException {
        // Mock successful validation response
        stubFor(post(urlEqualTo("/check"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "email": "async@example.com",
                        "domain": "example.com",
                        "valid": true,
                        "disposable": false,
                        "scam_domain": false,
                        "mx_exists": true,
                        "mx_records": [],
                        "blacklisted_mx": false,
                        "free_email": false,
                        "did_you_mean": "",
                        "risk_score": "low",
                        "risk_factors": [],
                        "reason": null,
                        "email_provider": null,
                        "deliverability_score": 85,
                        "spf": "pass",
                        "dmarc": "pass",
                        "normalized_email": "async@example.com",
                        "is_aliased": false,
                        "alias_type": null
                    }
                    """)));
        
        CompletableFuture<ValidationResult> future = client.validateAsync("async@example.com");
        ValidationResult result = future.get();
        
        assertNotNull(result);
        assertEquals("async@example.com", result.getEmail());
        assertTrue(result.isValid());
        assertFalse(result.isDisposable());
        assertEquals("low", result.getRiskScore());
        assertEquals(85, result.getDeliverabilityScore());
    }
    
    @Test
    void testHelperMethods() throws MailchkException {
        // Mock validation response
        stubFor(post(urlEqualTo("/check"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "email": "test@example.com",
                        "domain": "example.com",
                        "valid": true,
                        "disposable": false,
                        "scam_domain": false,
                        "mx_exists": true,
                        "mx_records": [],
                        "blacklisted_mx": false,
                        "free_email": false,
                        "did_you_mean": "",
                        "risk_score": "medium",
                        "risk_factors": ["role_account"],
                        "reason": null,
                        "email_provider": null,
                        "deliverability_score": 70,
                        "spf": "pass",
                        "dmarc": "pass",
                        "normalized_email": "test@example.com",
                        "is_aliased": false,
                        "alias_type": null
                    }
                    """)));
        
        // Test helper methods
        assertTrue(client.isValid("test@example.com"));
        assertFalse(client.isDisposable("test@example.com"));
        assertEquals("medium", client.getRiskScore("test@example.com"));
        assertEquals(70, client.getDeliverabilityScore("test@example.com"));
        
        // Verify multiple requests were made (one for each helper method)
        verify(4, postRequestedFor(urlEqualTo("/check")));
    }
}