import { Component, OnInit } from '@angular/core';
import { DEFAULT_LANG, LanguageService } from 'src/app/services/language.service';
import { SupportedStores } from '../../supported.stores';

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
  }

  public getDiscountshunter(navigator: string) {
    switch(navigator){
      case 'Chrome':
        window.open('https://chrome.google.com/webstore/detail/cazadescuentos/miadcmhlfknbjhlknpaidjnelinghpdf', '_blank');
        break;
      case 'Firefox':
        window.open('https://addons.mozilla.org/firefox/addon/cazadescuentos/', '_blank');
        break;
      case 'Android':
        window.open('https://play.google.com/store/apps/details?id=net.cazadescuentos.app', '_blank')
        break;  
      default:
        return;
    }
  }
}
