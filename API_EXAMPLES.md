# API Examples

## Create

```bash
curl -i -X POST http://localhost:8080/api/v1/urls \
  -H 'Content-Type: application/json' \
  -d '{"longUrl":"https://www.example.com/products/123","expirationHours":24}'
```

Expected response: `201 Created` with a JSON body containing `shortCode`, `shortUrl`, `longUrl`, timestamps and `clickCount`.

## Redirect

```bash
curl -i http://localhost:8080/Abc1234
```

Expected response: `302 Found` with a `Location` header.

## Analytics

```bash
curl -i http://localhost:8080/api/v1/urls/Abc1234/stats
```

## Delete

```bash
curl -i -X DELETE http://localhost:8080/api/v1/urls/Abc1234
```

Expected response: `204 No Content`.
