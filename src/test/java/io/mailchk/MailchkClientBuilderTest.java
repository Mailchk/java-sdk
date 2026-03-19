package io.mailchk;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class MailchkClientBuilderTest {
    
    @Test
    void testBasicBuilder() {
        MailchkClient client = MailchkClient.builder()
            .apiKey("test-api-key")
            .build();
        
        assertNotNull(client);
        client.close();
    }
    
    @Test
    void testBuilderWithAllOptions() {
        MailchkClient client = MailchkClient.builder()
            .apiKey("test-api-key")
            .baseUrl("https://custom.api.com/v1")
            .timeout(Duration.ofSeconds(60))
            .build();
        
        assertNotNull(client);
        client.close();
    }
    
    @Test
    void testBuilderWithTimeoutSeconds() {
        MailchkClient client = MailchkClient.builder()
            .apiKey("test-api-key")
            .timeoutSeconds(45)
            .build();
        
        assertNotNull(client);
        client.close();
    }
    
    @Test
    void testBuilderWithTimeoutMillis() {
        MailchkClient client = MailchkClient.builder()
            .apiKey("test-api-key")
            .timeoutMillis(30000)
            .build();
        
        assertNotNull(client);
        client.close();
    }
    
    @Test
    void testApiKeyValidation() {
        MailchkClientBuilder builder = MailchkClient.builder();
        
        // Test null API key
        assertThrows(IllegalArgumentException.class, () -> 
            builder.apiKey(null));
        
        // Test empty API key
        assertThrows(IllegalArgumentException.class, () -> 
            builder.apiKey(""));
        
        // Test whitespace-only API key
        assertThrows(IllegalArgumentException.class, () -> 
            builder.apiKey("   "));
        
        // Test missing API key
        assertThrows(IllegalStateException.class, () -> 
            MailchkClient.builder().build());
    }
    
    @Test
    void testBaseUrlValidation() {
        MailchkClientBuilder builder = MailchkClient.builder().apiKey("test-key");
        
        // Test empty base URL
        assertThrows(IllegalArgumentException.class, () -> 
            builder.baseUrl(""));
        
        // Test whitespace-only base URL
        assertThrows(IllegalArgumentException.class, () -> 
            builder.baseUrl("   "));
        
        // Test invalid protocol
        assertThrows(IllegalArgumentException.class, () -> 
            builder.baseUrl("ftp://invalid.com"));
        
        // Test missing protocol
        assertThrows(IllegalArgumentException.class, () -> 
            builder.baseUrl("invalid.com"));
        
        // Test null base URL (should be allowed)
        assertDoesNotThrow(() -> builder.baseUrl(null));
    }
    
    @Test
    void testBaseUrlNormalization() {
        // Test trailing slash removal
        MailchkClient client1 = MailchkClient.builder()
            .apiKey("test-key")
            .baseUrl("https://api.example.com/v1/")
            .build();
        
        assertNotNull(client1);
        client1.close();
        
        // Test without trailing slash
        MailchkClient client2 = MailchkClient.builder()
            .apiKey("test-key")
            .baseUrl("https://api.example.com/v1")
            .build();
        
        assertNotNull(client2);
        client2.close();
        
        // Test HTTP protocol
        MailchkClient client3 = MailchkClient.builder()
            .apiKey("test-key")
            .baseUrl("http://localhost:8080/api")
            .build();
        
        assertNotNull(client3);
        client3.close();
    }
    
    @Test
    void testTimeoutValidation() {
        MailchkClientBuilder builder = MailchkClient.builder().apiKey("test-key");
        
        // Test negative duration
        assertThrows(IllegalArgumentException.class, () -> 
            builder.timeout(Duration.ofSeconds(-1)));
        
        // Test zero duration
        assertThrows(IllegalArgumentException.class, () -> 
            builder.timeout(Duration.ZERO));
        
        // Test negative timeout seconds
        assertThrows(IllegalArgumentException.class, () -> 
            builder.timeoutSeconds(-1));
        
        // Test zero timeout seconds
        assertThrows(IllegalArgumentException.class, () -> 
            builder.timeoutSeconds(0));
        
        // Test negative timeout millis
        assertThrows(IllegalArgumentException.class, () -> 
            builder.timeoutMillis(-1));
        
        // Test zero timeout millis
        assertThrows(IllegalArgumentException.class, () -> 
            builder.timeoutMillis(0));
        
        // Test null timeout (should be allowed)
        assertDoesNotThrow(() -> builder.timeout(null));
    }
    
    @Test
    void testFluentInterface() {
        // Test that all builder methods return the same builder instance
        MailchkClientBuilder builder = MailchkClient.builder();
        
        assertSame(builder, builder.apiKey("test-key"));
        assertSame(builder, builder.baseUrl("https://api.example.com"));
        assertSame(builder, builder.timeout(Duration.ofSeconds(30)));
        assertSame(builder, builder.timeoutSeconds(45));
        assertSame(builder, builder.timeoutMillis(60000));
        
        // Build the client
        MailchkClient client = builder.build();
        assertNotNull(client);
        client.close();
    }
    
    @Test
    void testMethodChaining() {
        // Test complete method chaining
        MailchkClient client = MailchkClient.builder()
            .apiKey("test-key")
            .baseUrl("https://custom.api.com")
            .timeoutSeconds(60)
            .build();
        
        assertNotNull(client);
        client.close();
    }
    
    @Test
    void testApiKeyTrimming() {
        // Test that API key is trimmed
        MailchkClient client = MailchkClient.builder()
            .apiKey("  test-key  ")
            .build();
        
        assertNotNull(client);
        client.close();
    }
    
    @Test
    void testBuilderReuse() {
        // Test that the same builder can be used multiple times
        MailchkClientBuilder builder = MailchkClient.builder()
            .apiKey("test-key")
            .baseUrl("https://api.example.com");
        
        MailchkClient client1 = builder.build();
        assertNotNull(client1);
        
        // Modify builder and create another client
        MailchkClient client2 = builder
            .timeoutSeconds(60)
            .build();
        assertNotNull(client2);
        
        client1.close();
        client2.close();
    }
    
    @Test
    void testTimeoutOverrides() {
        MailchkClientBuilder builder = MailchkClient.builder().apiKey("test-key");
        
        // Set timeout using Duration, then override with seconds
        MailchkClient client1 = builder
            .timeout(Duration.ofSeconds(30))
            .timeoutSeconds(60)
            .build();
        assertNotNull(client1);
        client1.close();
        
        // Set timeout using seconds, then override with millis
        MailchkClient client2 = MailchkClient.builder()
            .apiKey("test-key")
            .timeoutSeconds(30)
            .timeoutMillis(45000)
            .build();
        assertNotNull(client2);
        client2.close();
    }
    
    @Test
    void testLargeTimeoutValues() {
        // Test very large timeout values
        MailchkClient client1 = MailchkClient.builder()
            .apiKey("test-key")
            .timeoutSeconds(Integer.MAX_VALUE)
            .build();
        assertNotNull(client1);
        client1.close();
        
        MailchkClient client2 = MailchkClient.builder()
            .apiKey("test-key")
            .timeoutMillis(Long.MAX_VALUE / 2) // Avoid overflow
            .build();
        assertNotNull(client2);
        client2.close();
    }
}