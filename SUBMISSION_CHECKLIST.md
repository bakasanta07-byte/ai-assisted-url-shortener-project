# Final Submission Checklist

## Prototype
- [x] Java 17 / Spring Boot REST API
- [x] URL creation
- [x] Redirect endpoint
- [x] Aggregate click analytics
- [x] Delete endpoint
- [x] Optional expiration
- [x] PostgreSQL persistence
- [x] Database uniqueness constraint
- [x] Atomic click counting
- [x] Structured validation/error responses
- [x] Actuator health/metrics
- [x] OpenAPI / Swagger
- [x] Angular UI
- [x] Docker Compose local dependencies

## Assignment evidence
- [x] Requirement understanding and assumptions
- [x] Task decomposition
- [x] Architecture and control flow
- [x] Greenfield scenario
- [x] Brownfield scenario
- [x] Ambiguous requirement scenario
- [x] AI-assisted execution log
- [x] AI safety / human approval controls
- [x] Testing strategy
- [x] Security review
- [x] Risks, limitations and trade-offs
- [x] Demo script

## Before client submission
- [ ] Run `docker compose up -d postgres redis`
- [ ] Run `cd backend && mvn clean verify`
- [ ] Confirm Testcontainers integration test passes on a Docker-enabled machine
- [ ] Run `cd frontend && npm ci && npm run build`
- [ ] Start backend and verify Swagger + Actuator
- [ ] Start Angular and exercise create / redirect / analytics / delete
- [ ] Review generated AI transcript/log and remove any environment-specific details
- [ ] Commit the final source and test results to version control
