package com.schwab.urlshortener.service;

import com.schwab.urlshortener.dto.AnalyticsResponse;
import com.schwab.urlshortener.dto.CreateShortUrlRequest;
import com.schwab.urlshortener.dto.ShortUrlResponse;
import com.schwab.urlshortener.entity.ShortUrl;
import com.schwab.urlshortener.exception.NotFoundException;
import com.schwab.urlshortener.repository.ShortUrlRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ShortUrlService {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int CODE_LENGTH = 7;
    private static final int MAX_ALLOCATION_ATTEMPTS = 8;
    private final ShortUrlRepository repo;

    public ShortUrlService(ShortUrlRepository repo) {
        this.repo = repo;
    }

    public ShortUrlResponse create(CreateShortUrlRequest request, String baseUrl) {
        validateUrl(request.longUrl());

        Instant now = Instant.now();
        Instant expires = request.expirationHours() == null
                ? null
                : now.plusSeconds(request.expirationHours() * 3600L);

        for (int attempt = 1; attempt <= MAX_ALLOCATION_ATTEMPTS; attempt++) {
            String code = generateCode(CODE_LENGTH);
            try {
                ShortUrl saved = repo.saveAndFlush(new ShortUrl(code, request.longUrl(), now, expires));
                return toResponse(saved, baseUrl);
            } catch (DataIntegrityViolationException collision) {
                // The database unique constraint is the final collision guard. Retry with a new code.
                if (attempt == MAX_ALLOCATION_ATTEMPTS) {
                    throw new IllegalStateException("Unable to allocate a unique short code after retries", collision);
                }
            }
        }
        throw new IllegalStateException("Unable to allocate a unique short code");
    }

    @Transactional
    public ShortUrl resolve(String code) {
        ShortUrl shortUrl = repo.findByShortCode(code)
                .orElseThrow(() -> new NotFoundException("Short URL not found: " + code));

        if (shortUrl.getExpiresAt() != null && !shortUrl.getExpiresAt().isAfter(Instant.now())) {
            throw new NotFoundException("Short URL has expired: " + code);
        }

        // Atomic SQL increment prevents lost updates under concurrent redirects.
        repo.incrementClickCount(shortUrl.getId());
        return shortUrl;
    }

    @Transactional(readOnly = true)
    public AnalyticsResponse analytics(String code) {
        ShortUrl shortUrl = repo.findByShortCode(code)
                .orElseThrow(() -> new NotFoundException("Short URL not found: " + code));
        return new AnalyticsResponse(shortUrl.getShortCode(), shortUrl.getClickCount(),
                shortUrl.getCreatedAt(), shortUrl.getExpiresAt());
    }

    @Transactional
    public void delete(String code) {
        ShortUrl shortUrl = repo.findByShortCode(code)
                .orElseThrow(() -> new NotFoundException("Short URL not found: " + code));
        repo.delete(shortUrl);
    }

    private ShortUrlResponse toResponse(ShortUrl shortUrl, String baseUrl) {
        return new ShortUrlResponse(shortUrl.getShortCode(), baseUrl + "/" + shortUrl.getShortCode(),
                shortUrl.getLongUrl(), shortUrl.getCreatedAt(), shortUrl.getExpiresAt(), shortUrl.getClickCount());
    }

    private void validateUrl(String value) {
        try {
            URI uri = URI.create(value);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    || uri.getHost() == null) {
                throw new IllegalArgumentException("URL must be an absolute HTTP or HTTPS URL");
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("URL must be an absolute HTTP or HTTPS URL");
        }
    }

    private String generateCode(int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(ALPHABET.charAt(ThreadLocalRandom.current().nextInt(ALPHABET.length())));
        }
        return code.toString();
    }
}
