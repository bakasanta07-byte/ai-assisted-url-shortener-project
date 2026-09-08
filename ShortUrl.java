package com.schwab.urlshortener.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="short_urls", indexes={@Index(name="idx_short_code", columnList="short_code", unique=true)})
public class ShortUrl {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;
  @Column(name="short_code", nullable=false, unique=true, length=12)
  private String shortCode;
  @Column(name="long_url", nullable=false, length=2048)
  private String longUrl;
  @Column(nullable=false) private Instant createdAt;
  private Instant expiresAt;
  @Column(nullable=false) private long clickCount;

  protected ShortUrl() {}
  public ShortUrl(String shortCode, String longUrl, Instant createdAt, Instant expiresAt) {
    this.shortCode=shortCode; this.longUrl=longUrl; this.createdAt=createdAt; this.expiresAt=expiresAt; this.clickCount=0;
  }
  public Long getId(){return id;} public String getShortCode(){return shortCode;} public String getLongUrl(){return longUrl;}
  public Instant getCreatedAt(){return createdAt;} public Instant getExpiresAt(){return expiresAt;} public long getClickCount(){return clickCount;}
  public void incrementClickCount(){clickCount++;}
}
