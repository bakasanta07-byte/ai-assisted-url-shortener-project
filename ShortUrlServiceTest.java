package com.schwab.urlshortener.service;

import com.schwab.urlshortener.dto.CreateShortUrlRequest;
import com.schwab.urlshortener.entity.ShortUrl;
import com.schwab.urlshortener.exception.NotFoundException;
import com.schwab.urlshortener.repository.ShortUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ShortUrlServiceTest {
    @Mock ShortUrlRepository repo;
    private ShortUrlService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ShortUrlService(repo);
    }

    @Test
    void createsShortUrl() {
        when(repo.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(
                new CreateShortUrlRequest("https://example.com/a", null),
                "http://localhost:8080");

        assertEquals("https://example.com/a", response.longUrl());
        assertEquals("http://localhost:8080/" + response.shortCode(), response.shortUrl());
        verify(repo).saveAndFlush(any(ShortUrl.class));
    }

    @Test
    void rejectsInvalidUrlBeforePersistence() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(new CreateShortUrlRequest("javascript:alert(1)", null), "http://localhost:8080"));
        verifyNoInteractions(repo);
    }

    @Test
    void createsExpirationFromRequestedHours() {
        when(repo.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(
                new CreateShortUrlRequest("https://example.com", 24),
                "http://localhost:8080");

        assertNotNull(response.expiresAt());
        assertTrue(response.expiresAt().isAfter(response.createdAt()));
    }

    @Test
    void resolvesAndUsesAtomicClickIncrement() {
        ShortUrl shortUrl = new ShortUrl("Abc1234", "https://example.com", Instant.now(), null);
        when(repo.findByShortCode("Abc1234")).thenReturn(Optional.of(shortUrl));
        when(repo.incrementClickCount(shortUrl.getId())).thenReturn(1);

        assertEquals("https://example.com", service.resolve("Abc1234").getLongUrl());
        verify(repo).incrementClickCount(shortUrl.getId());
    }

    @Test
    void rejectsExpiredUrl() {
        ShortUrl expired = new ShortUrl("Abc1234", "https://example.com", Instant.now().minusSeconds(3600),
                Instant.now().minusSeconds(1));
        when(repo.findByShortCode("Abc1234")).thenReturn(Optional.of(expired));

        assertThrows(NotFoundException.class, () -> service.resolve("Abc1234"));
        verify(repo, never()).incrementClickCount(any());
    }

    @Test
    void returnsNotFoundForUnknownCode() {
        when(repo.findByShortCode(anyString())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.analytics("missing"));
    }
}
