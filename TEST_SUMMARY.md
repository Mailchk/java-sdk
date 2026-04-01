# Mailchk Java SDK — Test Summary

## Run Date
2026-03-31

## Environment
- Java: OpenJDK 21.0.10 (Eclipse Temurin)
- Maven: 3.9.6
- OS: Windows 11

## Result: ALL TESTS PASSING

```
Tests run: 66, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Test Suites

| Test Class | Tests | Status |
|---|---|---|
| `io.mailchk.exceptions.ExceptionTest` | 7 | PASS |
| `io.mailchk.integration.MailchkClientIntegrationTest` | 9 | PASS |
| `io.mailchk.MailchkClientBuilderTest` | 14 | PASS |
| `io.mailchk.MailchkClientTest` | 9 | PASS |
| `io.mailchk.models.BulkValidationResultTest` | 9 | PASS |
| `io.mailchk.models.MxRecordTest` | 9 | PASS |
| `io.mailchk.models.ValidationResultTest` | 9 | PASS |
| **Total** | **66** | **PASS** |

## Full Maven Output

```
[INFO] Scanning for projects...
[INFO]
[INFO] ----------------------< io.mailchk:mailchk-java >-----------------------
[INFO] Building Mailchk Java SDK 1.0.0
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- clean:3.2.0:clean (default-clean) @ mailchk-java ---
[INFO]
[INFO] --- compiler:3.13.0:compile (default-compile) @ mailchk-java ---
[INFO] Compiling 11 source files with javac [debug target 17] to target\classes
[INFO]
[INFO] --- compiler:3.13.0:testCompile (default-testCompile) @ mailchk-java ---
[INFO] Compiling 7 source files with javac [debug target 17] to target\test-classes
[INFO]
[INFO] --- surefire:3.2.5:test (default-test) @ mailchk-java ---
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running io.mailchk.exceptions.ExceptionTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.099 s
[INFO] Running io.mailchk.integration.MailchkClientIntegrationTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.864 s
[INFO] Running io.mailchk.MailchkClientBuilderTest
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.094 s
[INFO] Running io.mailchk.MailchkClientTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.096 s
[INFO] Running io.mailchk.models.BulkValidationResultTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.026 s
[INFO] Running io.mailchk.models.MxRecordTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.032 s
[INFO] Running io.mailchk.models.ValidationResultTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.012 s
[INFO]
[INFO] Tests run: 66, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

## API Surface Covered

The SDK covers all endpoints accessible via API key (`X-API-Key` header):

| Endpoint | Method | SDK Method |
|---|---|---|
| `/v1/check?email={email}` | GET | `validate(email)` / `validateAsync(email)` |
| `/v1/check/bulk` | POST `{"emails":[...]}` | `validateBulk(emails)` / `validateBulkAsync(emails)` |

Helper methods delegate to the above:
- `isValid(email)`, `isDisposable(email)`, `getRiskScore(email)`, `getDeliverabilityScore(email)`

## ValidationResult Field Mapping

All fields returned by `GET /v1/check` are mapped:

| API field | Java getter | Notes |
|---|---|---|
| `email` | `getEmail()` | |
| `domain` | `getDomain()` | |
| `valid` | `isValid()` | |
| `disposable` | `isDisposable()` | |
| `scam_domain` | `isScamDomain()` | |
| `mx_exists` | `isMxExists()` | |
| `mx_records` | `getMxRecords()` | Returns empty list when field absent in response |
| `blacklisted_mx` | `isBlacklistedMx()` | |
| `free_email` | `isFreeEmail()` | |
| `did_you_mean` | `getDidYouMean()` | |
| `risk_score` | `getRiskScore()` | `"low"`, `"medium"`, `"high"`, `"critical"` |
| `risk_factors` | `getRiskFactors()` | |
| `reason` | `getReason()` | nullable |
| `email_provider` | `getEmailProvider()` | nullable |
| `deliverability_score` | `getDeliverabilityScore()` | 0–100 |
| `spf` | `getSpf()` | `"pass"`, `"fail"`, `"none"` |
| `dmarc` | `getDmarc()` | `"pass"`, `"fail"`, `"none"` |
| `normalized_email` | `getNormalizedEmail()` | |
| `is_aliased` | `isAliased()` | |
| `alias_type` | `getAliasType()` | `"plus_addressing"`, `"dot_variation"`, `"subdomain_addressing"`, `"provider_alias"`, or null |

## Changes Applied in Final Review

1. **`getMxRecords()` null safety** — API omits `mx_records` field when no MX records exist.
   `getMxRecords()` now returns `Collections.emptyList()` instead of null, preventing NPE.

2. **`UsageInfo` removed** — No `/v1/usage` endpoint exists for API key users in the Mailchk
   API. The `UsageInfo` model and `UsageInfoTest` were dead code and have been removed.

3. **`alias_type` values documented** — Javadoc updated with the four valid values returned
   by the API: `plus_addressing`, `dot_variation`, `subdomain_addressing`, `provider_alias`.
