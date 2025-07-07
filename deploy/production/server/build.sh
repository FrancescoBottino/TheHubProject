#!/bin/bash

set -e

# Resolve script directory, even when run from project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"

# Paths (relative to project root)
BUILD_OUTPUT_FILE="$PROJECT_ROOT/server/build/libs/server-all.jar"
TARGET_DIR="$SCRIPT_DIR/output"
TARGET_FILE="$TARGET_DIR/ktor-server.jar"

# 1. Build the wasm output
echo "🛠️ Building Compose WASM app..."
(cd "$PROJECT_ROOT" && ./gradlew :server:buildFatJar -Penvironment=prod)

# 2. Clean target static folder
echo "🧹 Cleaning $TARGET_DIR..."
rm -rf "$TARGET_DIR"
mkdir -p "$TARGET_DIR"

# 3. Copy build output
echo "📦 Copying build output to $TARGET_FILE..."
cp -r "$BUILD_OUTPUT_FILE" "$TARGET_FILE"

echo "✅ Done! Static site is ready at $TARGET_FILE"