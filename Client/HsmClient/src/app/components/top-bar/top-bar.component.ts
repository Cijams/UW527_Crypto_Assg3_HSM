import { Component, OnInit } from '@angular/core';
import { ApiService } from 'src/app/api.service';

@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css']
})
export class TopBarComponent implements OnInit {
  loggedInUser;
  constructor(private apiservice: ApiService) { }

  ngOnInit(): void {
    this.subscribeToUserName();
  }
  subscribeToUserName() {
    this.apiservice.displayUser.subscribe((userName) => {
      this.loggedInUser = userName;
    });
  }

}
