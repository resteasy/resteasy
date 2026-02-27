# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

RESTEasy is a Jakarta RESTful Web Services (JAX-RS) implementation. It is a multi-module Maven project under `org.jboss.resteasy`, currently at version 7.x, targeting Java 17+.

## Build Commands

```bash
# Build without tests (most common for development)
./mvnw clean install -DskipTests=true

# Build with tests (requires WildFly - will be provisioned automatically)
./mvnw clean install -fae

# Build with a specific WildFly version
./mvnw clean install -fae -Dserver.version=37.0.1.Final

# Run a single test class
./mvnw -pl testsuite/unit-tests surefire:test@default-test -Dtest=TestClassName

# Run a single integration test (requires server provisioning first)
./mvnw -pl testsuite/integration-tests surefire:test@default-test -Dtest=TestClassName -Dserver.home=PATH_TO_WILDFLY

# Validate code formatting (CI runs this)
./mvnw -B validate -Pformat-check -Denforcer.skip=true

# Auto-format code (runs automatically during build, or explicitly)
./mvnw process-sources -Denforcer.skip=true

# See test output on console instead of files
./mvnw test -Ddebug.logs
```

## Code Formatting

Formatting is enforced by CI and auto-applied during builds. Two plugins handle this:

- **formatter-maven-plugin**: Code formatting using Eclipse formatter rules from `dev.resteasy.tools:ide-config`
- **impsort-maven-plugin**: Import ordering — groups: `java.`, `javax.`, `jakarta.`, `org.`, `com.` (static imports grouped separately, unused imports removed)

Format caches are stored in `.cache/` directories (not `target/`) for speed across clean builds. These directories are gitignored.

## Module Architecture

**Core modules** (build order matters):
- `resteasy-core-spi` — Service Provider Interfaces (extension points, base classes)
- `resteasy-core` — Core Jakarta REST implementation
- `resteasy-client-api` — Client API interfaces
- `resteasy-client` — Client implementation
- `resteasy-client-utils` — Client utilities

**Providers** (`providers/` directory) — serialization/deserialization:
- `jackson2` (JSON via Jackson), `jaxb` (XML), `json-binding` (JSON-B), `json-p-ee7` (JSON-P), `multipart`, `resteasy-atom`, `resteasy-html`, `resteasy-validator-provider`, `fastinfoset`

**Server adapters** (`server-adapters/`): `resteasy-undertow`, `resteasy-netty4`, `resteasy-vertx`

**Integration modules**: `resteasy-cdi`, `resteasy-servlet-initializer`, `resteasy-rxjava2`, `resteasy-reactor`, `resteasy-links` (HATEOAS), `resteasy-wadl`

**Security** (`security/`): `jose-jwt`, `resteasy-crypto`

**WildFly integration** (`wildfly/`): `resteasy-feature-pack`, `resteasy-channel` — Galleon feature pack and channel for WildFly distribution

**BOMs**: `resteasy-dependencies-bom` (internal dependency management), `resteasy-bom` (public BOM for consumers)

## Test Structure

- `testsuite/unit-tests` — Standalone unit tests (no container required)
- `testsuite/integration-tests` — Arquillian-based tests running in WildFly (largest suite)
- `testsuite/jetty-integration-tests` — Jetty server tests
- `testsuite/cloud-tests` — Cloud environment tests
- `testsuite/arquillian-utils` — Shared test utilities, deployment helpers, categories

Integration tests use Arquillian with ShrinkWrap for deployment creation. Tests use JUnit Jupiter (JUnit 5). Test output is redirected to `target/surefire-reports/` by default.

## Key Conventions

- Issues tracked in JIRA at `https://issues.redhat.com/browse/RESTEASY` (branch names typically reference JIRA IDs like `RESTEASY-NNNN`)
- Dependency versions are centralized in `resteasy-dependencies-bom/pom.xml`
- The `jboss-logging-processor` annotation processor is configured for all modules
- Maven wrapper (`./mvnw`) should be used instead of system Maven
- The project uses `jboss-parent` POM (v50) as its parent
