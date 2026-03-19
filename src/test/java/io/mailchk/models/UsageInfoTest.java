package io.mailchk.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UsageInfoTest {
    
    private UsageInfo usageInfo;
    
    @BeforeEach
    void setUp() {
        usageInfo = new UsageInfo("pro", 750, 1000, 250, "2024-12-01T10:30:00");
    }
    
    @Test
    void testBasicProperties() {
        assertEquals("pro", usageInfo.getPlan());
        assertEquals(750, usageInfo.getUsed());
        assertEquals(1000, usageInfo.getLimit());
        assertEquals(250, usageInfo.getRemaining());
        assertEquals("2024-12-01T10:30:00", usageInfo.getResetDate());
    }
    
    @Test
    void testPercentageUsed() {
        assertEquals(75.0, usageInfo.getPercentageUsed(), 0.01);
    }
    
    @Test
    void testQuotaNearlyExhausted() {
        // 75% usage - not nearly exhausted
        assertFalse(usageInfo.isQuotaNearlyExhausted());
        
        // 95% usage - nearly exhausted
        usageInfo.setUsed(950);
        usageInfo.setRemaining(50);
        assertTrue(usageInfo.isQuotaNearlyExhausted());
        
        // Exactly 90% - not nearly exhausted (must be > 90%)
        usageInfo.setUsed(900);
        usageInfo.setRemaining(100);
        assertFalse(usageInfo.isQuotaNearlyExhausted());
        
        // 90.1% - nearly exhausted
        usageInfo.setUsed(901);
        usageInfo.setRemaining(99);
        assertTrue(usageInfo.isQuotaNearlyExhausted());
    }
    
    @Test
    void testQuotaExhausted() {
        // 250 remaining - not exhausted
        assertFalse(usageInfo.isQuotaExhausted());
        
        // 0 remaining - exhausted
        usageInfo.setRemaining(0);
        assertTrue(usageInfo.isQuotaExhausted());
        
        // Negative remaining - exhausted
        usageInfo.setRemaining(-10);
        assertTrue(usageInfo.isQuotaExhausted());
    }
    
    @Test
    void testZeroLimit() {
        UsageInfo zeroLimitUsage = new UsageInfo("free", 0, 0, 0, "2024-12-01T10:30:00");
        assertEquals(0.0, zeroLimitUsage.getPercentageUsed());
        assertFalse(zeroLimitUsage.isQuotaNearlyExhausted());
        assertTrue(zeroLimitUsage.isQuotaExhausted());
    }
    
    @Test
    void testResetDateTime() {
        LocalDateTime resetDateTime = usageInfo.getResetDateTime();
        assertNotNull(resetDateTime);
        assertEquals(2024, resetDateTime.getYear());
        assertEquals(12, resetDateTime.getMonthValue());
        assertEquals(1, resetDateTime.getDayOfMonth());
        assertEquals(10, resetDateTime.getHour());
        assertEquals(30, resetDateTime.getMinute());
        assertEquals(0, resetDateTime.getSecond());
    }
    
    @Test
    void testInvalidResetDate() {
        usageInfo.setResetDate("invalid-date");
        assertNull(usageInfo.getResetDateTime());
        
        usageInfo.setResetDate(null);
        assertNull(usageInfo.getResetDateTime());
        
        usageInfo.setResetDate("");
        assertNull(usageInfo.getResetDateTime());
    }
    
    @Test
    void testSettersAndGetters() {
        UsageInfo newUsage = new UsageInfo();
        
        newUsage.setPlan("enterprise");
        newUsage.setUsed(5000);
        newUsage.setLimit(10000);
        newUsage.setRemaining(5000);
        newUsage.setResetDate("2024-12-15T09:00:00");
        
        assertEquals("enterprise", newUsage.getPlan());
        assertEquals(5000, newUsage.getUsed());
        assertEquals(10000, newUsage.getLimit());
        assertEquals(5000, newUsage.getRemaining());
        assertEquals("2024-12-15T09:00:00", newUsage.getResetDate());
        assertEquals(50.0, newUsage.getPercentageUsed(), 0.01);
        assertFalse(newUsage.isQuotaNearlyExhausted());
        assertFalse(newUsage.isQuotaExhausted());
    }
    
    @Test
    void testToString() {
        String str = usageInfo.toString();
        assertTrue(str.contains("plan='pro'"));
        assertTrue(str.contains("used=750"));
        assertTrue(str.contains("limit=1000"));
        assertTrue(str.contains("remaining=250"));
        assertTrue(str.contains("resetDate='2024-12-01T10:30:00'"));
        assertTrue(str.contains("percentageUsed=75.0%"));
    }
    
    @Test
    void testConstructorOverloads() {
        // Test empty constructor
        UsageInfo emptyUsage = new UsageInfo();
        assertNull(emptyUsage.getPlan());
        assertEquals(0, emptyUsage.getUsed());
        assertEquals(0, emptyUsage.getLimit());
        assertEquals(0, emptyUsage.getRemaining());
        assertNull(emptyUsage.getResetDate());
        
        // Test full constructor
        UsageInfo fullUsage = new UsageInfo("business", 2500, 5000, 2500, "2024-12-31T23:59:59");
        assertEquals("business", fullUsage.getPlan());
        assertEquals(2500, fullUsage.getUsed());
        assertEquals(5000, fullUsage.getLimit());
        assertEquals(2500, fullUsage.getRemaining());
        assertEquals("2024-12-31T23:59:59", fullUsage.getResetDate());
        assertEquals(50.0, fullUsage.getPercentageUsed(), 0.01);
    }
    
    @Test
    void testEdgeCases() {
        // Test with very high usage
        UsageInfo highUsage = new UsageInfo("unlimited", 999999, 1000000, 1, "2024-12-01T10:30:00");
        assertEquals(99.9999, highUsage.getPercentageUsed(), 0.001);
        assertTrue(highUsage.isQuotaNearlyExhausted());
        assertFalse(highUsage.isQuotaExhausted());
        
        // Test with over-usage (negative remaining)
        UsageInfo overUsage = new UsageInfo("pro", 1100, 1000, -100, "2024-12-01T10:30:00");
        assertEquals(110.0, overUsage.getPercentageUsed(), 0.01);
        assertTrue(overUsage.isQuotaNearlyExhausted());
        assertTrue(overUsage.isQuotaExhausted());
    }
}