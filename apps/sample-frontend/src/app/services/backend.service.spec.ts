import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { BackendService } from './backend.service';
import { AppConfigService } from '../providers/app-config.service';

describe('BackendService', () => {
  let service: BackendService;
  let mockAppConfigService;

  beforeEach(() => {
    mockAppConfigService = {
      getConfig: () => { return { server: "http://127.0.0.1:8080" } }
    }

    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ],
      providers: [{
        provide: AppConfigService, useValue: mockAppConfigService
      }]
    });
    service = TestBed.inject(BackendService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
