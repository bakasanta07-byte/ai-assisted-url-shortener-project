# AI-Assisted Engineering Execution Log

The AI assistant was used as an accelerator inside engineer-owned tasks. Generated output was treated as untrusted source code and reviewed against acceptance criteria before acceptance.

## Task 1 — API contract
**Intent:** Define a minimal REST API for create, redirect, analytics and delete.

**Constraints:** Java 17, Spring Boot, REST, predictable HTTP semantics, validation.

**Acceptance criteria:**
- Create returns 201.
- Redirect returns 302.
- Unknown/expired code returns 404.
- Analytics is read-only.
- Delete returns 204.

**AI use:** Requested endpoint and DTO candidates.

**Engineer decision:** Kept the contract small and versioned under `/api/v1`; rejected unnecessary endpoints such as bulk import and user accounts.

## Task 2 — persistence and collision handling
**Intent:** Persist short URLs and guarantee code uniqueness.

**AI use:** Generated an initial JPA entity/repository design.

**Engineer changes:**
- Added database uniqueness constraint.
- Added bounded retry on collision.
- Changed persistence to `saveAndFlush` so a uniqueness collision is detected inside the allocation loop.

## Task 3 — concurrent click counting
**Intent:** Ensure concurrent redirects do not silently overwrite each other's click counts.

**AI use:** Asked for concurrency failure modes in a JPA counter implementation.

**Engineer decision:** Replaced entity mutation with an atomic SQL increment. This is a correctness improvement, not merely a performance optimization.

## Task 4 — validation and error handling
**AI use:** Suggested validation and exception-handler patterns.

**Engineer changes:**
- Removed regex-only URL validation as the source of truth.
- Added URI parsing and HTTP/HTTPS scheme/host checks.
- Added structured 400/404/500 responses.
- Avoided returning exception internals for unexpected failures.

## Task 5 — brownfield optimization
**Requirement:** Improve redirect performance.

**AI proposal:** Introduce Redis immediately.

**Engineer decision:** Deferred cache adoption until a baseline is measured. Redis is infrastructure-ready, but correctness currently depends only on PostgreSQL. A future cache-aside layer must include invalidation on delete and expiration handling.

## Task 6 — testing
**AI use:** Generated candidate unit test cases.

**Engineer-selected cases:**
- successful creation
- invalid URL
- expiration
- redirect
- atomic click increment
- expired URL
- unknown code
- repository interaction expectations

## AI safety controls
- No credentials, secrets, customer data, production URLs, or proprietary source code were supplied to AI tools.
- AI output was reviewed before inclusion.
- High-impact correctness/security changes require human approval.
- Generated code was subjected to build, test, static analysis and manual review gates in a real CI environment.
