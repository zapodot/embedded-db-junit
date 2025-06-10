#!/usr/bin/env bash

# Validates semantic version number
validate_version() {
    local version=$1
    if [[ ! $version =~ ^[0-9]+\.[0-9]+\.[0-9]+(-[a-zA-Z0-9.-]+)?(\+[a-zA-Z0-9.-]+)?$ ]]; then
        echo "Error: '$version' is not a valid semantic version."
        echo "Format: MAJOR.MINOR.PATCH (ex. 1.2.3, 2.0.0-beta.1)"
        exit 1
    fi
}

# Function to increment patch version for next development cycle
increment_patch_version() {
    local version=$1
    # Extract major, minor, and patch numbers
    local major=$(echo "$version" | cut -d. -f1)
    local minor=$(echo "$version" | cut -d. -f2)
    local patch=$(echo "$version" | cut -d. -f3 | cut -d- -f1)

    # Increment patch version
    patch=$((patch + 1))

    # Return new version
    echo "${major}.${minor}.${patch}"
}

# Sjekk at versjonsnummer er oppgitt
if [ $# -eq 0 ]; then
    echo "Usage: $0 <version_number>"
    echo "Example: $0 1.2.3"
    exit 1
fi

VERSION=$1

# Validates version number
validate_version "$VERSION"

# Make sure there is a pom.xml-file in the folder
if [ ! -f "pom.xml" ]; then
    echo "Error: could not find pom.xml in the current folder."
    exit 1
fi

# Make sure that we are in a git repo
if ! git rev-parse --git-dir > /dev/null 2>&1; then
    echo "Error: Current folder is not a git repository."
    exit 1
fi

# Check if working directory is clean (no uncommitted changes)
if ! git diff-index --quiet HEAD --; then
    echo "Error: Working directory contains uncommitted changes."
    echo "Please commit or stash your changes before running this script."
    echo ""
    echo "Uncommitted changes:"
    git status --porcelain
    exit 1
fi

echo "Updates version to $VERSION..."

# Updates version in the pom using maven versions-plugin
mvn versions:set -DnewVersion="$VERSION" -DgenerateBackupPoms=false

# Make sure the versions-plugin was successful
if [ $? -ne 0 ]; then
    echo "Error: Could not update version in pom.xml"
    exit 1
fi

echo "Committing changes to git..."

# Add changes to git
git add .
git commit -m "Release version $VERSION"

# Make sure the commit was successful
if [ $? -ne 0 ]; then
    echo "Error: Failed to commit changes"
    exit 1
fi

echo "creates git-tag $VERSION..."

# Lag git tag
git tag -a "$VERSION" -m "Release version $VERSION"

# Sjekk at tagging var vellykket
if [ $? -ne 0 ]; then
    echo "Error: Could not create git tag"
    exit 1
fi

# Calculate next development version by incrementing patch number
NEXT_VERSION=$(increment_patch_version "$VERSION")
SNAPSHOT_VERSION="${NEXT_VERSION}-SNAPSHOT"

echo "Preparing for next development cycle with version $SNAPSHOT_VERSION..."

# Set next SNAPSHOT version for development
mvn versions:set -DnewVersion="$SNAPSHOT_VERSION" -DgenerateBackupPoms=false

# Check if the version update was successful
if [ $? -ne 0 ]; then
    echo "Error: Could not set next development version"
    exit 1
fi

echo "Committing development version changes..."

# Add and commit the SNAPSHOT version change
git add .
git commit -m "Prepare for next development iteration $SNAPSHOT_VERSION"

# Check if the commit was successful
if [ $? -ne 0 ]; then
    echo "Error: Failed to commit development version changes"
    exit 1
fi

echo "Success! Version $VERSION is updated, committed and tagged."
echo "Next development version $SNAPSHOT_VERSION is set and committed."
echo "Remember to push the changes: git push && git push --tags"