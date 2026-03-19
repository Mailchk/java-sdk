package io.mailchk.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents the result of an email validation request.
 */
public class ValidationResult {
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("domain")
    private String domain;
    
    @JsonProperty("valid")
    private boolean valid;
    
    @JsonProperty("disposable")
    private boolean disposable;
    
    @JsonProperty("scam_domain")
    private boolean scamDomain;
    
    @JsonProperty("mx_exists")
    private boolean mxExists;
    
    @JsonProperty("mx_records")
    private List<MxRecord> mxRecords;
    
    @JsonProperty("blacklisted_mx")
    private boolean blacklistedMx;
    
    @JsonProperty("free_email")
    private boolean freeEmail;
    
    @JsonProperty("did_you_mean")
    private String didYouMean;
    
    @JsonProperty("risk_score")
    private String riskScore;
    
    @JsonProperty("risk_factors")
    private List<String> riskFactors;
    
    @JsonProperty("reason")
    private String reason;
    
    @JsonProperty("email_provider")
    private String emailProvider;
    
    @JsonProperty("deliverability_score")
    private int deliverabilityScore;
    
    @JsonProperty("spf")
    private String spf;
    
    @JsonProperty("dmarc")
    private String dmarc;
    
    @JsonProperty("normalized_email")
    private String normalizedEmail;
    
    @JsonProperty("is_aliased")
    private boolean isAliased;
    
    @JsonProperty("alias_type")
    private String aliasType;
    
    public ValidationResult() {
    }
    
    // Getters
    
    /**
     * Gets the validated email address.
     * 
     * @return the email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Gets the domain part of the email.
     * 
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }
    
    /**
     * Checks if the email is valid.
     * 
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return valid;
    }
    
    /**
     * Checks if the email uses a disposable/temporary provider.
     * 
     * @return true if disposable, false otherwise
     */
    public boolean isDisposable() {
        return disposable;
    }
    
    /**
     * Checks if the domain is flagged as a scam domain.
     * 
     * @return true if scam domain, false otherwise
     */
    public boolean isScamDomain() {
        return scamDomain;
    }
    
    /**
     * Checks if the domain has MX records.
     * 
     * @return true if MX records exist, false otherwise
     */
    public boolean isMxExists() {
        return mxExists;
    }
    
    /**
     * Gets the MX records for the domain.
     * 
     * @return list of MX records
     */
    public List<MxRecord> getMxRecords() {
        return mxRecords;
    }
    
    /**
     * Checks if the MX servers are blacklisted.
     * 
     * @return true if blacklisted, false otherwise
     */
    public boolean isBlacklistedMx() {
        return blacklistedMx;
    }
    
    /**
     * Checks if the email uses a free provider (Gmail, Yahoo, etc.).
     * 
     * @return true if free email, false otherwise
     */
    public boolean isFreeEmail() {
        return freeEmail;
    }
    
    /**
     * Gets the suggested correction for domain typos.
     * 
     * @return suggested email or empty string if none
     */
    public String getDidYouMean() {
        return didYouMean;
    }
    
    /**
     * Gets the risk score level.
     * 
     * @return risk level: "low", "medium", "high", or "critical"
     */
    public String getRiskScore() {
        return riskScore;
    }
    
    /**
     * Gets the risk factors contributing to the risk score.
     * 
     * @return list of risk factors
     */
    public List<String> getRiskFactors() {
        return riskFactors;
    }
    
    /**
     * Gets the reason if the email is invalid.
     * 
     * @return reason string or null
     */
    public String getReason() {
        return reason;
    }
    
    /**
     * Gets the detected email provider name.
     * 
     * @return provider name or null
     */
    public String getEmailProvider() {
        return emailProvider;
    }
    
    /**
     * Gets the deliverability score (0-100).
     * 
     * @return deliverability score
     */
    public int getDeliverabilityScore() {
        return deliverabilityScore;
    }
    
    /**
     * Gets the SPF authentication status.
     * 
     * @return "pass", "fail", or "none"
     */
    public String getSpf() {
        return spf;
    }
    
    /**
     * Gets the DMARC policy status.
     * 
     * @return "pass", "fail", or "none"
     */
    public String getDmarc() {
        return dmarc;
    }
    
    /**
     * Gets the normalized/canonical form of the email.
     * 
     * @return normalized email
     */
    public String getNormalizedEmail() {
        return normalizedEmail;
    }
    
    /**
     * Checks if the email uses aliasing.
     * 
     * @return true if aliased, false otherwise
     */
    public boolean isAliased() {
        return isAliased;
    }
    
    /**
     * Gets the type of aliasing used.
     * 
     * @return alias type or null
     */
    public String getAliasType() {
        return aliasType;
    }
    
    // Setters
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public void setDisposable(boolean disposable) {
        this.disposable = disposable;
    }
    
    public void setScamDomain(boolean scamDomain) {
        this.scamDomain = scamDomain;
    }
    
    public void setMxExists(boolean mxExists) {
        this.mxExists = mxExists;
    }
    
    public void setMxRecords(List<MxRecord> mxRecords) {
        this.mxRecords = mxRecords;
    }
    
    public void setBlacklistedMx(boolean blacklistedMx) {
        this.blacklistedMx = blacklistedMx;
    }
    
    public void setFreeEmail(boolean freeEmail) {
        this.freeEmail = freeEmail;
    }
    
    public void setDidYouMean(String didYouMean) {
        this.didYouMean = didYouMean;
    }
    
    public void setRiskScore(String riskScore) {
        this.riskScore = riskScore;
    }
    
    public void setRiskFactors(List<String> riskFactors) {
        this.riskFactors = riskFactors;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public void setEmailProvider(String emailProvider) {
        this.emailProvider = emailProvider;
    }
    
    public void setDeliverabilityScore(int deliverabilityScore) {
        this.deliverabilityScore = deliverabilityScore;
    }
    
    public void setSpf(String spf) {
        this.spf = spf;
    }
    
    public void setDmarc(String dmarc) {
        this.dmarc = dmarc;
    }
    
    public void setNormalizedEmail(String normalizedEmail) {
        this.normalizedEmail = normalizedEmail;
    }
    
    public void setAliased(boolean aliased) {
        isAliased = aliased;
    }
    
    public void setAliasType(String aliasType) {
        this.aliasType = aliasType;
    }
    
    // Helper methods
    
    /**
     * Checks if the email is safe to use (valid and low/medium risk).
     * 
     * @return true if safe, false otherwise
     */
    public boolean isSafe() {
        return valid && ("low".equals(riskScore) || "medium".equals(riskScore));
    }
    
    /**
     * Checks if the email has high risk.
     * 
     * @return true if high or critical risk, false otherwise
     */
    public boolean isHighRisk() {
        return "high".equals(riskScore) || "critical".equals(riskScore);
    }
    
    /**
     * Checks if the email has good deliverability.
     * 
     * @param threshold minimum deliverability score (default: 50)
     * @return true if deliverable, false otherwise
     */
    public boolean isDeliverable(int threshold) {
        return deliverabilityScore >= threshold;
    }
    
    /**
     * Checks if the email has good deliverability (threshold: 50).
     * 
     * @return true if deliverable, false otherwise
     */
    public boolean isDeliverable() {
        return isDeliverable(50);
    }
    
    /**
     * Checks if the domain has valid SPF and DMARC authentication.
     * 
     * @return true if both SPF and DMARC pass, false otherwise
     */
    public boolean hasValidAuth() {
        return "pass".equals(spf) && "pass".equals(dmarc);
    }
    
    @Override
    public String toString() {
        return String.format(
            "ValidationResult{email='%s', valid=%s, disposable=%s, riskScore='%s', deliverabilityScore=%d}",
            email, valid, disposable, riskScore, deliverabilityScore
        );
    }
}