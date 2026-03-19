package io.mailchk.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MxRecordTest {
    
    @Test
    void testBasicProperties() {
        MxRecord record = new MxRecord("mail.example.com", 10);
        
        assertEquals("mail.example.com", record.getExchange());
        assertEquals(10, record.getPriority());
    }
    
    @Test
    void testSettersAndGetters() {
        MxRecord record = new MxRecord();
        
        record.setExchange("mx.company.org");
        record.setPriority(20);
        
        assertEquals("mx.company.org", record.getExchange());
        assertEquals(20, record.getPriority());
    }
    
    @Test
    void testEquality() {
        MxRecord record1 = new MxRecord("mail.example.com", 10);
        MxRecord record2 = new MxRecord("mail.example.com", 10);
        MxRecord record3 = new MxRecord("mail2.example.com", 10);
        MxRecord record4 = new MxRecord("mail.example.com", 20);
        
        // Test equality
        assertEquals(record1, record2);
        assertNotEquals(record1, record3);
        assertNotEquals(record1, record4);
        assertNotEquals(record3, record4);
        
        // Test with null
        assertNotEquals(record1, null);
        
        // Test with different class
        assertNotEquals(record1, "not an MxRecord");
        
        // Test self-equality
        assertEquals(record1, record1);
    }
    
    @Test
    void testHashCode() {
        MxRecord record1 = new MxRecord("mail.example.com", 10);
        MxRecord record2 = new MxRecord("mail.example.com", 10);
        MxRecord record3 = new MxRecord("mail2.example.com", 10);
        
        // Equal objects must have equal hash codes
        assertEquals(record1.hashCode(), record2.hashCode());
        
        // Different objects should generally have different hash codes
        assertNotEquals(record1.hashCode(), record3.hashCode());
    }
    
    @Test
    void testToString() {
        MxRecord record = new MxRecord("mx.google.com", 5);
        String str = record.toString();
        
        assertTrue(str.contains("mx.google.com"));
        assertTrue(str.contains("5"));
        assertTrue(str.contains("MxRecord"));
    }
    
    @Test
    void testNullExchange() {
        MxRecord record1 = new MxRecord(null, 10);
        MxRecord record2 = new MxRecord(null, 10);
        MxRecord record3 = new MxRecord("mail.example.com", 10);
        
        // Two null exchanges should be equal
        assertEquals(record1, record2);
        assertEquals(record1.hashCode(), record2.hashCode());
        
        // Null exchange should not equal non-null exchange
        assertNotEquals(record1, record3);
    }
    
    @Test
    void testEmptyExchange() {
        MxRecord record = new MxRecord("", 0);
        
        assertEquals("", record.getExchange());
        assertEquals(0, record.getPriority());
    }
    
    @Test
    void testNegativePriority() {
        MxRecord record = new MxRecord("mail.example.com", -1);
        
        assertEquals("mail.example.com", record.getExchange());
        assertEquals(-1, record.getPriority());
    }
    
    @Test
    void testHighPriority() {
        MxRecord record = new MxRecord("backup.example.com", 65535);
        
        assertEquals("backup.example.com", record.getExchange());
        assertEquals(65535, record.getPriority());
    }
}