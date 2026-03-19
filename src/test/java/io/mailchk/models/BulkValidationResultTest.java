package io.mailchk.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BulkValidationResultTest {
    
    private BulkValidationResult result;
    private List<ValidationResult> validationResults;
    
    @BeforeEach
    void setUp() {
        // Create sample validation results
        ValidationResult valid1 = new ValidationResult();
        valid1.setEmail("user1@gmail.com");
        valid1.setValid(true);
        valid1.setDisposable(false);
        
        ValidationResult valid2 = new ValidationResult();
        valid2.setEmail("user2@company.com");
        valid2.setValid(true);
        valid2.setDisposable(false);
        
        ValidationResult disposable = new ValidationResult();
        disposable.setEmail("temp@tempmail.com");
        disposable.setValid(false);
        disposable.setDisposable(true);
        
        ValidationResult invalid = new ValidationResult();
        invalid.setEmail("invalid@nonexistent.xyz");
        invalid.setValid(false);
        invalid.setDisposable(false);
        
        validationResults = List.of(valid1, valid2, disposable, invalid);
        
        result = new BulkValidationResult(4, 2, 2, 1, validationResults);
    }
    
    @Test
    void testBasicProperties() {
        assertEquals(4, result.getTotal());
        assertEquals(2, result.getValid());
        assertEquals(2, result.getInvalid());
        assertEquals(1, result.getDisposable());
        assertEquals(validationResults, result.getResults());
    }
    
    @Test
    void testValidPercentage() {
        assertEquals(50.0, result.getValidPercentage(), 0.01);
    }
    
    @Test
    void testDisposablePercentage() {
        assertEquals(25.0, result.getDisposablePercentage(), 0.01);
    }
    
    @Test
    void testZeroTotal() {
        BulkValidationResult emptyResult = new BulkValidationResult(0, 0, 0, 0, List.of());
        assertEquals(0.0, emptyResult.getValidPercentage());
        assertEquals(0.0, emptyResult.getDisposablePercentage());
    }
    
    @Test
    void testAllValid() {
        BulkValidationResult allValidResult = new BulkValidationResult(5, 5, 0, 0, List.of());
        assertEquals(100.0, allValidResult.getValidPercentage(), 0.01);
        assertEquals(0.0, allValidResult.getDisposablePercentage());
    }
    
    @Test
    void testAllDisposable() {
        BulkValidationResult allDisposableResult = new BulkValidationResult(3, 0, 3, 3, List.of());
        assertEquals(0.0, allDisposableResult.getValidPercentage());
        assertEquals(100.0, allDisposableResult.getDisposablePercentage(), 0.01);
    }
    
    @Test
    void testSettersAndGetters() {
        BulkValidationResult newResult = new BulkValidationResult();
        
        newResult.setTotal(10);
        newResult.setValid(7);
        newResult.setInvalid(3);
        newResult.setDisposable(2);
        newResult.setResults(validationResults);
        
        assertEquals(10, newResult.getTotal());
        assertEquals(7, newResult.getValid());
        assertEquals(3, newResult.getInvalid());
        assertEquals(2, newResult.getDisposable());
        assertEquals(validationResults, newResult.getResults());
        assertEquals(70.0, newResult.getValidPercentage(), 0.01);
        assertEquals(20.0, newResult.getDisposablePercentage(), 0.01);
    }
    
    @Test
    void testToString() {
        String str = result.toString();
        assertTrue(str.contains("total=4"));
        assertTrue(str.contains("valid=2"));
        assertTrue(str.contains("invalid=2"));
        assertTrue(str.contains("disposable=1"));
        assertTrue(str.contains("validPercentage=50.0%"));
    }
    
    @Test
    void testConstructorOverloads() {
        // Test empty constructor
        BulkValidationResult emptyResult = new BulkValidationResult();
        assertEquals(0, emptyResult.getTotal());
        assertEquals(0, emptyResult.getValid());
        assertEquals(0, emptyResult.getInvalid());
        assertEquals(0, emptyResult.getDisposable());
        assertNull(emptyResult.getResults());
        
        // Test full constructor
        BulkValidationResult fullResult = new BulkValidationResult(5, 3, 2, 1, validationResults);
        assertEquals(5, fullResult.getTotal());
        assertEquals(3, fullResult.getValid());
        assertEquals(2, fullResult.getInvalid());
        assertEquals(1, fullResult.getDisposable());
        assertEquals(validationResults, fullResult.getResults());
    }
}