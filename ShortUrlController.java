package com.schwab.urlshortener.controller;

import com.schwab.urlshortener.dto.AnalyticsResponse;
import com.schwab.urlshortener.dto.CreateShortUrlRequest;
import com.schwab.urlshortener.dto.ShortUrlResponse;
import com.schwab.urlshortener.service.ShortUrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/urls")
public class ShortUrlController {
    private final ShortUrlService service;

    public ShortUrlController(ShortUrlService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ShortUrlResponse> create(@Valid @RequestBody CreateShortUrlRequest request,
                                                    HttpServletRequest servletRequest) {
        String baseUrl = servletRequest.getScheme() + "://" + servletRequest.getServerName()
                + ((servletRequest.getServerPort() == 80 || servletRequest.getServerPort() == 443)
                ? "" : ":" + servletRequest.getServerPort());
        ShortUrlResponse response = service.create(request, baseUrl);
        return ResponseEntity.created(URI.create(response.shortUrl())).body(response);
    }

    @GetMapping("/{code}/stats")
    public AnalyticsResponse stats(@PathVariable String code) {
        return service.analytics(code);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        service.delete(code);
        return ResponseEntity.noContent().build();
    }
}
