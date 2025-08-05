#!/bin/bash
# Script to copy all color dataset .txt files from Python reference to Java production directory
# Usage: ./scripts/copy_color_datasets.sh

# Set source and destination directories
PYTHON_DATASET_DIR="../reference/codrone_edu/data"
JAVA_DATASET_DIR="src/main/resources/color_data"

# Create destination directory if it doesn't exist
mkdir -p "$JAVA_DATASET_DIR"

# Copy all .txt files
cp "$PYTHON_DATASET_DIR"/*.txt "$JAVA_DATASET_DIR"/

echo "Copied all color dataset .txt files from $PYTHON_DATASET_DIR to $JAVA_DATASET_DIR."
