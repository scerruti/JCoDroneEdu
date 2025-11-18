#!/bin/bash

# Test Coverage Improvement Project - Milestone Creation Script
# This script creates all milestones needed for the project using GitHub CLI

set -e

echo "🎯 Creating milestones for Test Coverage Improvement Project..."
echo ""

# Check if gh CLI is installed
if ! command -v gh &> /dev/null; then
    echo "❌ Error: GitHub CLI (gh) is not installed."
    echo "   Install from: https://cli.github.com/"
    exit 1
fi

# Check if we're in a git repository
if ! git rev-parse --git-dir > /dev/null 2>&1; then
    echo "❌ Error: Not in a git repository"
    exit 1
fi

# Calculate due dates (2 weeks apart)
# Note: date command varies between macOS and Linux
# Using a more portable approach
echo "📅 Calculating milestone due dates..."
TODAY=$(date +%Y-%m-%d)
echo "   Today: $TODAY"

# Use Python for cross-platform date calculation if available, otherwise use basic date
if command -v python3 &> /dev/null; then
    DUE_1=$(python3 -c "from datetime import datetime, timedelta; print((datetime.now() + timedelta(days=14)).strftime('%Y-%m-%d'))")
    DUE_2=$(python3 -c "from datetime import datetime, timedelta; print((datetime.now() + timedelta(days=28)).strftime('%Y-%m-%d'))")
    DUE_3=$(python3 -c "from datetime import datetime, timedelta; print((datetime.now() + timedelta(days=42)).strftime('%Y-%m-%d'))")
    DUE_4=$(python3 -c "from datetime import datetime, timedelta; print((datetime.now() + timedelta(days=56)).strftime('%Y-%m-%d'))")
    DUE_5=$(python3 -c "from datetime import datetime, timedelta; print((datetime.now() + timedelta(days=70)).strftime('%Y-%m-%d'))")
else
    # Fallback: use date command (works on most Linux systems)
    DUE_1=$(date -d "$TODAY + 14 days" +%Y-%m-%d 2>/dev/null || date -v +14d +%Y-%m-%d 2>/dev/null || echo "2025-12-01")
    DUE_2=$(date -d "$TODAY + 28 days" +%Y-%m-%d 2>/dev/null || date -v +28d +%Y-%m-%d 2>/dev/null || echo "2025-12-15")
    DUE_3=$(date -d "$TODAY + 42 days" +%Y-%m-%d 2>/dev/null || date -v +42d +%Y-%m-%d 2>/dev/null || echo "2025-12-29")
    DUE_4=$(date -d "$TODAY + 56 days" +%Y-%m-%d 2>/dev/null || date -v +56d +%Y-%m-%d 2>/dev/null || echo "2026-01-12")
    DUE_5=$(date -d "$TODAY + 70 days" +%Y-%m-%d 2>/dev/null || date -v +70d +%Y-%m-%d 2>/dev/null || echo "2026-01-26")
fi

echo "   Sprint 1 Due: $DUE_1"
echo "   Sprint 2 Due: $DUE_2"
echo "   Sprint 3 Due: $DUE_3"
echo "   Sprint 4 Due: $DUE_4"
echo "   Sprint 5 Due: $DUE_5"
echo ""

echo "🏃 Creating Sprint 1 Milestone..."
gh milestone create "Sprint 1 - Critical API & Core Logic" \
  --description "Achieve comprehensive test coverage for all critical API classes that students and educators interact with directly. Target: 80%+ coverage on core API classes." \
  --due "$DUE_1" 2>/dev/null || echo "   (Milestone may already exist)"

echo "🏃 Creating Sprint 2 Milestone..."
gh milestone create "Sprint 2 - ML & Data Processing Logic" \
  --description "Achieve comprehensive test coverage for machine learning features and data processing components. Target: 75%+ coverage on ML and data classes." \
  --due "$DUE_2" 2>/dev/null || echo "   (Milestone may already exist)"

echo "🏃 Creating Sprint 3 Milestone..."
gh milestone create "Sprint 3 - Protocols, Communication, and Utilities" \
  --description "Achieve comprehensive test coverage for communication protocol implementation and utility classes. Target: 70%+ coverage on protocol classes." \
  --due "$DUE_3" 2>/dev/null || echo "   (Milestone may already exist)"

echo "🏃 Creating Sprint 4 Milestone..."
gh milestone create "Sprint 4 - System, Storage, and Integration" \
  --description "Achieve comprehensive test coverage for system-level components, storage operations, and end-to-end integration scenarios. Target: 80%+ coverage on system and storage." \
  --due "$DUE_4" 2>/dev/null || echo "   (Milestone may already exist)"

echo "🏃 Creating Sprint 5 Milestone..."
gh milestone create "Sprint 5 - Examples, Demos, and Edge Packages" \
  --description "Achieve comprehensive test coverage for example code, educational demonstrations, specialized packages, and edge cases. Target: 70%+ overall project coverage." \
  --due "$DUE_5" 2>/dev/null || echo "   (Milestone may already exist)"

echo ""
echo "🎉 All milestones created successfully!"
echo ""
echo "📝 Summary:"
echo "   - Sprint 1: Critical API & Core Logic"
echo "   - Sprint 2: ML & Data Processing Logic"
echo "   - Sprint 3: Protocols, Communication, and Utilities"
echo "   - Sprint 4: System, Storage, and Integration"
echo "   - Sprint 5: Examples, Demos, and Edge Packages"
echo ""
echo "🔗 View milestones at: https://github.com/$(gh repo view --json nameWithOwner -q .nameWithOwner)/milestones"
