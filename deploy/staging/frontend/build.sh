#!/bin/bash

set -e

# Resolve script directory, even when run from project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Paths (relative to project root)
BUILD_OUTPUT="$PROJECT_ROOT/composeApp/build/dist/wasmJs/productionExecutable"
TARGET_DIR="$SCRIPT_DIR/output"

# 1. Build the wasm output
echo "🛠️ Building Compose WASM app..."
(cd "$PROJECT_ROOT" && ./gradlew :composeApp:wasmJsBrowserDistribution -Penvironment=staging)

# 2. Clean target static folder
echo "🧹 Cleaning $TARGET_DIR..."
rm -rf "$TARGET_DIR"
mkdir -p "$TARGET_DIR"

# 3. Copy build output
echo "📦 Copying build output to $TARGET_DIR..."
cp -r "$BUILD_OUTPUT"/* "$TARGET_DIR"

echo "✅ Done! Static site is ready at $TARGET_DIR"