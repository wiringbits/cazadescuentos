import { Component, OnInit } from '@angular/core';
import { DEFAULT_LANG, LanguageService } from 'src/app/services/language.service';
import { SupportedStores } from '../../supported.stores';

import { detect } from 'detect-browser';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  language = DEFAULT_LANG;
  stores = SupportedStores;

  constructor(languageService: LanguageService) {
    this.language = languageService.getLang();
  }

  ngOnInit() {
    const browser = detect();

    if (!this.handleMobile()) {
      // it is useless to suggest the extension on mobile
      const isComputer = browser && (
        browser.os.indexOf('Windows') >= 0 ||
        browser.os.indexOf('Linux') >= 0 ||
        browser.os.indexOf('Mac OS') >= 0
      );

      switch (isComputer && browser.name) {
        case 'firefox':
          this.openBuenFinApp();
          break;
        case 'chrome':
          this.openBuenFinApp();
          break;
        default:
          this.openBuenFinApp();
          break;
      }
    }
  }

  private handleMobile(): boolean {
    const browser = detect();
    switch (browser && browser.os) {
      case 'Android OS':
        this.openBuenFinApp();
        return true;

      case 'iOS':
        this.openBuenFinApp();
        return true;

      default:
        return false;
    }
  }

  public getDiscountshunter(navigator: string) {
    switch (navigator) {
      case 'Chrome':
        this.openChromeApp();
        break;
      case 'Firefox':
        this.openFirefoxApp();
        break;
      case 'Android':
        this.openAndroidApp();
        break;
      default:
        return;
    }
  }

  private openBuenFinApp() {
    window.location.href = 'https://app.cazadescuentos.net/buenfin';
  }

  private openAndroidApp() {
    window.location.href = 'https://play.google.com/store/apps/details?id=net.cazadescuentos.app';
  }

  private openPWA() {
    window.location.href = 'https://app.cazadescuentos.net/guia?utm_source=landing-page';
  }

  private openChromeApp() {
    window.location.href = 'https://chrome.google.com/webstore/detail/cazadescuentos/miadcmhlfknbjhlknpaidjnelinghpdf';
  }

  private openFirefoxApp() {
    window.location.href = 'https://addons.mozilla.org/firefox/addon/cazadescuentos/';
  }
}
