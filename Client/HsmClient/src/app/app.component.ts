import { Component, OnInit } from '@angular/core';

import { Router } from '@angular/router';
import { ApiService } from './api.service';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(private router: Router,
              private apiService: ApiService) { }

  dataExists;
  smartphone: any = [];

  title = 'HsmClient';

  // tslint:disable-next-line: use-lifecycle-interface
  ngOnInit() {
    this.router.navigate(['']);
    this.subscribeToDataExists();
    this.dataExists = null;
  }

  subscribeToDataExists() {
    this.apiService.displayData.subscribe((isData) => {
      this.dataExists = isData;
    });
  }

}
