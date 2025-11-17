#!/bin/bash

# Test Coverage Improvement Project - Label Creation Script
# This script creates all labels needed for the project using GitHub CLI

set -e

echo "🏷️  Creating labels for Test Coverage Improvement Project..."
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

echo "📋 Creating Priority Labels..."
gh label create "priority-critical" --color "B60205" --description "Must complete immediately - blocks other work" --force 2>/dev/null || true
gh label create "priority-high" --color "D93F0B" --description "Complete in current sprint" --force 2>/dev/null || true
gh label create "priority-medium" --color "FBCA04" --description "Schedule for upcoming sprint" --force 2>/dev/null || true
gh label create "priority-low" --color "0E8A16" --description "Nice to have, schedule when capacity allows" --force 2>/dev/null || true
echo "✅ Priority labels created"

echo ""
echo "🗓️  Creating Sprint Labels..."
gh label create "sprint-1" --color "5319E7" --description "Sprint 1: Critical API & Core Logic" --force 2>/dev/null || true
gh label create "sprint-2" --color "D876E3" --description "Sprint 2: ML & Data Processing Logic" --force 2>/dev/null || true
gh label create "sprint-3" --color "1D76DB" --description "Sprint 3: Protocols, Communication, and Utilities" --force 2>/dev/null || true
gh label create "sprint-4" --color "0E8A16" --description "Sprint 4: System, Storage, and Integration" --force 2>/dev/null || true
gh label create "sprint-5" --color "BFD4F2" --description "Sprint 5: Examples, Demos, and Edge Packages" --force 2>/dev/null || true
echo "✅ Sprint labels created"

echo ""
echo "🧪 Creating Type Labels..."
gh label create "test" --color "7FE9A3" --description "Test-related work" --force 2>/dev/null || true
gh label create "coverage" --color "C5DEF5" --description "Coverage improvement specific" --force 2>/dev/null || true
gh label create "unit-test" --color "BFDB38" --description "Unit test development" --force 2>/dev/null || true
gh label create "integration-test" --color "5E7D4F" --description "Integration test development" --force 2>/dev/null || true
gh label create "documentation" --color "0075CA" --description "Test documentation" --force 2>/dev/null || true
echo "✅ Type labels created"

echo ""
echo "📊 Creating Status Labels..."
gh label create "ready" --color "0E8A16" --description "Ready to start - all prerequisites met" --force 2>/dev/null || true
gh label create "in-progress" --color "FBCA04" --description "Currently being worked on" --force 2>/dev/null || true
gh label create "blocked" --color "D93F0B" --description "Blocked by dependencies or issues" --force 2>/dev/null || true
gh label create "review" --color "5319E7" --description "Ready for code review" --force 2>/dev/null || true
gh label create "done" --color "0B4F30" --description "Completed and merged" --force 2>/dev/null || true
echo "✅ Status labels created"

echo ""
echo "🔧 Creating Component Labels..."
gh label create "component-api" --color "1D76DB" --description "Core API classes (Drone, Controllers)" --force 2>/dev/null || true
gh label create "component-protocol" --color "006B75" --description "Protocol implementation" --force 2>/dev/null || true
gh label create "component-system" --color "D876E3" --description "System-level functionality" --force 2>/dev/null || true
gh label create "component-storage" --color "FEF2C0" --description "Storage operations" --force 2>/dev/null || true
gh label create "component-ml" --color "F9D0C4" --description "Machine learning features" --force 2>/dev/null || true
gh label create "component-autonomous" --color "C2E0C6" --description "Autonomous flight features" --force 2>/dev/null || true
gh label create "component-tools" --color "BFDADC" --description "Developer tools and utilities" --force 2>/dev/null || true
gh label create "component-examples" --color "FFC0CB" --description "Example code and demos" --force 2>/dev/null || true
gh label create "component-buzzer" --color "FFD700" --description "Buzzer/audio functionality" --force 2>/dev/null || true
gh label create "component-display" --color "E99695" --description "Display/screen functionality" --force 2>/dev/null || true
echo "✅ Component labels created"

echo ""
echo "🎉 All labels created successfully!"
echo ""
echo "📝 Summary:"
echo "   - 4 Priority labels"
echo "   - 5 Sprint labels"
echo "   - 5 Type labels"
echo "   - 5 Status labels"
echo "   - 10 Component labels"
echo "   ─────────────────"
echo "   Total: 29 labels"
echo ""
echo "🔗 View labels at: https://github.com/$(gh repo view --json nameWithOwner -q .nameWithOwner)/labels"
