# Security and Reliability Review

## Input validation
- Destination URL is required and capped at 2048 characters.
- Only absolute HTTP/HTTPS URLs are accepted.
- Expiration is bounded from 1 hour to 1 year.
- Short codes are constrained to seven Base62 characters at the redirect route.

## Redirect safety
The service redirects clients; it does **not** server-side fetch the destination URL. Therefore the URL-shortening flow itself does not create a server-side SSRF request. Destination URLs are still restricted to HTTP/HTTPS to avoid arbitrary schemes.

## Data integrity
- Short-code uniqueness is enforced at the database layer.
- Allocation retries are bounded.
- Redirect click count uses an atomic database update.
- Delete and create operations are transactional.

## Operational controls
- Spring Boot Actuator exposes health, metrics and operational information.
- Secrets are supplied through environment variables rather than source code.
- Production deployments should place the API behind an API gateway with authentication/authorization, rate limiting, TLS and centralized audit logging.

## Production hardening still required
- Replace `ddl-auto=update` with Flyway/Liquibase migrations.
- Add authentication/authorization if the service is customer-facing.
- Add rate limits and abuse controls.
- Add dependency/SCA scanning and SAST in CI.
- Add structured JSON logs with correlation IDs.
- Define analytics privacy/retention requirements before collecting IP/device data.
