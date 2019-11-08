import { Component, OnInit } from '@angular/core';
import { DEFAULT_LANG, LanguageService } from 'src/app/services/language.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  language = DEFAULT_LANG;

  constructor(languageService: LanguageService) {
    this.language = languageService.getLang();
  }

  ngOnInit() {
  }

  public getDiscountshunter(navigator: string) {
    if (navigator === 'Chrome') {
      window.open('https://chrome.google.com/webstore/detail/cazadescuentos/miadcmhlfknbjhlknpaidjnelinghpdf', '_blank');
    } else { // Firefox
      window.open('https://addons.mozilla.org/firefox/addon/cazadescuentos/', '_blank');
    }
  }
}
