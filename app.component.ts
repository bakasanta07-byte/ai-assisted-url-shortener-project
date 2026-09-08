import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface ShortUrlResponse {
  shortCode: string;
  shortUrl: string;
  longUrl: string;
  createdAt: string;
  expiresAt: string | null;
  clickCount: number;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html'
})
export class AppComponent {
  private readonly http = inject(HttpClient);

  url = '';
  hours: number | null = null;
  result: ShortUrlResponse | null = null;
  analytics: ShortUrlResponse | null = null;
  error = '';
  loading = false;

  create(): void {
    this.error = '';
    this.analytics = null;
    this.loading = true;

    this.http.post<ShortUrlResponse>('/api/v1/urls', {
      longUrl: this.url,
      expirationHours: this.hours
    }).subscribe({
      next: response => {
        this.result = response;
        this.loading = false;
      },
      error: error => {
        this.error = error.error?.message ?? 'Unable to create short URL.';
        this.loading = false;
      }
    });
  }

  stats(): void {
    if (!this.result) return;
    this.http.get<ShortUrlResponse>(`/api/v1/urls/${this.result.shortCode}/stats`)
      .subscribe({
        next: response => this.analytics = response,
        error: error => this.error = error.error?.message ?? 'Unable to load analytics.'
      });
  }

  delete(): void {
    if (!this.result) return;
    this.http.delete<void>(`/api/v1/urls/${this.result.shortCode}`).subscribe({
      next: () => {
        this.result = null;
        this.analytics = null;
        this.url = '';
      },
      error: error => this.error = error.error?.message ?? 'Unable to delete short URL.'
    });
  }
}
