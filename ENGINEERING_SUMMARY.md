# Final Engineering Summary

## 1. Requirement understanding
The assignment asks for a runnable URL shortener plus evidence of requirement interpretation, task decomposition, AI-assisted execution, validation, and human ownership. The implementation therefore focuses on a small but defensible production-style vertical slice rather than maximizing feature count.

### Explicit assumptions
- A shortened URL is identified by a seven-character Base62 code.
- A URL must use HTTP or HTTPS.
- Expiration is optional.
- Analytics means aggregate click count for the MVP.
- The prototype does not require authentication because it was not specified.

## 2. Task decomposition
1. Establish project structure and build pipeline.
2. Define URL entity and persistence constraints.
3. Implement URL creation and validation.
4. Implement redirect resolution and expiration.
5. Add click analytics.
6. Add deletion and structured error handling.
7. Add unit tests.
8. Add operational endpoints and OpenAPI.
9. Add Angular UI.
10. Add Docker-based local infrastructure.
11. Review security, reliability, scalability and trade-offs.

## 3. Greenfield scenario
**Requirement:** Build URL creation and redirect functionality from scratch.

**Acceptance criteria:**
- Valid URL returns 201.
- Invalid URL returns 400.
- Short code is unique.
- Redirect returns 302 to the original URL.
- Expired links are not redirected.
- Tests pass.

**AI-assisted execution:** AI can generate initial DTO/controller/service/test scaffolding; the engineer reviews the design, changes validation, verifies persistence behavior, and runs tests before accepting the output.

## 4. Brownfield scenario
**Change:** Add caching for high-frequency redirects.

**Impacted areas:** RedirectController, ShortUrlService, cache abstraction, invalidation path, metrics and tests.

**Decision:** Introduce a cache-aside abstraction only after measuring database latency and hit rate. Cache reads should not become a correctness dependency. Deletes and expirations must invalidate cached entries.

**Validation:** Compare p95 redirect latency and database load before/after the change, plus regression tests for cache misses, hits, expiration and deletion.

## 5. Ambiguous scenario
**Requirement:** “Provide analytics for shortened URLs.”

Questions to clarify:
- Click count or unique visitors?
- Which dimensions: time, country, device, referrer?
- Real-time or delayed?
- How long should analytics be retained?
- Are IP addresses considered personal data for this deployment?

**MVP assumption:** aggregate click count only. This avoids collecting unnecessary personal data while providing useful functionality. A future event model could asynchronously capture analytics if the business confirms the requirements.

## 6. AI usage and traceability
AI was treated as an accelerator inside engineer-owned tasks.

| Activity | AI assistance | Engineer action |
|---|---|---|
| API scaffolding | Generate DTO/controller/service ideas | Reviewed and adapted API contract |
| Validation | Suggest URL validation | Added explicit HTTP/HTTPS and host checks |
| Tests | Generate candidate unit tests | Selected meaningful cases and verified behavior |
| Documentation | Draft architecture/readme sections | Reviewed assumptions and limitations |
| Review | Identify failure scenarios | Accepted relevant findings; rejected unnecessary complexity |

**Rejected/modified patterns:** direct entity exposure, unbounded inputs, trusting generated validation without tests, and adding distributed-system complexity before measuring a need.

## 7. Quality gates
- Compile/build succeeds.
- Unit tests cover core service behavior.
- Input validation is enforced at API boundary and service layer.
- Database uniqueness is enforced.
- Error responses are consistent.
- Security review checks URL schemes and input bounds.
- Operational health endpoint is available.
- Production migration strategy is documented as a limitation.

## 8. Risks and trade-offs
- Random codes can collide; database uniqueness plus retry bounds the risk.
- Aggregate click counts are not a full analytics platform.
- Cache can improve latency but introduces invalidation complexity.
- PostgreSQL is a strong prototype choice but high-scale deployments may require partitioning/event-driven analytics.
- A modular monolith is intentionally preferred over microservices for a small interview prototype.

## 9. Engineer ownership statement
AI-generated code is not considered correct by default. The engineer owns correctness, security, maintainability, testing, and production readiness. Every AI suggestion is reviewed against requirements and quality gates before acceptance.
