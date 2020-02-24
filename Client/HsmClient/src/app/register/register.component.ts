import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { HttpParams, HttpClient } from '@angular/common/http';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  loginForm: FormGroup;
  userID;
  password;

  constructor(private http: HttpClient, private apiService: ApiService,
              private formBuilder: FormBuilder) {
    this.userID = new FormControl('', [Validators.required]);
    this.loginForm = this.formBuilder.group({
      userID: this.userID
    });
  }

  ngOnInit(): void {
  }

  onRegisterUserTest() {
    console.log(this.userID);
    console.log(this.password);
  }

  onRegisterUser() {
    console.log(this.userID);
    console.log(this.password);
  }

}
