import { Injectable } from '@angular/core';

@Injectable()
export class DiscountCompanyService {

  constructor() { }

  getDiscountCompanies():any[]{
    return [ 
      {
        name : "zara",
        linkHref:"https://www.zara.com",
        imgAlt:"Zara",
        imgSrc: "../../../assets/pages/logo-zara.png",
        imgSizes: "150px"
      },
      {
        name : "liverpool",
        linkHref:"https://www.liverpool.com.mx/",
        imgAlt:"Liverpool",
        imgSrc: "../../../assets/pages/logo-liverpool.png" ,
        imgSizes: "200px"
      },
      {
        name : "h&m",
        linkHref:"https://www2.hm.com",
        imgAlt:"H&M",
        imgSrc: "../../../assets/pages/logo-hm.png",
        imgSizes: "150px" 
      },
      {
        name : "coppel",
        linkHref:"https://www.coppel.com/",
        imgAlt:"Coppel",
        imgSrc: "./../../assets/pages/logo-coppel.png",
        imgSizes: "160px" 
      },
      {
        name : "ebay",
        linkHref:"https://www.ebay.com/",
        imgAlt:"Ebay",
        imgSrc: "../../../assets/pages/logo-ebay.png",
        imgSizes: "160px" 
      },
      {
        name : "officeDepotMx",
        linkHref:"https://www.officedepot.com.mx/",
        imgAlt:"OfficeDepotMx",
        imgSrc: "../../../assets/pages/logo-officeDepotMx.png",
        imgSizes: "160px" 
      },
      {
        name : "officeDepotUs",
        linkHref:"https://www.officedepot.com/",
        imgAlt:"OfficeDepotUs",
        imgSrc: "../../../assets/pages/logo-officeDepotUs.png",
        imgSizes: "160px" 
      },   
      {
        name : "sanborns",
        linkHref:"https://www.sanborns.com.mx/",
        imgAlt:"Sanborns",
        imgSrc: "../../../assets/pages/logo-sanborns.png",
        imgSizes: "160px" 
      },          
      {
        name : "samsclub",
        linkHref:"https://www.sams.com.mx/",
        imgAlt:"Samsclub",
        imgSrc: "../../../assets/pages/logo-samsClub.png",
        imgSizes: "160px" 
      },
      {
        name : "samsclub",
        linkHref:"https://www.samsclub.com/",
        imgAlt:"Samsclub",
        imgSrc: "../../../assets/pages/logo-samsClubUs.png",
        imgSizes: "160px" 
      },      
      {
        name : "mercadoLibre",
        linkHref:"https://www.mercadolibre.com.mx/",
        imgAlt:"MercadoLibre",
        imgSrc: "../../../assets/pages/logo-mercadolibre.png",
        imgSizes: "160px" 
      }, 
      {
        name : "nordstrom",
        linkHref:"https://shop.nordstrom.com/",
        imgAlt:"Nordstrom",
        imgSrc: "../../../assets/pages/logo-nordstrom.png",
        imgSizes: "160px" 
      }, 
      {
        name : "elektra",
        linkHref:"https://www.elektra.com.mx/",
        imgAlt:"Elektra",
        imgSrc: "../../../assets/pages/logo-elektra.png",
        imgSizes: "160px" 
      }, 
      {
        name : "costcoMx",
        linkHref:"https://www.costco.com.mx/",
        imgAlt:"CostcoMx",
        imgSrc: "../../../assets/pages/logo-costcoMx.png",
        imgSizes: "160px" 
      }, 
      {
        name : "homeDepot",
        linkHref:"https://www.homedepot.com.mx/",
        imgAlt:"HomeDepot",
        imgSrc: "../../../assets/pages/logo-homeDepotMx.png",
        imgSizes: "160px" 
      }, 
      {
        name : "walmartMx",
        linkHref:"https://www.walmart.com.mx/",
        imgAlt:"WalmartMx",
        imgSrc: "../../../assets/pages/logo-walmartMx.png",
        imgSizes: "160px" 
      }, 
    ];
  }
}
