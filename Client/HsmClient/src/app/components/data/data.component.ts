import { Component, OnInit } from '@angular/core';
import { ApiService } from 'src/app/api.service';

@Component({
  selector: 'app-data',
  templateUrl: './data.component.html',
  styleUrls: ['./data.component.css']
})
export class DataComponent implements OnInit {

  data;
  functionName;

  constructor(private apiService: ApiService) { }

  ngOnInit(): void {
    this.subscribeToData();
    this.subscribeToCalledFunction();
  }

  subscribeToData() {
    this.apiService.data.subscribe((data) => {
      this.data = data;
    });
  }

  subscribeToCalledFunction() {
    this.apiService.lastFunctionCalled.subscribe((functionName) => {
      this.functionName = functionName;
    });
  }

}
