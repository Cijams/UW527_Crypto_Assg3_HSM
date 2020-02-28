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
  location: Location
  constructor(
    private formBuilder: FormBuilder,
    private http: HttpClient
  ) { 
    this.location = location;
  }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      userID: [''],
      password: ['']
    });
  }

  public onLoginUser() {
    const url = 'http://localhost:8080/loginUser';
    this.http.get(url,
      {
        params: new HttpParams().set('userID', this.loginForm.get('userID').value)
          .append('password', this.loginForm.get('password').value)
      },
    ).subscribe(
      (res: Response) => {
        if (Object.values(res)[0]+"" === "200") {
          console.log("auth");
          this.location.assign("crypto"); // TODO dont reload page, use spa.
         }
         else {
           //this.location.assign("unauthorized");
           console.log("Unauthorized");
         }
      },
    );
  }

}

