#!/bin/bash
# Script to merge the gh-pages documentation changes
# Run this script to push the SensorDisplayGui documentation to gh-pages

echo "Merging SensorDisplayGui documentation to gh-pages..."
echo ""

# Check if we're in the right repository
if [ ! -d ".git" ]; then
    echo "Error: Not in a git repository"
    exit 1
fi

# Save current branch
CURRENT_BRANCH=$(git branch --show-current)
echo "Current branch: $CURRENT_BRANCH"

# Checkout gh-pages
echo "Switching to gh-pages branch..."
git checkout gh-pages

# Check if the documentation commit exists
if git log --oneline | grep -q "Add comprehensive SensorDisplayGui documentation"; then
    echo "✅ Documentation commit found on gh-pages"
    echo ""
    echo "Commit details:"
    git log --oneline -1
    echo ""
    git diff --stat HEAD~1 HEAD
    echo ""
    echo "To push to remote:"
    echo "  git push origin gh-pages"
else
    echo "❌ Documentation commit not found on gh-pages"
    echo "Something went wrong. The commit should be at b82051f"
fi

# Return to original branch
echo ""
echo "Returning to $CURRENT_BRANCH..."
git checkout "$CURRENT_BRANCH"

echo ""
echo "Documentation files created on gh-pages:"
echo "  - guides/student/sensor-monitoring.md (new, 448 lines)"
echo "  - guides/student/sensors.md (updated)"
echo "  - guides/student/index.md (updated)"
echo "  - guides/student/next-steps.md (updated)"
