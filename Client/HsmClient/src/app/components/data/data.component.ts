import { Component, OnInit } from '@angular/core';
import { ApiService } from 'src/app/api.service';

@Component({
  selector: 'app-data',
  templateUrl: './data.component.html',
  styleUrls: ['./data.component.css']
})
export class DataComponent implements OnInit {

  data;
  displayData;

  constructor(private apiService: ApiService) { }

  ngOnInit(): void {
    this.data = this.apiService.data.subscribe((info) => {
      this.displayData = info;
    }) ;
  }

}
