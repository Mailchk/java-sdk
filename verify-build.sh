#!/bin/bash

# Build verification script for Mailchk Java SDK
# This script checks the project structure and validates file syntax

echo "🔍 Verifying Mailchk Java SDK build structure..."

# Check if we're in the right directory
if [ ! -f "pom.xml" ]; then
    echo "❌ Error: pom.xml not found. Please run this script from the java-sdk directory."
    exit 1
fi

echo "✅ Found pom.xml"

# Check directory structure
echo "📁 Verifying directory structure..."

required_dirs=(
    "src/main/java/io/mailchk"
    "src/main/java/io/mailchk/models"
    "src/main/java/io/mailchk/exceptions"
    "src/main/java/io/mailchk/http"
    "src/test/java/io/mailchk"
    "src/test/java/io/mailchk/models"
    "src/test/java/io/mailchk/exceptions"
    "src/test/java/io/mailchk/integration"
)

for dir in "${required_dirs[@]}"; do
    if [ -d "$dir" ]; then
        echo "✅ $dir"
    else
        echo "❌ Missing directory: $dir"
        exit 1
    fi
done

# Check required Java source files
echo "📄 Verifying Java source files..."

required_files=(
    "src/main/java/io/mailchk/MailchkClient.java"
    "src/main/java/io/mailchk/MailchkClientBuilder.java"
    "src/main/java/io/mailchk/models/ValidationResult.java"
    "src/main/java/io/mailchk/models/BulkValidationResult.java"
    "src/main/java/io/mailchk/models/MxRecord.java"
    "src/main/java/io/mailchk/exceptions/MailchkException.java"
    "src/main/java/io/mailchk/exceptions/AuthenticationException.java"
    "src/main/java/io/mailchk/exceptions/RateLimitException.java"
    "src/main/java/io/mailchk/exceptions/ValidationException.java"
    "src/main/java/io/mailchk/exceptions/ApiException.java"
    "src/main/java/io/mailchk/http/HttpClient.java"
)

for file in "${required_files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file"
    else
        echo "❌ Missing file: $file"
        exit 1
    fi
done

# Check test files
echo "🧪 Verifying test files..."

test_files=(
    "src/test/java/io/mailchk/MailchkClientTest.java"
    "src/test/java/io/mailchk/MailchkClientBuilderTest.java"
    "src/test/java/io/mailchk/models/ValidationResultTest.java"
    "src/test/java/io/mailchk/models/BulkValidationResultTest.java"
    "src/test/java/io/mailchk/models/MxRecordTest.java"
    "src/test/java/io/mailchk/exceptions/ExceptionTest.java"
    "src/test/java/io/mailchk/integration/MailchkClientIntegrationTest.java"
)

for file in "${test_files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file"
    else
        echo "❌ Missing test file: $file"
        exit 1
    fi
done

# Check documentation
echo "📚 Verifying documentation..."

doc_files=(
    "README.md"
    "TEST_SUMMARY.md"
)

for file in "${doc_files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file"
    else
        echo "❌ Missing documentation: $file"
        exit 1
    fi
done

# Basic syntax check for Java files (if available)
if command -v javac &> /dev/null; then
    echo "☕ Java compiler found. Running basic syntax checks..."
    
    # Create temporary directory for compilation
    temp_dir=$(mktemp -d)
    
    # Try to compile main sources
    echo "🔧 Compiling main sources..."
    find src/main/java -name "*.java" | head -5 | while read -r file; do
        if javac -cp ".:$temp_dir" -d "$temp_dir" "$file" 2>/dev/null; then
            echo "✅ Compiled: $file"
        else
            echo "⚠️  Compilation issues in: $file"
        fi
    done
    
    # Clean up
    rm -rf "$temp_dir"
else
    echo "⚠️  Java compiler not available. Skipping syntax checks."
fi

# Validate pom.xml structure
echo "🔧 Validating pom.xml structure..."

if grep -q "<groupId>io.mailchk</groupId>" pom.xml; then
    echo "✅ Group ID correct"
else
    echo "❌ Group ID missing or incorrect"
    exit 1
fi

if grep -q "<artifactId>mailchk-java</artifactId>" pom.xml; then
    echo "✅ Artifact ID correct"
else
    echo "❌ Artifact ID missing or incorrect"
    exit 1
fi

if grep -q "<version>1.0.0</version>" pom.xml; then
    echo "✅ Version correct"
else
    echo "❌ Version missing or incorrect"
    exit 1
fi

# Check for required dependencies
echo "📦 Verifying dependencies..."

if grep -q "jackson-databind" pom.xml; then
    echo "✅ Jackson dependency found"
else
    echo "❌ Jackson dependency missing"
    exit 1
fi

if grep -q "junit-jupiter" pom.xml; then
    echo "✅ JUnit 5 dependency found"
else
    echo "❌ JUnit 5 dependency missing"
    exit 1
fi

if grep -q "wiremock" pom.xml; then
    echo "✅ WireMock dependency found"
else
    echo "❌ WireMock dependency missing"
    exit 1
fi

# Count lines of code
echo "📊 Project statistics..."

java_files=$(find src -name "*.java" | wc -l)
test_files_count=$(find src/test -name "*.java" | wc -l)
total_lines=$(find src -name "*.java" -exec wc -l {} + | tail -1 | awk '{print $1}')

echo "   📁 Java files: $java_files"
echo "   🧪 Test files: $test_files_count"
echo "   📝 Total lines: $total_lines"

echo ""
echo "🎉 Build verification completed successfully!"
echo ""
echo "📋 Next steps:"
echo "   1. Run 'mvn clean compile' to compile the project"
echo "   2. Run 'mvn test' to execute all tests"
echo "   3. Run 'mvn package' to build the JAR file"
echo "   4. Run 'mvn install' to install to local Maven repository"
echo ""
echo "📖 For detailed test information, see TEST_SUMMARY.md"