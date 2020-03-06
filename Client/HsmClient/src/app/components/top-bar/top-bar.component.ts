import { Component, OnInit } from '@angular/core';
import { ApiService } from 'src/app/api.service';

@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css']
})
export class TopBarComponent implements OnInit {
  loggedInUser;
  constructor(private apiService: ApiService) { }

  ngOnInit(): void {
    this.subscribeToUserName();
  }
  subscribeToUserName() {
    this.apiService.displayUser.subscribe((userName) => {
      this.loggedInUser = userName;
    });
  }

  onHomeButton() {
    this.apiService.updateDisplayedData(null);
    this. loggedInUser = null;
  }

}
