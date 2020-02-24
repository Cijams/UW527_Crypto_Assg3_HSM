import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { HttpParams, HttpClient } from '@angular/common/http';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  loginForm: FormGroup;

  constructor(private http: HttpClient, private apiService: ApiService) { }




  ngOnInit(): void {
  }

  public onRegisterUser() {
    let userID = 'Josh1800';
    let password = 'secret';

    const data = 'This is from angular';
    const url = 'http://localhost:8080/registerUser';
    this.http.get<string>(url,
      {
        params: new HttpParams().set('userID', userID).append('password', password)
      },
      ).subscribe(
        res => {
          const returnKeys = Object.keys(res);
          const returnValues = Object.values(res);
          console.log(returnKeys);
          console.log(returnValues);
        },
      );
  }

}
