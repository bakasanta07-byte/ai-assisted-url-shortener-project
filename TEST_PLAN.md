# Test Plan

## Functional
- Create valid URL.
- Reject blank URL.
- Reject non-http/https schemes.
- Reject URL without host.
- Enforce maximum URL length.
- Accept optional expiration.
- Reject expiration outside supported bounds.
- Resolve existing short code.
- Increment click count.
- Reject missing code.
- Reject expired code.
- Delete existing code.

## Integration
Use Testcontainers PostgreSQL to verify JPA mappings, unique constraints, transactions and controller serialization.

## Performance
For a production environment, run a representative redirect workload and measure p50/p95/p99 latency, throughput, database CPU/connections, cache hit rate, and error rate.

## Security
- Validate and bound input.
- Never execute or fetch the submitted URL server-side.
- Do not log full URLs if URLs can contain sensitive query parameters.
- Add authentication/rate limiting at the gateway if exposed to untrusted users.
