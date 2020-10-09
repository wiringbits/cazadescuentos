import { Component, OnInit } from '@angular/core';
import { DEFAULT_LANG, LanguageService } from 'src/app/services/language.service';
import { SupportedStores } from '../../supported.stores';

import { detect } from 'detect-browser';
import { DiscountCompanyService } from 'src/app/services/discount-company.service';
const browser = detect();

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  language = DEFAULT_LANG;
  stores = SupportedStores;

  constructor(languageService: LanguageService, discountCompanyService: DiscountCompanyService) {
    this.language = languageService.getLang();
    this.discountCompany = discountCompanyService.getDiscountCompanies();
  }

  ngOnInit() {
    switch (browser && browser.name) {
      case 'firefox':
          this.isFirefox=true;
          this.urlExtention = 'https://addons.mozilla.org/firefox/addon/cazadescuentos/';
        break;
      case 'chrome':
          this.isFirefox=false;
          this.urlExtention ='https://chrome.google.com/webstore/detail/cazadescuentos/miadcmhlfknbjhlknpaidjnelinghpdf';
        break;      
      default:
        console.log('not supported');
    }
  }

  public download() {
    window.open(this.urlExtention, '_blank');
  }
}
