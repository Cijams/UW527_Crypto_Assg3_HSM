import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';

const localUrl = 'http://localhost:8080/hello-world';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private displayedUser = new BehaviorSubject<string>('');
  displayUser = this.displayedUser.asObservable();


  registeredUser;

  private dataSource = new BehaviorSubject<string>('');
  data = this.dataSource.asObservable();

  constructor() { }

  updateDisplayedUser(userName: string) {
    this.displayedUser.next(userName);
  }

  updatedDataSelection(data: string) {
    this.dataSource.next(data);
  }

  getRegisteredUser() {
    return this.registeredUser;
  }

  setRegisteredUser(registeredUser: string) {
    this.registeredUser = registeredUser;
  }

}
