import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import { LanguageService } from './services/language.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  constructor(translate: TranslateService, languageService: LanguageService) {
    translate.setDefaultLang(languageService.getLang());

    translate.setTranslation('en', {
      appName: 'Cazadescuentos',
      pricesChangePeopleOverpayText: `Prices change.<br>
People overpay.<br>
With Cazadescuentos, you won't.`,
      useOurAppOnTheAvailableStores: `Use Cazadescuentos to watch items on
BestBuy, Ebay, Amazon Mx, Liverpool, Zara, Coppel, H&M and more.
We’ll send a notification the instant there’s a better price.`,
      findUsIn: `Find us in`,
      getOnChromeLink: 'Get on Chrome',
      getOnFirefoxLink: 'Get on Firefox (recommended)',
      getOnAndroidLink: 'Get on Android',
      blueInstructionsStep1: '1. Find which product you are interested in buying sites',
      youWillNeedTheAppInstalled: `You'll need Cazadescuentos installed`,
      blueInstructionsStep2: `2. Cazadescuentos will ask you if you want <br>to add the product, so you click in ok`,
      blueInstructionsStep3: `3. Sit back and relax—we’ll send a notification<br>the instant there’s a deal.`,
      availableOn: 'Now available on',
      moreStoresSoon: 'More stores coming soon!',
      getPricedropsWithTheApp: 'Get price drops with Cazadescuentos'
    });

    translate.setTranslation('es', {
      appName: 'Caza Descuentos',
      pricesChangePeopleOverpayText: `Los precios cambian.<br>
Las personas pagan de más.<br>
Con Caza Descuentos, tú pagarás menos.`,
      useOurAppOnTheAvailableStores: `Usa Caza Descuentos para monitorear productos de tus tiendas favoritas.
Te notificaremos en el instante que los productos de tu interés bajen de precio.`,
      findUsIn: `Encuéntranos en`,
      getOnChromeLink: 'Usar en Chrome',
      getOnFirefoxLink: 'Usar en Firefox',
      getOnAndroidLink: 'Usar en Android',
      blueInstructionsStep1: '1. Busca el producto que deseas comprar en las tiendas',
      youWillNeedTheAppInstalled: `Necesitas tener instalada la app de Caza Descuentos`,
      blueInstructionsStep2: `2. Caza Descuentos te preguntara<br>si deseas monitorear el producto`,
      blueInstructionsStep3: `3. Relajate y te notificaremos<br>cuando el producto baje de precio.`,
      availableOn: 'Disponible para',
      moreStoresSoon: 'Más tiendas pronto!',
      getPricedropsWithTheApp: 'Paga menos con Caza Descuentos'
    });
  }
}
