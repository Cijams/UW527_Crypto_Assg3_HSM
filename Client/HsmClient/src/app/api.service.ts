import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';

const localUrl = 'http://localhost:8080/hello-world';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  currentUserKeyIDs = [];

  // Displayed user.
  private displayedUser = new BehaviorSubject<string>('');
  displayUser = this.displayedUser.asObservable();

  // Actively registered session user.
  registeredUser;

  // Displayed raw data. Keys, ciphertext, hash.
  private dataSource = new BehaviorSubject<string>('');
  data = this.dataSource.asObservable();

  // Shows the datasource.
  private functionCalled = new BehaviorSubject<string>('');
  lastFunctionCalled = this.functionCalled.asObservable();

  // Toggles the display component on if there is data to show.
  private displayedData = new BehaviorSubject<string>('');
  displayData = this.displayedData.asObservable();

  constructor() { }

  updateDisplayedData(data: string) {
    this.displayedData.next(data);
  }

  updateLastFunctionCalled(functionName: string) {
    this.functionCalled.next(functionName);
  }

  updateDisplayedUser(userName: string) {
    this.displayedUser.next(userName);
  }

  updatedDataSelection(data: string) {
    this.dataSource.next(data);
  }

  getData() {
    return this.dataSource.getValue();
  }

  getRegisteredUser() {
    return this.registeredUser;
  }

  setRegisteredUser(registeredUser: string) {
    this.registeredUser = registeredUser;
  }

}
