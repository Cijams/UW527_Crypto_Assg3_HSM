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
  loading = false;
  submitted = false;
  returnUrl: string;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,

  ) { }

  ngOnInit() {
   this.loginForm = this.formBuilder.group({
     userID: [''],
     password: ['']
   });
  }

  onClickMe() {
    console.log(this.loginForm.get('userID').value);
    console.log(this.loginForm.get('password').value);
  }

  public onLoginUser() {
    const data = 'This is from angular';
    const url = 'http://localhost:8080/loginUser';
    this.http.get<string>(url,
      {
        params: new HttpParams().set('userID', this.loginForm.get('userID').value).append('password', this.loginForm.get('password').value)
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

