package com.schwab.urlshortener.controller;
import com.schwab.urlshortener.entity.ShortUrl; import com.schwab.urlshortener.service.ShortUrlService; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController public class RedirectController { private final ShortUrlService service; public RedirectController(ShortUrlService service){this.service=service;}
 @GetMapping("/{code:[A-Za-z0-9]{7}}") public ResponseEntity<Void> redirect(@PathVariable String code){ShortUrl s=service.resolve(code);return ResponseEntity.status(HttpStatus.FOUND).location(java.net.URI.create(s.getLongUrl())).build();}
}
