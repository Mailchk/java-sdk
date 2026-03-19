# Mailchk Java SDK

Official Java SDK for the [Mailchk](https://mailchk.io) email validation API.

## Installation

### Maven

Add this dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.mailchk</groupId>
    <artifactId>mailchk-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

Add this dependency to your `build.gradle`:

```gradle
implementation 'io.mailchk:mailchk-java:1.0.0'
```

## Quick Start

```java
import io.mailchk.MailchkClient;
import io.mailchk.models.ValidationResult;

// Initialize the client
MailchkClient client = MailchkClient.builder()
    .apiKey("your-api-key")
    .build();

// Validate an email
ValidationResult result = client.validate("user@example.com");

if (result.isValid() && !result.isDisposable()) {
    System.out.println("Email is valid!");
} else {
    System.out.println("Email rejected: " + result.getReason());
}
```

## Features

- **Email Validation** - Check if emails are valid and deliverable
- **Disposable Detection** - Detect temporary/disposable email providers
- **Scam Domain Detection** - Identify known scam and fraud domains
- **Risk Scoring** - Get risk assessment (low, medium, high, critical) with detailed risk factors
- **Deliverability Scoring** - Get a 0-100 deliverability score for each email
- **MX Validation** - Verify domain mail server records and detect blacklisted MX hosts
- **SPF & DMARC Checks** - Verify domain email authentication records
- **Email Normalization** - Get the canonical form of an email address
- **Alias Detection** - Detect plus addressing, dot variations, subdomain addressing, and provider aliases
- **Typo Suggestions** - Get "did you mean" suggestions for misspelled domains
- **Bulk Validation** - Validate up to 100 emails per request
- **Async Support** - Full async support with CompletableFuture

## Usage

### Basic Validation

```java
import io.mailchk.MailchkClient;
import io.mailchk.models.ValidationResult;

MailchkClient client = MailchkClient.builder()
    .apiKey("your-api-key")
    .build();

ValidationResult result = client.validate("test@gmail.com");

System.out.println("Valid: " + result.isValid());
System.out.println("Disposable: " + result.isDisposable());
System.out.println("Risk Score: " + result.getRiskScore());
System.out.println("Risk Factors: " + result.getRiskFactors());
System.out.println("Deliverability: " + result.getDeliverabilityScore() + "/100");
System.out.println("SPF: " + result.getSpf());
System.out.println("DMARC: " + result.getDmarc());
System.out.println("Provider: " + result.getEmailProvider());
System.out.println("Normalized: " + result.getNormalizedEmail());
```

### Quick Checks

```java
// Check if email is disposable
if (client.isDisposable("user@tempmail.com")) {
    System.out.println("Disposable email detected!");
}

// Check if email is valid
if (client.isValid("user@gmail.com")) {
    System.out.println("Email is valid!");
}

// Get risk level (low, medium, high, critical)
String risk = client.getRiskScore("user@example.com");
System.out.println("Risk level: " + risk);

// Get deliverability score (0-100)
int score = client.getDeliverabilityScore("user@example.com");
System.out.println("Deliverability score: " + score);
```

### Bulk Validation

```java
import java.util.List;
import io.mailchk.models.BulkValidationResult;

List<String> emails = List.of(
    "user1@gmail.com",
    "user2@tempmail.com",
    "invalid-email",
    "user3@company.com"
);

BulkValidationResult result = client.validateBulk(emails);

System.out.println("Total: " + result.getTotal());
System.out.println("Valid: " + result.getValid());
System.out.println("Invalid: " + result.getInvalid());
System.out.println("Disposable: " + result.getDisposable());

for (ValidationResult r : result.getResults()) {
    String status = r.isValid() ? "PASS" : "FAIL";
    System.out.printf("%s: %s (risk: %s, deliverability: %d)%n", 
        r.getEmail(), status, r.getRiskScore(), r.getDeliverabilityScore());
}
```

### Check Usage

```java
import io.mailchk.models.UsageInfo;

UsageInfo usage = client.getUsage();

System.out.println("Used: " + usage.getUsed() + "/" + usage.getLimit());
System.out.println("Remaining: " + usage.getRemaining());
System.out.println("Resets: " + usage.getResetDate());
System.out.printf("Usage: %.1f%%%n", usage.getPercentageUsed());
```

### Async Usage

```java
import java.util.concurrent.CompletableFuture;

// Single validation
CompletableFuture<ValidationResult> future = client.validateAsync("user@example.com");
future.thenAccept(result -> {
    System.out.println("Valid: " + result.isValid());
    System.out.println("Deliverability: " + result.getDeliverabilityScore());
});

// Bulk validation
CompletableFuture<BulkValidationResult> bulkFuture = client.validateBulkAsync(emails);
bulkFuture.thenAccept(bulk -> {
    System.out.println("Valid: " + bulk.getValid() + "/" + bulk.getTotal());
});

// Wait for completion
ValidationResult result = future.get();
```

### Try-with-resources

```java
try (MailchkClient client = MailchkClient.builder()
        .apiKey("your-api-key")
        .build()) {
    
    ValidationResult result = client.validate("user@example.com");
    System.out.println(result.isValid());
}
// Connection automatically closed
```

## Error Handling

```java
import io.mailchk.exceptions.*;

MailchkClient client = MailchkClient.builder()
    .apiKey("your-api-key")
    .build();

try {
    ValidationResult result = client.validate("user@example.com");
} catch (AuthenticationException e) {
    System.err.println("Invalid API key");
} catch (RateLimitException e) {
    System.err.printf("Rate limited. Retry after %d seconds%n", e.getRetryAfter());
} catch (ValidationException e) {
    System.err.println("Invalid request: " + e.getMessage());
} catch (ApiException e) {
    System.err.println("API error: " + e.getMessage());
} catch (MailchkException e) {
    System.err.println("General error: " + e.getMessage());
}
```

## ValidationResult Fields

| Field | Type | Description |
|-------|------|-------------|
| `email` | String | The validated email address |
| `domain` | String | The email domain |
| `valid` | boolean | Whether the email is valid |
| `disposable` | boolean | Whether it's a disposable/temporary email |
| `scamDomain` | boolean | Whether the domain is a known scam domain |
| `mxExists` | boolean | Whether the domain has MX records |
| `mxRecords` | List&lt;MxRecord&gt; | MX records with exchange and priority |
| `blacklistedMx` | boolean | Whether MX host is on a blacklist |
| `freeEmail` | boolean | Whether it's a free email provider (Gmail, Yahoo, etc.) |
| `didYouMean` | String | Suggested correction for misspelled domains |
| `riskScore` | String | Risk level: `low`, `medium`, `high`, or `critical` |
| `riskFactors` | List&lt;String&gt; | Specific reasons contributing to the risk score |
| `reason` | String | Reason if the email is invalid |
| `emailProvider` | String | Identified email provider name |
| `deliverabilityScore` | int | Deliverability score from 0-100 |
| `spf` | String | SPF record status: `pass`, `fail`, or `none` |
| `dmarc` | String | DMARC record status: `pass`, `fail`, or `none` |
| `normalizedEmail` | String | Canonical/normalized form of the email |
| `isAliased` | boolean | Whether the email uses an alias |
| `aliasType` | String | Alias type: `plus_addressing`, `dot_variation`, `subdomain_addressing`, or `provider_alias` |

### Helper Methods

```java
ValidationResult result = client.validate("user@example.com");

// Check if safe to use (valid and low/medium risk)
if (result.isSafe()) {
    System.out.println("Email is safe to use");
}

// Check if high risk
if (result.isHighRisk()) {
    System.out.println("High risk email detected");
}

// Check if scam domain
if (result.isScamDomain()) {
    System.out.println("Scam domain detected!");
}

// Check deliverability (default threshold: 50)
if (result.isDeliverable()) {
    System.out.println("Email is likely deliverable");
}

// Check with custom threshold
if (result.isDeliverable(80)) {
    System.out.println("Email has high deliverability");
}

// Check SPF and DMARC authentication
if (result.hasValidAuth()) {
    System.out.println("Domain has valid SPF and DMARC");
}
```

## Configuration

```java
MailchkClient client = MailchkClient.builder()
    .apiKey("your-api-key")
    .baseUrl("https://api.mailchk.io/v1")  // Custom API URL
    .timeout(Duration.ofSeconds(45))       // Request timeout
    .timeoutSeconds(45)                    // Alternative timeout method
    .timeoutMillis(45000)                  // Timeout in milliseconds
    .build();
```

## Requirements

- Java 11 or higher
- Valid Mailchk API key ([Get one free](https://mailchk.io))

## Dependencies

This SDK uses the following dependencies:
- Jackson (JSON processing)
- Java 11+ HttpClient (HTTP communication)

## License

MIT License - see [LICENSE](LICENSE) for details.

## Support

- Documentation: https://mailchk.io/docs
- Email: support@mailchk.io
- Issues: https://github.com/mailchk/mailchk-java/issues

## Changelog

### 1.0.0
- Initial release
- Full email validation API support
- Sync and async methods
- Comprehensive error handling
- Builder pattern configuration
- Helper methods for common use cases