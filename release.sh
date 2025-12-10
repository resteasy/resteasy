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
    echo "Performs a release of the project. The release argument and value and the development argument and value are required parameters."
    echo "Any additional arguments are passed to the Maven command."
    echo ""
    printArgHelp "-d" "--development" "The next version for the development cycle."
    printArgHelp "-f" "--force" "Forces to allow a SNAPSHOT suffix in release version and not require one for the development version."
    printArgHelp "-h" "--help" "Displays this help."
    printArgHelp "" "--notes-start-tag" "When doing a GitHub release, indicates the tag to use as the starting point for generating release notes."
    printArgHelp "-p" "--prerelease" "Indicates this is a prerelease and the GitHub release should be marked as such."
    printArgHelp "-r" "--release" "The version to be released. Also used for the tag."
    printArgHelp "" "--dry-run" "Executes the release as a dry-run. Nothing will be updated or pushed."
    printArgHelp "-v" "--verbose" "Prints verbose output."
    echo ""
    echo "Usage: ${0##*/} --release 1.0.0 --development 1.0.1-SNAPSHOT"
}

# ============================================================================
# Color setup
# ============================================================================

CLEAR=""
RED=""
YELLOW=""

# Check if stdout is a terminal and NO_COLOR is not set
if [[ -t 1 ]] && [[ -z "${NO_COLOR-}" ]]; then
    if command -v tput >/dev/null 2>&1; then
        if [ "$(tput colors 2>/dev/null || echo 0)" -ge 8 ]; then
            CLEAR=$(tput sgr0)
            RED=$(tput setaf 1)
            YELLOW=$(tput setaf 3)
        fi
    fi
fi

# ============================================================================
# Variable initialization
# ============================================================================

DRY_RUN=false
FORCE=false
DEVEL_VERSION=""
RELEASE_VERSION=""
SCRIPT_PATH=$(realpath "${0}")
SCRIPT_DIR=$(dirname "${SCRIPT_PATH}")
LOCAL_REPO="/tmp/m2/repository/$(basename "${SCRIPT_DIR}")"
VERBOSE=false
GH_RELEASE_TYPE="--latest"
START_TAG=()
MAVEN_ARGS=()
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
DAYS="${DAYS:-5}" # Default to 5 if not set

# ============================================================================
# Argument parsing
# ============================================================================

while [ "$#" -gt 0 ]; do
    case "${1}" in
        -d|--development)
            DEVEL_VERSION="${2}"
            shift
            ;;
        --dry-run)
            DRY_RUN=true
            ;;
        -f|--force)
            FORCE=true
            ;;
        -h|--help)
            printHelp
            exit 0
            ;;
        --notes-start-tag)
            START_TAG=("--notes-from-tag" "${2}")
            shift
            ;;
        -p|--prerelease)
            GH_RELEASE_TYPE="--prerelease"
            ;;
        -r|--release)
            RELEASE_VERSION="${2}"
            shift
            ;;
        -v|--verbose)
            VERBOSE=true
            ;;
        *)
            MAVEN_ARGS+=("${1}")
            ;;
    esac
    shift
done

# ============================================================================
# Validation
# ============================================================================

if [ -z "${DEVEL_VERSION}" ]; then
    fail "The development version is required."
fi

if [ -z "${RELEASE_VERSION}" ]; then
    fail "The release version is required."
fi

if [ "${FORCE}" == "false" ]; then
    # Native bash string matching instead of grep
    if [[ "${RELEASE_VERSION}" == *"SNAPSHOT"* ]]; then
        failNoHelp "The release version appears to be a SNAPSHOT (${RELEASE_VERSION}). This is likely not valid. Use -f to force."
    fi
    if [[ "${DEVEL_VERSION}" != *"SNAPSHOT"* ]]; then
        failNoHelp "The development version does not appear to be a SNAPSHOT (${DEVEL_VERSION}). This is likely not valid. Use -f to force."
    fi
fi

# Find the expected Server ID
# We temporarily disable set -e here because mvn might fail if args are bad, and we want to capture that
set +e
SERVER_ID=$(mvn help:evaluate -Dexpression=nexus.serverId -q -DforceStdout "${MAVEN_ARGS[@]}" 2>/dev/null | sed 's/^\[INFO\] \[stdout\] //')
RET_CODE=$?
set -e

if [ ${RET_CODE} -ne 0 ]; then
    failNoHelp "Failed to evaluate Maven expression. Please check your Maven arguments."
fi

if [ -z "${SERVER_ID}" ]; then
    failNoHelp "Could not determine server ID from Maven configuration."
fi

# Check the settings to ensure a server defined with that value
if ! mvn help:effective-settings 2>/dev/null | grep -q "<id>${SERVER_ID}</id>"; then
    failNoHelp "A server with the id of \"${SERVER_ID}\" was not found in your settings.xml file."
fi

# ============================================================================
# Release preparation
# ============================================================================

printf "Performing release for version %s with the next version of %s\n" "${RELEASE_VERSION}" "${DEVEL_VERSION}"

TAG_NAME="v${RELEASE_VERSION}"
MVN_FLAGS=()

if ${DRY_RUN}; then
    echo "This will be a dry run and nothing will be updated or pushed."
    MVN_FLAGS+=("-DdryRun" "-DpushChanges=false")
fi

# ============================================================================
# Clean up local repository
# ============================================================================

if [ -d "${LOCAL_REPO}" ]; then
    # Verbose flag logic for rm
    RM_FLAGS="-rf"
    if ${VERBOSE}; then RM_FLAGS="-rfv"; fi

    # Delete any directories over DAYS old
    if find "${LOCAL_REPO}" -type d -mtime +"${DAYS}" -print0 2>/dev/null | grep -qz .; then
        find "${LOCAL_REPO}" -type d -mtime +"${DAYS}" -print0 | xargs -0 -I {} rm ${RM_FLAGS} "{}"
    fi

    # Delete any SNAPSHOTs
    if find "${LOCAL_REPO}" -type d -name "*SNAPSHOT" -print0 2>/dev/null | grep -qz .; then
        find "${LOCAL_REPO}" -type d -name "*SNAPSHOT" -print0 | xargs -0 -I {} rm ${RM_FLAGS} "{}"
    fi

    # Delete directories associated with this project
    set +e
    PROJECT_PATH="$(mvn help:evaluate -Dexpression=project.groupId -q -DforceStdout "${MAVEN_ARGS[@]}" 2>/dev/null)"
    RET_CODE=$?
    set -e

    if [ ${RET_CODE} -eq 0 ] && [ -n "${PROJECT_PATH}" ]; then
        # Safe replacement of dots with slashes
        PROJECT_PATH="${LOCAL_REPO}/${PROJECT_PATH//./\/}"

        if [ -d "${PROJECT_PATH}" ]; then
            rm ${RM_FLAGS} "${PROJECT_PATH}"
        fi
    fi
fi

# ============================================================================
# Execute Maven release
# ============================================================================

# Create the command as an array
CMD=(mvn clean release:clean release:prepare release:perform)
CMD+=("-Dmaven.repo.local=${LOCAL_REPO}")
CMD+=("-Prelease,cloud-tests")
CMD+=("-DdevelopmentVersion=${DEVEL_VERSION}")
CMD+=("-DreleaseVersion=${RELEASE_VERSION}")
CMD+=("-Dtag=${TAG_NAME}")

# Append extra maven flags calculated earlier
if [ ${#MVN_FLAGS[@]} -gt 0 ]; then
    CMD+=("${MVN_FLAGS[@]}")
fi

# Append any pass-through arguments
if [ ${#MAVEN_ARGS[@]} -gt 0 ]; then
    CMD+=("${MAVEN_ARGS[@]}")
fi

if ${VERBOSE}; then
    printf "\n\nExecuting:\n  %s\n\n" "${CMD[*]}"
fi

# Execute the command
# "${CMD[@]}" expands the array respecting spaces within arguments
"${CMD[@]}"
status=$?

# ============================================================================
# Post-release
# ============================================================================

if [ ${status} -eq 0 ]; then
    # Get the path to the ZIP files
    distZip=$(readlink -e distribution/target/distribution/*.zip 2>/dev/null || echo "")
    srcZip=$(readlink -e distribution/src-distribution/target/distribution/*.zip 2>/dev/null || echo "")

    # ============================================================================
    # GitHub Release Instructions
    # ============================================================================

    echo ""
    if command -v gh &>/dev/null; then
        # Check for default repo quietly
        if ! gh repo set-default --view &>/dev/null; then
            echo -e "${RED}No default repository has been set. You must use gh repo set-default to set a default repository before executing the following commands.${CLEAR}"
            echo ""
            echo "gh release create --generate-notes ${START_TAG[*]} ${GH_RELEASE_TYPE} --verify-tag ${TAG_NAME}"
        else
            if ${DRY_RUN}; then
                printf "${YELLOW}Dry run would execute:${CLEAR}\ngh release create --generate-notes %s %s --verify-tag %s\n" "${START_TAG[*]}" "${GH_RELEASE_TYPE}" "${TAG_NAME}"
            else
                if gh release create --generate-notes "${START_TAG[@]}" ${GH_RELEASE_TYPE} --verify-tag "${TAG_NAME}"; then
                    echo "GitHub release created successfully."
                else
                    echo "${RED}Warning: Failed to create GitHub release.${CLEAR}"
                fi
            fi
        fi
        if [ -n "${distZip}" ] && [ -n "${srcZip}" ]; then
            if ${DRY_RUN}; then
                printf "${YELLOW}Dry run would execute:${CLEAR}\ngh release upload %s %s %s\n" "${TAG_NAME}" "${distZip}" "${srcZip}"
            else
                if gh release upload "${TAG_NAME}" "${distZip}" "${srcZip}"; then
                    echo "Release artifacts uploaded successfully."
                else
                    echo "${RED}Warning: Failed to upload release artifacts.${CLEAR}"
                fi
            fi
        fi
    else
        echo "The gh command is not available. You must manually create a release for the GitHub tag ${TAG_NAME}."
        if [ -n "${distZip}" ] || [ -n "${srcZip}" ]; then
            echo "The following files should be attached to the release:"
            if [ -n "${distZip}" ]; then echo "  ${distZip}"; fi
            if [ -n "${srcZip}" ]; then echo "  ${srcZip}"; fi
        fi
    fi

    # ============================================================================
    # Documentation Publishing Instructions
    # ============================================================================

    echo ""
    # Check if distribution ZIP with documentation exists (reuse distZip from above)
    if [ -n "${distZip}" ] && [ -f "${distZip}" ]; then
        DOCS_REPO="../resteasy.github.io"
        if [ ! -d "${DOCS_REPO}" ]; then
            echo "${YELLOW}Warning: Could not find resteasy.github.io repository.${CLEAR}"
            DOCS_REPO="/path/to/resteasy.github.io"
            echo "./publish-docs.sh --version ${RELEASE_VERSION} --docs-repo ${DOCS_REPO} --dist-zip ${distZip}"
        else
            if ${DRY_RUN}; then
                printf "${YELLOW}Dry run would execute:${CLEAR}\n./publish-docs.sh --version %s --docs-repo %s --dist-zip %s\n" "${RELEASE_VERSION}" "${DOCS_REPO}" "${distZip}"
            else
                echo "Publishing documentation:"
                if ./publish-docs.sh --version "${RELEASE_VERSION}" --docs-repo "${DOCS_REPO}" --dist-zip "${distZip}"; then
                    echo "Documentation published successfully."
                else
                    echo "${RED}Warning: Failed to publish documentation.${CLEAR}"
                fi
            fi
        fi
    else
        echo "${YELLOW}Warning: Distribution ZIP not found. Documentation may not have been packaged.${CLEAR}"
    fi
else
    printf "\n%sThe release has failed.%s See the previous errors and try again.\n" "${RED}" "${CLEAR}"
    if ${VERBOSE}; then
        printf "The command executed was:\n%s\n" "${CMD[*]}"
    fi
fi

exit ${status}
