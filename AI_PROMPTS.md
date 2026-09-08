# AI-Assisted Engineering Prompt Log

These are representative prompts to demonstrate disciplined AI use. The actual tool conversation should be retained by the candidate if requested by the client.

## Prompt 1 — API design
**Intent:** design the smallest REST contract for URL creation, redirect, analytics and deletion.

**Constraints:** Java 17, Spring Boot, PostgreSQL, validation, production-oriented error handling.

**Acceptance criteria:** endpoint list, request/response models, status codes, failure cases.

## Prompt 2 — implementation
Implement the URL creation service using the supplied DTO and repository contract. Do not expose JPA entities from the controller. Validate HTTP/HTTPS URLs, support optional expiration, generate a seven-character Base62 code, and preserve database uniqueness as the final collision guard. Explain any assumptions before code.

## Prompt 3 — test generation
Generate unit tests for valid URL creation, invalid schemes, missing URLs, expiration bounds, code collision retry, redirect resolution, expired links, and click-count increments. Do not invent external behavior not present in the requirement.

## Prompt 4 — review
Review the implementation for security, reliability, scalability and maintainability. Prioritize concrete defects over speculative micro-optimizations. For each finding, explain severity, evidence, and a minimal fix.

## Prompt 5 — brownfield change
Assume the current redirect path is experiencing high read volume. Propose a cache-aside design using Redis. Identify impacted modules, invalidation requirements, failure behavior when Redis is unavailable, metrics to collect, and tests. Do not introduce cache dependence for correctness.
