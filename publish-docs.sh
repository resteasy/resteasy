#!/usr/bin/env bash

#
# Copyright The RESTEasy Authors
# SPDX-License-Identifier: Apache-2.0
#

# Strict mode: fail on error, fail on unset vars, fail on pipe failure
set -o errexit
set -o nounset
set -o pipefail

# ============================================================================
# Formatting functions
# ============================================================================

fail() {
    printf "%s%s%s\n\n" "${RED}" "${1}" "${CLEAR}"
    printHelp
    exit 1
}

failNoHelp() {
    printf "%s%s%s\n" "${RED}" "${1}" "${CLEAR}"
    exit 1
}

printArgHelp() {
    if [ -z "${1}" ]; then
        printf "${YELLOW}    %-20s${CLEAR}%s\n" "${2}" "${3}"
    else
        printf "${YELLOW}%s, %-20s${CLEAR}%s\n" "${1}" "${2}" "${3}"
    fi
}

printHelp() {
    echo "Publishes RESTEasy documentation from a release distribution ZIP to the resteasy.github.io repository."
    echo "Creates a branch, commits changes, pushes to origin, and creates a pull request."
    echo ""
    printArgHelp "-v" "--version" "The release version (required, e.g., 7.0.1.Final)"
    printArgHelp "-d" "--docs-repo" "Path to resteasy.github.io repository (required)"
    printArgHelp "-z" "--dist-zip" "Path to distribution ZIP (required)"
    printArgHelp "" "--verbose" "Print verbose output"
    printArgHelp "-h" "--help" "Display this help message"
    echo ""
    echo "Usage: ${0##*/} --version 7.0.1.Final --docs-repo ~/projects/resteasy/resteasy.github.io --dist-zip /path/to/resteasy-7.0.1.Final-all.zip"
}

# ============================================================================
# Color setup
# ============================================================================

CLEAR=""
RED=""
YELLOW=""
GREEN=""

# Check if stdout is a terminal and NO_COLOR is not set
if [[ -t 1 ]] && [[ -z "${NO_COLOR-}" ]]; then
    if command -v tput >/dev/null 2>&1; then
        if [ "$(tput colors 2>/dev/null || echo 0)" -ge 8 ]; then
            CLEAR=$(tput sgr0)
            RED=$(tput setaf 1)
            YELLOW=$(tput setaf 3)
            GREEN=$(tput setaf 2)
        fi
    fi
fi

# ============================================================================
# Variable initialization
# ============================================================================

VERBOSE=false
RELEASE_VERSION=""
DOCS_REPO=""
DIST_ZIP=""
TEMP_DIR=""

# ============================================================================
# Argument parsing
# ============================================================================

while [ "$#" -gt 0 ]; do
    case "${1}" in
        -v|--version)
            RELEASE_VERSION="${2}"
            shift
            ;;
        -d|--docs-repo)
            DOCS_REPO="${2}"
            shift
            ;;
        -z|--dist-zip)
            DIST_ZIP="${2}"
            shift
            ;;
        --verbose)
            VERBOSE=true
            ;;
        -h|--help)
            printHelp
            exit 0
            ;;
        *)
            fail "Unknown argument: ${1}"
            ;;
    esac
    shift
done

# ============================================================================
# Validation
# ============================================================================

if [ -z "${RELEASE_VERSION}" ]; then
    fail "The release version is required (use --version)."
fi

if [ -z "${DOCS_REPO}" ]; then
    fail "The documentation repository path is required (use --docs-repo)."
fi

if [ -z "${DIST_ZIP}" ]; then
    fail "The distribution ZIP path is required (use --dist-zip)."
fi

# Expand tilde in paths
DOCS_REPO="${DOCS_REPO/#\~/$HOME}"
DIST_ZIP="${DIST_ZIP/#\~/$HOME}"

if [ ! -d "${DOCS_REPO}" ]; then
    failNoHelp "Documentation repository not found: ${DOCS_REPO}"
fi

if [ ! -d "${DOCS_REPO}/.git" ]; then
    failNoHelp "Documentation repository is not a git repository: ${DOCS_REPO}"
fi

if [ ! -f "${DIST_ZIP}" ]; then
    failNoHelp "Distribution ZIP not found: ${DIST_ZIP}"
fi

# ============================================================================
# Extract major.minor version from release version
# ============================================================================

# Extract major.minor from the release version (e.g., 7.0.1.Final -> 7.0)
if [[ "${RELEASE_VERSION}" =~ ^([0-9]+)\.([0-9]+)\. ]]; then
    MAJOR_MINOR="${BASH_REMATCH[1]}.${BASH_REMATCH[2]}"
else
    failNoHelp "Could not extract major.minor version from release version: ${RELEASE_VERSION}\nExpected format: X.Y.Z.Qualifier (e.g., 7.0.1.Final)"
fi

if ${VERBOSE}; then
    echo "Version: ${RELEASE_VERSION} (major.minor: ${MAJOR_MINOR})"
    echo "Docs repo: ${DOCS_REPO}"
    echo "Dist ZIP: ${DIST_ZIP}"
fi

# ============================================================================
# Extract documentation from ZIP
# ============================================================================

TEMP_DIR=$(mktemp -d -t resteasy-docs-XXXXXX)

# Cleanup function
cleanup() {
    if [ -n "${TEMP_DIR}" ] && [ -d "${TEMP_DIR}" ]; then
        rm -rf "${TEMP_DIR}"
    fi
}
trap cleanup EXIT

echo "Extracting documentation from ${DIST_ZIP##*/}..."
if ! unzip -q "${DIST_ZIP}" "resteasy-${RELEASE_VERSION}/docs/*" -d "${TEMP_DIR}"; then
    failNoHelp "Failed to extract documentation from ZIP: ${DIST_ZIP}"
fi

# Validate extracted documentation
EXTRACTED_JAVADOCS="${TEMP_DIR}/resteasy-${RELEASE_VERSION}/docs/javadocs"
EXTRACTED_USERGUIDE="${TEMP_DIR}/resteasy-${RELEASE_VERSION}/docs/userguide"

if [ ! -d "${EXTRACTED_JAVADOCS}" ] || [ ! -d "${EXTRACTED_USERGUIDE}" ]; then
    failNoHelp "Documentation not found in distribution ZIP (expected javadocs/ and userguide/)"
fi

# ============================================================================
# Sync and check documentation repository
# ============================================================================

cd "${DOCS_REPO}"

# Sync with upstream
echo "Syncing documentation repository..."
if ! git pull --rebase 2>/dev/null; then
    failNoHelp "Failed to sync documentation repository. Please ensure you have an upstream configured or sync manually."
fi

# Check if repository has uncommitted changes
if ! git diff-index --quiet HEAD -- 2>/dev/null; then
    failNoHelp "Documentation repository has uncommitted changes. Please commit or stash them first.\nRepository: ${DOCS_REPO}"
fi

# ============================================================================
# Publish documentation
# ============================================================================

echo ""
echo "Publishing documentation for ${RELEASE_VERSION} (${MAJOR_MINOR})..."
echo ""

if [ -d "${MAJOR_MINOR}" ]; then
    echo "Removing existing ${MAJOR_MINOR}/ directory..."
    rm -rf "${MAJOR_MINOR}"
else
    mkdir -p "${MAJOR_MINOR}"
fi

# ============================================================================
# Create version directory and copy documentation
# ============================================================================

echo "Creating ${MAJOR_MINOR}/ directory structure..."
mkdir -p "${MAJOR_MINOR}/javadocs"
mkdir -p "${MAJOR_MINOR}/userguide"

echo "Copying JavaDocs ${EXTRACTED_JAVADOCS}/* to ${MAJOR_MINOR}/javadocs/"
cp -r "${EXTRACTED_JAVADOCS}"/* "${MAJOR_MINOR}/javadocs/"

echo "Copying user guide ${EXTRACTED_USERGUIDE}/* to ${MAJOR_MINOR}/userguide/"
cp -r "${EXTRACTED_USERGUIDE}"/* "${MAJOR_MINOR}/userguide/"

# ============================================================================
# Create branch, commit, and push
# ============================================================================

echo ""
echo "Creating branch and committing changes..."
echo ""

# Generate branch name and commit message
BRANCH_NAME="docs-${RELEASE_VERSION}"
COMMIT_MSG="Update ${MAJOR_MINOR} documentation to ${RELEASE_VERSION}"

# Create and checkout new branch
echo "Creating branch: ${BRANCH_NAME}"
git checkout -b "${BRANCH_NAME}"

# Stage all changes
echo "Staging changes..."
git add .

# Commit
echo "Committing changes..."
git commit -s -m "${COMMIT_MSG}"

# Push to origin
echo "Pushing to origin/${BRANCH_NAME}..."
git push -u origin "${BRANCH_NAME}"

# ============================================================================
# Create pull request
# ============================================================================

echo ""
if command -v gh &>/dev/null; then
    # gh CLI is available - try to create PR
    if ! gh repo set-default --view &>/dev/null; then
        # Get GitHub username for fork workflow
        GH_USER=$(gh api user -q .login 2>/dev/null || echo "")
        if [ -n "${GH_USER}" ]; then
            HEAD_REF="${GH_USER}:${BRANCH_NAME}"
        else
            HEAD_REF="${BRANCH_NAME}"
        fi

        echo "${YELLOW}Warning: No default repository set for gh CLI.${CLEAR}"
        echo "Run: gh repo set-default"
        echo ""
        echo "Then create PR with:"
        echo "gh pr create --fill --base main --head ${HEAD_REF}"
    else
        # Determine the head ref (handle fork workflow)
        GH_USER=$(gh api user -q .login 2>/dev/null || echo "")
        if [ -n "${GH_USER}" ]; then
            HEAD_REF="${GH_USER}:${BRANCH_NAME}"
        else
            HEAD_REF="${BRANCH_NAME}"
        fi

        # Create PR
        echo "Creating pull request..."
        PR_URL=$(gh pr create --fill --base main --head "${HEAD_REF}")
        echo ""
        echo "${GREEN}Pull request created successfully!${CLEAR}"
        echo "PR URL: ${PR_URL}"
    fi
else
    # gh CLI not available - print manual commands
    echo "The gh CLI is not available. To create a pull request manually:"
    echo ""
    echo "Visit: https://github.com/resteasy/resteasy.github.io/compare/main...${BRANCH_NAME}"
    echo ""
    echo "Or install gh CLI and run:"
    echo "gh pr create --fill --base main --head ${BRANCH_NAME}"
fi

echo ""
echo "Summary:"
echo "  Version: ${RELEASE_VERSION}"
echo "  Major.minor: ${MAJOR_MINOR}"
echo "  Branch: ${BRANCH_NAME}"
echo ""
echo "Verify GitHub Pages updated after PR is merged (may take a few minutes):"
echo "  https://resteasy.github.io/"
echo ""
