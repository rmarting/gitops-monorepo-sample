import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  //BASE_URL = "https://sample-app-gitops-monorepo-dev.apps.labs.sandbox893.opentlc.com";
  BASE_URL = "http://localhost:8080";

  constructor(private http: HttpClient) {

  }

  getUser() {
    return this.http.get(`${this.BASE_URL}/user`);
  }

  postUserGreeting(name: string) {
    return this.http.post(
      `${this.BASE_URL}/user/greeting`,
      {
        name: name
      });
  }

}
