# Interview Demo Script

## 1. Start with the engineering problem
"The requirement is a URL shortener with core APIs, analytics and reliability. I intentionally implemented a modular monolith because it is the smallest architecture that demonstrates production engineering without adding distributed-system overhead."

## 2. Show the architecture
Open `docs/ARCHITECTURE.md` and explain the create and redirect flows.

## 3. Run the happy path
1. Start PostgreSQL/Redis with Docker Compose.
2. Start Spring Boot.
3. Open Swagger.
4. Create a URL.
5. Follow the returned short URL.
6. Query analytics and show the click count.
7. Delete the URL and verify a subsequent request returns 404.

## 4. Demonstrate engineering judgment
Show the service implementation and explain:
- database uniqueness is the final collision guard;
- allocation retries are bounded;
- click counting uses an atomic SQL update;
- expired links are treated as unavailable.

## 5. Demonstrate AI proficiency
Open `docs/AI_EXECUTION_LOG.md`. Explain one concrete example where AI suggested an approach and you changed or rejected it after considering correctness, concurrency or operational complexity.

## 6. Discuss ambiguity
Use the analytics requirement as the ambiguous scenario. Explain the unanswered questions and why aggregate click count was selected for the MVP.

## 7. Close with limitations
Explicitly call out migration management, authentication, rate limiting, high-volume analytics and Redis cache invalidation as production-hardening work rather than pretending the interview prototype already solves them.
