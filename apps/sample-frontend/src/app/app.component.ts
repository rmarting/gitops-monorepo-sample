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

  constructor(private backendService: BackendService) {
    console.log('AppComponent constructor');
  }

  getUser() {
    console.log('getUser');
    this.backendService.getUser().subscribe(
      (data) => this.response = data
    );
  }

  getUserGreeting() {
    console.log('getUserGreeting');
    return {
      name: 'John Doe',
      email: 'john@example.org'
    }
  }  

  postUserGreeting() {
    console.log('postUserGreeting');
    this.backendService.postUserGreeting("Roman").subscribe(
      (data) => this.response = data
    );
  }

  getPostUserFarewell() {
    console.log('getPostUserFarewell');
    return {
      name: 'John Doe',
      email: 'john@example.org'
    }
  }  

}
