import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppConfigService } from '../providers/app-config.service';

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  BASE_URL = '';

  constructor(private http: HttpClient, private appConfigService: AppConfigService) {
    console.log('BackendService constructor');
    console.log(this.appConfigService.getConfig());

    this.BASE_URL = this.appConfigService.getConfig().server;
  }

  getUser() {
    return this.http.get(`${this.BASE_URL}/user`);
  }

  getUserGreeting() {
    return this.http.get(`${this.BASE_URL}/user/greeting/Front`);
  }

  postUserGreeting(name: string) {
    return this.http.post(
      `${this.BASE_URL}/user/greeting`,
      {
        name: name
      });
  }

  postUserFarewell(name: string) {
    return this.http.post(
      `${this.BASE_URL}/user/farewell`,
      {
        name: name
      });
  }

}
