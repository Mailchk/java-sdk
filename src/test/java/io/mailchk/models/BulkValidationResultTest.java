package io.mailchk.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BulkValidationResultTest {

    private BulkValidationResult result;
    private List<ValidationResult> validationResults;

    @BeforeEach
    void setUp() {
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
        result = new BulkValidationResult(validationResults);
    }

    @Test
    void testBasicProperties() {
        // Counts computed from results list
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
        BulkValidationResult emptyResult = new BulkValidationResult(Collections.emptyList());
        assertEquals(0, emptyResult.getTotal());
        assertEquals(0.0, emptyResult.getValidPercentage());
        assertEquals(0.0, emptyResult.getDisposablePercentage());
    }

    @Test
    void testAllValid() {
        ValidationResult v1 = new ValidationResult();
        v1.setValid(true);
        v1.setDisposable(false);
        ValidationResult v2 = new ValidationResult();
        v2.setValid(true);
        v2.setDisposable(false);

        BulkValidationResult allValidResult = new BulkValidationResult(List.of(v1, v2));
        assertEquals(2, allValidResult.getTotal());
        assertEquals(2, allValidResult.getValid());
        assertEquals(0, allValidResult.getInvalid());
        assertEquals(100.0, allValidResult.getValidPercentage(), 0.01);
        assertEquals(0.0, allValidResult.getDisposablePercentage());
    }

    @Test
    void testAllDisposable() {
        ValidationResult d1 = new ValidationResult();
        d1.setValid(false);
        d1.setDisposable(true);
        ValidationResult d2 = new ValidationResult();
        d2.setValid(false);
        d2.setDisposable(true);
        ValidationResult d3 = new ValidationResult();
        d3.setValid(false);
        d3.setDisposable(true);

        BulkValidationResult allDisposableResult = new BulkValidationResult(List.of(d1, d2, d3));
        assertEquals(3, allDisposableResult.getTotal());
        assertEquals(0, allDisposableResult.getValid());
        assertEquals(3, allDisposableResult.getInvalid());
        assertEquals(3, allDisposableResult.getDisposable());
        assertEquals(0.0, allDisposableResult.getValidPercentage());
        assertEquals(100.0, allDisposableResult.getDisposablePercentage(), 0.01);
    }

    @Test
    void testSetResults() {
        BulkValidationResult newResult = new BulkValidationResult();
        newResult.setResults(validationResults);

        assertEquals(4, newResult.getTotal());
        assertEquals(2, newResult.getValid());
        assertEquals(2, newResult.getInvalid());
        assertEquals(1, newResult.getDisposable());
        assertEquals(50.0, newResult.getValidPercentage(), 0.01);
        assertEquals(25.0, newResult.getDisposablePercentage(), 0.01);
    }

    @Test
    void testNullResults() {
        BulkValidationResult emptyResult = new BulkValidationResult();
        assertEquals(0, emptyResult.getTotal());
        assertEquals(0, emptyResult.getValid());
        assertEquals(0, emptyResult.getInvalid());
        assertEquals(0, emptyResult.getDisposable());
        assertNull(emptyResult.getResults());
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
}
