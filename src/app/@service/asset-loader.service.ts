import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AssetLoaderService {
  private loadedScripts = new Set<string>();
  private loadedStyles = new Set<string>();

  loadScript(src: string, id?: string): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.loadedScripts.has(src)) return resolve();

      const script = document.createElement('script');
      script.src = src;
      if (id) script.id = id;
      script.onload = () => {
        this.loadedScripts.add(src);
        resolve();
      };
      script.onerror = reject;
      document.body.appendChild(script);
    });
  }

  loadStyle(href: string): void {
    if (this.loadedStyles.has(href)) return;

    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = href;
    document.head.appendChild(link);
    this.loadedStyles.add(href);
  }

  loadAssets(js: string[], css: string[]): Promise<void> {
    css.forEach(href => this.loadStyle(href));
    return Promise.all(js.map(src => this.loadScript(src))).then(() => {});
  }
}
