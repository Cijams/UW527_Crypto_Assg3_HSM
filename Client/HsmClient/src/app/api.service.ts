import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

const localUrl = 'http://localhost:8080/hello-world';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  registeredUser;

  constructor(private http: HttpClient) { }

  getRegisteredUser() {

  }

  setRegisteredUser() {

  }

}
