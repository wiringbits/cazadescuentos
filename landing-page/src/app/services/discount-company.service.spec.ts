import { TestBed } from '@angular/core/testing';

import { DiscountCompanyService } from './discount-company.service';

describe('DiscountCompanyService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DiscountCompanyService = TestBed.get(DiscountCompanyService);
    expect(service).toBeTruthy();
  });
});
