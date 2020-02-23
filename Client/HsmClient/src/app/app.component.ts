import { Component, OnInit } from '@angular/core';
import { ApiService } from './api.service';
import { HttpClient } from '@angular/common/http';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  smartphone: any = [];

  constructor(private api: ApiService, private http: HttpClient) {}

  title = 'HsmClient';

}
