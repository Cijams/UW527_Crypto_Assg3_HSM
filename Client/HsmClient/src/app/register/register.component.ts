import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { HttpParams, HttpClient } from '@angular/common/http';
import { ApiService } from '../api.service';
import { Router, } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  location: Location;

  authStatus = '/register';
  isHidden = true;
  constructor(
    private formBuilder: FormBuilder,
    private http: HttpClient,
    private apiService: ApiService,
    private snackBar: MatSnackBar,
    private route: Router,

  ) {
    this.location = location;
  }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      userID: [''],
      password: ['']
    });
  }

  public onRegisterUser() {
    const url = 'http://localhost:8080/registerUser';
    this.http.get<string>(url,
      {
        params: new HttpParams().set('userID', this.registerForm.get('userID').value)
          .append('password', this.registerForm.get('password').value)
      },
    ).subscribe(
      res => {
        const registrationStatus = Object.values(res)[0];
        const incomingUserID = Object.keys(res)[0].toString();

        if (registrationStatus) {
          this.apiService.setRegisteredUser(incomingUserID);
        } else {
          console.log('Failed to register user');
        }
        const returnValues = Object.values(res);

        if (!!returnValues[0]) {
          this.openSnackBar('Registration Successful.', 'OK');
          this.route.navigate(['/login']);

        } else {
          this.isHidden = false;
        }
      },
    );
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 2500,
      verticalPosition: 'top',
      horizontalPosition: 'right',
    });
  }
}
