package io.mailchk.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.mailchk.exceptions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP client for making requests to the Mailchk API.
 */
public class HttpClient {
    
    private static final String USER_AGENT = "mailchk-java/1.0.0";
    private static final String CONTENT_TYPE = "application/json";
    
    private final java.net.http.HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String baseUrl;
    private final Duration timeout;
    
    public HttpClient(String apiKey, String baseUrl, Duration timeout) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.timeout = timeout;
        
        this.httpClient = java.net.http.HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();
        
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }
    
    /**
     * Makes a synchronous POST request to the API.
     * 
     * @param endpoint the API endpoint (e.g., "/check")
     * @param requestBody the request body object
     * @param responseType the expected response type
     * @param <T> response type
     * @return the deserialized response
     * @throws MailchkException if the request fails
     */
    public <T> T post(String endpoint, Object requestBody, Class<T> responseType) throws MailchkException {
        try {
            return postAsync(endpoint, requestBody, responseType).get();
        } catch (Exception e) {
            throw unwrapException(e);
        }
    }
    
    /**
     * Makes an asynchronous POST request to the API.
     * 
     * @param endpoint the API endpoint (e.g., "/check")
     * @param requestBody the request body object
     * @param responseType the expected response type
     * @param <T> response type
     * @return CompletableFuture with the deserialized response
     */
    public <T> CompletableFuture<T> postAsync(String endpoint, Object requestBody, Class<T> responseType) {
        try {
            String url = baseUrl + endpoint;
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(timeout)
                    .header("Content-Type", CONTENT_TYPE)
                    .header("User-Agent", USER_AGENT)
                    .header("X-API-Key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        try {
                            return handleResponse(response, responseType);
                        } catch (MailchkException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    
        } catch (JsonProcessingException e) {
            return CompletableFuture.failedFuture(new ApiException("Failed to serialize request: " + e.getMessage(), e));
        }
    }
    
    /**
     * Makes a synchronous GET request to the API.
     * 
     * @param endpoint the API endpoint
     * @param responseType the expected response type
     * @param <T> response type
     * @return the deserialized response
     * @throws MailchkException if the request fails
     */
    public <T> T get(String endpoint, Class<T> responseType) throws MailchkException {
        try {
            return getAsync(endpoint, responseType).get();
        } catch (Exception e) {
            throw unwrapException(e);
        }
    }
    
    /**
     * Makes an asynchronous GET request to the API.
     * 
     * @param endpoint the API endpoint
     * @param responseType the expected response type
     * @param <T> response type
     * @return CompletableFuture with the deserialized response
     */
    public <T> CompletableFuture<T> getAsync(String endpoint, Class<T> responseType) {
        String url = baseUrl + endpoint;
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .header("User-Agent", USER_AGENT)
                .header("X-API-Key", apiKey)
                .GET()
                .build();
        
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    try {
                        return handleResponse(response, responseType);
                    } catch (MailchkException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
    
    /**
     * Handles the HTTP response and converts it to the expected type.
     * 
     * @param response the HTTP response
     * @param responseType the expected response type
     * @param <T> response type
     * @return the deserialized response
     * @throws MailchkException if the response indicates an error
     */
    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseType) throws MailchkException {
        int statusCode = response.statusCode();
        String responseBody = response.body();
        
        if (statusCode == 401) {
            throw new AuthenticationException("Invalid API key or unauthorized access");
        } else if (statusCode == 429) {
            Integer retryAfter = null;
            String retryAfterHeader = response.headers().firstValue("Retry-After").orElse(null);
            if (retryAfterHeader != null) {
                try {
                    retryAfter = Integer.parseInt(retryAfterHeader);
                } catch (NumberFormatException ignored) {
                }
            }
            throw new RateLimitException("Rate limit exceeded", retryAfter);
        } else if (statusCode == 400) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> errorResponse = objectMapper.readValue(responseBody, Map.class);
                String message = (String) errorResponse.getOrDefault("message", "Bad request");
                String errorCode = (String) errorResponse.get("code");
                throw new ValidationException(message, errorCode);
            } catch (JsonProcessingException e) {
                throw new ValidationException("Bad request: " + responseBody);
            }
        } else if (statusCode >= 500) {
            throw new ApiException("Server error: " + statusCode, "SERVER_ERROR", statusCode);
        } else if (statusCode >= 400) {
            throw new ApiException("Client error: " + statusCode, "CLIENT_ERROR", statusCode);
        }
        
        try {
            return objectMapper.readValue(responseBody, responseType);
        } catch (IOException e) {
            throw new ApiException("Failed to parse response: " + e.getMessage(), e);
        }
    }
    
    /**
     * Unwraps MailchkException from ExecutionException/RuntimeException chains.
     */
    private MailchkException unwrapException(Exception e) {
        Throwable cause = e.getCause();
        if (cause instanceof MailchkException) {
            return (MailchkException) cause;
        }
        if (cause instanceof RuntimeException && cause.getCause() instanceof MailchkException) {
            return (MailchkException) cause.getCause();
        }
        return new ApiException("Request failed: " + e.getMessage(), e);
    }

    /**
     * Closes the HTTP client and releases resources.
     */
    public void close() {
        // Java 21 HttpClient doesn't need explicit cleanup
    }
}