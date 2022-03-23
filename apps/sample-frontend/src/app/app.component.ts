import { Component } from '@angular/core';
import { BackendService } from './services/backend.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'sample-frontend';

  response = {}
  message = ''
  environment = ''

  constructor(private backendService: BackendService) {
    console.log('AppComponent constructor');
  }

  getUser() {
    console.log('getUser');
    this.backendService.getUser().subscribe(
      (data) => {
        this.response = data;
        this.message = JSON.parse(JSON.stringify(data)).message;
        this.environment = JSON.parse(JSON.stringify(data)).environment;
      }
    );
  }

  getUserGreeting() {
    console.log('getUserGreeting');
    this.backendService.getUserGreeting().subscribe(
      (data) => {
        this.response = data;
        this.message = JSON.parse(JSON.stringify(data)).message;
        this.environment = JSON.parse(JSON.stringify(data)).environment;
      }
    );
  }  

  postUserGreeting() {
    console.log('postUserGreeting');
    this.backendService.postUserGreeting("User Front").subscribe(
      (data) => {
        this.response = data;
        this.message = JSON.parse(JSON.stringify(data)).message;
        this.environment = JSON.parse(JSON.stringify(data)).environment;
      }
    );
  }

  postUserFarewell() {
    console.log('getPostUserFarewell');
    this.backendService.postUserFarewell("User Front").subscribe(
      (data) => {
        this.response = data;
        this.message = JSON.parse(JSON.stringify(data)).message;
        this.environment = JSON.parse(JSON.stringify(data)).environment;
      }
    );
  }  

}
