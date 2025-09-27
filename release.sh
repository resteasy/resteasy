#!/bin/bash -i

fail() {
    printf "%s\n\n" "${1}"
    printHelp
    exit 1
}

failNoHelp() {
    printf "%s\n" "${1}"
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
    echo "Any addition arguments are passed to the Maven command."
    echo ""
    printArgHelp "-d" "--development" "The next version for the development cycle."
    printArgHelp "-f" "--force" "Forces to allow a SNAPSHOT suffix in release version and not require one for the development version."
    printArgHelp "-h" "--help" "Displays this help."
    printArgHelp "-r" "--release" "The version to be released. Also used for the tag."
    printArgHelp "" "--dry-run" "Executes the release in as a dry-run. Nothing will be updated or pushed."
    printArgHelp "-v" "--verbose" "Prints verbose output."
    echo ""
    echo "Usage: ${0##*/} --release 1.0.0 --development 1.0.1-SNAPSHOT"
}


CLEAR=""
RED=""
YELLOW=""

if [[ -t 1 ]] && [[ -z "${NO_COLOR-}" ]] && [ "$(tput colors)" -ge 8 ]; then
    CLEAR="\033[0m"
    RED="\033[0;31m"
    YELLOW="\033[0;33m"
fi

DRY_RUN=false
FORCE=false
DEVEL_VERSION=""
RELEASE_VERSION=""
SCRIPT_PATH=$(realpath "${0}")
SCRIPT_DIR=$(dirname "${SCRIPT_PATH}")
LOCAL_REPO="/tmp/m2/repository/$(basename "${SCRIPT_DIR}")"
VERBOSE=""
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

MAVEN_ARGS=()

if [ -z "${DAYS}" ]; then
    DAYS="5"
fi

while [ "$#" -gt 0 ]
do
    case "${1}" in
        -d|--development)
            DEVEL_VERSION="${2}"
            shift
            ;;
        --dry-run)
            DRY_RUN=true
            ;;
        -f|--force)
            FORCE=true;
            ;;
        -h|--help)
            printHelp
            exit 0
            ;;
        -r|--release)
            RELEASE_VERSION="${2}"
            shift
            ;;
        -v|--verbose)
            VERBOSE="-v"
            ;;
        *)
            MAVEN_ARGS+=("${1}")
            ;;
    esac
    shift
done

if [ -z "${DEVEL_VERSION}" ]; then
    fail "The development version is required."
fi

if [ -z "${RELEASE_VERSION}" ]; then
    fail "The release version is required."
fi

if [ ${FORCE} == false ]; then
    if  echo "${RELEASE_VERSION}" | grep -q "SNAPSHOT" ; then
        failNoHelp "The release version appears to be a SNAPSHOT (${RELEASE_VERSION}). This is likely no valid and -f should be used if it is."
    fi
    if  echo "${DEVEL_VERSION}" | grep -q -v "SNAPSHOT" ; then
        failNoHelp "The development version does not appear to be a SNAPSHOT (${DEVEL_VERSION}). This is likely no valid and -f should be used if it is."
    fi
fi

# Find the expected
SERVER_ID=$(mvn help:evaluate -Dexpression=nexus.serverId -q -DforceStdout "${MAVEN_ARGS[@]}")
# Check the settings to ensure a server defined with that value
if ! mvn help:effective-settings | grep -q "<id>${SERVER_ID}</id>"; then
    failNoHelp "A server with the id of \"${SERVER_ID}\" was not found in your settings.xml file."
fi

printf "Performing release for version %s with the next version of %s\n" "${RELEASE_VERSION}" "${DEVEL_VERSION}"

TAG_NAME="v${RELEASE_VERSION}"

if ${DRY_RUN}; then
    echo "This will be a dry run and nothing will be updated or pushed."
    MAVEN_ARGS+=("-DdryRun")
fi

if [ -d "${LOCAL_REPO}" ]; then
    # Delete any directories over a day old
    find "${LOCAL_REPO}" -type d -mtime +"${DAYS}" -print0 | xargs -0 -I {} rm -rf ${VERBOSE} "{}"
    # Delete any SNAPSHOT's
    find "${LOCAL_REPO}" -type d -name "*SNAPSHOT" -print0 | xargs -0 -I {} rm -rf ${VERBOSE} "{}"
    # Delete directories associated with this project
    PROJECT_PATH="$(mvn help:evaluate -Dexpression=project.groupId -q -DforceStdout "${MAVEN_ARGS[@]}")"
    PROJECT_PATH="${LOCAL_REPO}/${PROJECT_PATH//./\/}"
    rm -rf ${VERBOSE} "${PROJECT_PATH}"
fi

# Prepare the command
command="mvn clean release:clean release:prepare release:perform -Dmaven.repo.local=\"${LOCAL_REPO}\" -Prelease -DdevelopmentVersion=\"${DEVEL_VERSION}\" -DreleaseVersion=\"${RELEASE_VERSION}\" -Dtag=\"${TAG_NAME}\" ${MAVEN_ARGS[*]}"
if [ "-v" = "${VERBOSE}" ]; then
    printf "\n\nExecuting:\n  "
    echo "${command}"
fi
# Execute the command
eval "${command}"
status=$?

if [ ${status} = 0 ]; then
    # Get the path to the ZIP files
    distZip=$(readlink -e distribution/target/distribution/*.zip)
    srcZip=$(readlink -e distribution/src-distribution/target/distribution/*.zip)

    echo ""
    echo "Your release has been successful. Check the validation after $(date -d "now + 10 minutes" +"%H:%M:%S") on https://repository.jboss.org/nexus."
    echo "Once validation is successful, execute the following command:"
    echo ""
    echo "git checkout ${TAG_NAME}"
    echo "mvn nxrm3:staging-move -Dmaven.repo.local=\"${LOCAL_REPO}\""
    echo "git checkout ${CURRENT_BRANCH}"
    echo "git push upstream ${CURRENT_BRANCH}"
    echo "git push upstream ${TAG_NAME}"

    if command -v gh &>/dev/null; then
        result="$(gh repo set-default --view 2>&1)"
        if [[ "${result}" =~ "gh repo set-default" ]]; then
            echo ""
            echo -e "${RED}The no default repository has been set. You must use gh repo set-default to set a default repository before executing the following commands.${CLEAR}"
        fi
        echo ""
        echo "gh release create --generate-notes --latest --verify-tag ${TAG_NAME}"
        echo "gh release upload ${TAG_NAME} ${distZip} ${srcZip}"
    else
        echo ""
        echo "The gh commands are not available. You must manually create a release for the GitHub tag ${TAG_NAME}. The following files should be attached to the release."
        echo "${distZip}"
        echo "${srcZip}"
    fi
    echo ""
    echo "You must also create a pull request to https://github.com/resteasy/resteasy.github.io with the documentation content."
else
    printf "\nThe release has failed. See the previous errors and try again. The command executed was:\n%s\n" "${command}"
fi
exit ${status}

