import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { HttpClient, HttpParams } from '@angular/common/http';


@Component({
  selector: 'app-hsm-login',
  templateUrl: './hsm-login.component.html',
  styleUrls: ['./hsm-login.component.css']
})
export class HsmLoginComponent implements OnInit {
  loginForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private http: HttpClient
  ) { }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      userID: [''],
      password: ['']
    });
  }

  public onLoginUser() {
    const url = 'http://localhost:8080/loginUser';
    this.http.get<string>(url,
      {
        params: new HttpParams().set('userID', this.loginForm.get('userID').value)
          .append('password', this.loginForm.get('password').value)
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

