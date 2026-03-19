package io.mailchk.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationResultTest {
    
    private ValidationResult validResult;
    private ValidationResult invalidResult;
    private ValidationResult highRiskResult;
    
    @BeforeEach
    void setUp() {
        // Valid email result
        validResult = new ValidationResult();
        validResult.setEmail("user@example.com");
        validResult.setDomain("example.com");
        validResult.setValid(true);
        validResult.setDisposable(false);
        validResult.setScamDomain(false);
        validResult.setMxExists(true);
        validResult.setBlacklistedMx(false);
        validResult.setFreeEmail(false);
        validResult.setDidYouMean("");
        validResult.setRiskScore("low");
        validResult.setRiskFactors(List.of());
        validResult.setEmailProvider("Custom");
        validResult.setDeliverabilityScore(95);
        validResult.setSpf("pass");
        validResult.setDmarc("pass");
        validResult.setNormalizedEmail("user@example.com");
        validResult.setAliased(false);
        validResult.setAliasType(null);
        
        // Invalid email result
        invalidResult = new ValidationResult();
        invalidResult.setEmail("invalid@fakeDomain.xyz");
        invalidResult.setDomain("fakeDomain.xyz");
        invalidResult.setValid(false);
        invalidResult.setDisposable(true);
        invalidResult.setScamDomain(false);
        invalidResult.setMxExists(false);
        invalidResult.setBlacklistedMx(false);
        invalidResult.setFreeEmail(false);
        invalidResult.setDidYouMean("");
        invalidResult.setRiskScore("critical");
        invalidResult.setRiskFactors(List.of("disposable_domain", "no_mx_records"));
        invalidResult.setReason("No MX records found for domain");
        invalidResult.setEmailProvider(null);
        invalidResult.setDeliverabilityScore(5);
        invalidResult.setSpf("none");
        invalidResult.setDmarc("none");
        invalidResult.setNormalizedEmail("invalid@fakedomain.xyz");
        invalidResult.setAliased(false);
        invalidResult.setAliasType(null);
        
        // High risk email result
        highRiskResult = new ValidationResult();
        highRiskResult.setEmail("admin@suspicious.com");
        highRiskResult.setDomain("suspicious.com");
        highRiskResult.setValid(true);
        highRiskResult.setDisposable(false);
        highRiskResult.setScamDomain(true);
        highRiskResult.setMxExists(true);
        highRiskResult.setBlacklistedMx(true);
        highRiskResult.setFreeEmail(false);
        highRiskResult.setDidYouMean("");
        highRiskResult.setRiskScore("high");
        highRiskResult.setRiskFactors(List.of("scam_domain", "blacklisted_mx", "role_account"));
        highRiskResult.setEmailProvider(null);
        highRiskResult.setDeliverabilityScore(25);
        highRiskResult.setSpf("fail");
        highRiskResult.setDmarc("fail");
        highRiskResult.setNormalizedEmail("admin@suspicious.com");
        highRiskResult.setAliased(false);
        highRiskResult.setAliasType(null);
    }
    
    @Test
    void testValidEmailProperties() {
        assertEquals("user@example.com", validResult.getEmail());
        assertEquals("example.com", validResult.getDomain());
        assertTrue(validResult.isValid());
        assertFalse(validResult.isDisposable());
        assertFalse(validResult.isScamDomain());
        assertTrue(validResult.isMxExists());
        assertFalse(validResult.isBlacklistedMx());
        assertFalse(validResult.isFreeEmail());
        assertEquals("", validResult.getDidYouMean());
        assertEquals("low", validResult.getRiskScore());
        assertTrue(validResult.getRiskFactors().isEmpty());
        assertNull(validResult.getReason());
        assertEquals("Custom", validResult.getEmailProvider());
        assertEquals(95, validResult.getDeliverabilityScore());
        assertEquals("pass", validResult.getSpf());
        assertEquals("pass", validResult.getDmarc());
        assertEquals("user@example.com", validResult.getNormalizedEmail());
        assertFalse(validResult.isAliased());
        assertNull(validResult.getAliasType());
    }
    
    @Test
    void testInvalidEmailProperties() {
        assertEquals("invalid@fakeDomain.xyz", invalidResult.getEmail());
        assertEquals("fakeDomain.xyz", invalidResult.getDomain());
        assertFalse(invalidResult.isValid());
        assertTrue(invalidResult.isDisposable());
        assertFalse(invalidResult.isScamDomain());
        assertFalse(invalidResult.isMxExists());
        assertEquals("critical", invalidResult.getRiskScore());
        assertEquals(2, invalidResult.getRiskFactors().size());
        assertTrue(invalidResult.getRiskFactors().contains("disposable_domain"));
        assertTrue(invalidResult.getRiskFactors().contains("no_mx_records"));
        assertEquals("No MX records found for domain", invalidResult.getReason());
        assertEquals(5, invalidResult.getDeliverabilityScore());
        assertEquals("none", invalidResult.getSpf());
        assertEquals("none", invalidResult.getDmarc());
    }
    
    @Test
    void testHelperMethods_ValidEmail() {
        // Test isSafe() method
        assertTrue(validResult.isSafe(), "Valid email with low risk should be safe");
        
        // Test isHighRisk() method
        assertFalse(validResult.isHighRisk(), "Low risk email should not be high risk");
        
        // Test isDeliverable() methods
        assertTrue(validResult.isDeliverable(), "Email with 95 score should be deliverable (default threshold 50)");
        assertTrue(validResult.isDeliverable(90), "Email with 95 score should be deliverable (threshold 90)");
        assertFalse(validResult.isDeliverable(96), "Email with 95 score should not be deliverable (threshold 96)");
        
        // Test hasValidAuth() method
        assertTrue(validResult.hasValidAuth(), "Email with SPF=pass and DMARC=pass should have valid auth");
    }
    
    @Test
    void testHelperMethods_InvalidEmail() {
        // Test isSafe() method
        assertFalse(invalidResult.isSafe(), "Invalid email should not be safe");
        
        // Test isHighRisk() method
        assertTrue(invalidResult.isHighRisk(), "Critical risk email should be high risk");
        
        // Test isDeliverable() methods
        assertFalse(invalidResult.isDeliverable(), "Email with 5 score should not be deliverable");
        assertFalse(invalidResult.isDeliverable(1), "Email with 5 score should not be deliverable (low threshold)");
        
        // Test hasValidAuth() method
        assertFalse(invalidResult.hasValidAuth(), "Email with SPF=none and DMARC=none should not have valid auth");
    }
    
    @Test
    void testHelperMethods_HighRiskEmail() {
        // Test isSafe() method
        assertFalse(highRiskResult.isSafe(), "High risk email should not be safe even if valid");
        
        // Test isHighRisk() method
        assertTrue(highRiskResult.isHighRisk(), "High risk email should be high risk");
        
        // Test scam domain detection
        assertTrue(highRiskResult.isScamDomain(), "Should detect scam domain");
        
        // Test deliverability with low score
        assertFalse(highRiskResult.isDeliverable(), "Email with 25 score should not be deliverable");
        assertTrue(highRiskResult.isDeliverable(20), "Email with 25 score should be deliverable with threshold 20");
    }
    
    @Test
    void testRiskScoreBoundaries() {
        // Test medium risk
        ValidationResult mediumRisk = new ValidationResult();
        mediumRisk.setValid(true);
        mediumRisk.setRiskScore("medium");
        assertTrue(mediumRisk.isSafe(), "Medium risk should be safe");
        assertFalse(mediumRisk.isHighRisk(), "Medium risk should not be high risk");
        
        // Test high risk
        ValidationResult highRisk = new ValidationResult();
        highRisk.setValid(true);
        highRisk.setRiskScore("high");
        assertFalse(highRisk.isSafe(), "High risk should not be safe");
        assertTrue(highRisk.isHighRisk(), "High risk should be high risk");
        
        // Test critical risk
        ValidationResult criticalRisk = new ValidationResult();
        criticalRisk.setValid(true);
        criticalRisk.setRiskScore("critical");
        assertFalse(criticalRisk.isSafe(), "Critical risk should not be safe");
        assertTrue(criticalRisk.isHighRisk(), "Critical risk should be high risk");
    }
    
    @Test
    void testAuthenticationMethods() {
        // Test various SPF/DMARC combinations
        ValidationResult result = new ValidationResult();
        
        // Both pass
        result.setSpf("pass");
        result.setDmarc("pass");
        assertTrue(result.hasValidAuth());
        
        // SPF pass, DMARC fail
        result.setSpf("pass");
        result.setDmarc("fail");
        assertFalse(result.hasValidAuth());
        
        // SPF fail, DMARC pass
        result.setSpf("fail");
        result.setDmarc("pass");
        assertFalse(result.hasValidAuth());
        
        // Both fail
        result.setSpf("fail");
        result.setDmarc("fail");
        assertFalse(result.hasValidAuth());
        
        // Both none
        result.setSpf("none");
        result.setDmarc("none");
        assertFalse(result.hasValidAuth());
    }
    
    @Test
    void testToString() {
        String str = validResult.toString();
        assertTrue(str.contains("user@example.com"));
        assertTrue(str.contains("valid=true"));
        assertTrue(str.contains("disposable=false"));
        assertTrue(str.contains("riskScore='low'"));
        assertTrue(str.contains("deliverabilityScore=95"));
    }
    
    @Test
    void testAliasDetection() {
        ValidationResult aliasedResult = new ValidationResult();
        aliasedResult.setEmail("user+tag@gmail.com");
        aliasedResult.setNormalizedEmail("user@gmail.com");
        aliasedResult.setAliased(true);
        aliasedResult.setAliasType("plus_addressing");
        
        assertTrue(aliasedResult.isAliased());
        assertEquals("plus_addressing", aliasedResult.getAliasType());
        assertEquals("user@gmail.com", aliasedResult.getNormalizedEmail());
    }
}