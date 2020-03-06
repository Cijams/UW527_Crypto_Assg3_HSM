import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ApiService } from 'src/app/api.service';


@Component({
  selector: 'app-hsm-login',
  templateUrl: './hsm-login.component.html',
  styleUrls: ['./hsm-login.component.css']
})
export class HsmLoginComponent implements OnInit {
  loginForm: FormGroup;
  location: Location;
  isHidden = true;
  constructor(
    private formBuilder: FormBuilder,
    private http: HttpClient,
    private apiService: ApiService,
    private route: Router
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

        console.log(res);

        const registrationStatus = Object.values(res)[0];
        const incomingUserID = Object.keys(res)[0].toString();

        if (registrationStatus === 'true') {
          this.apiService.setRegisteredUser(incomingUserID);
          this.apiService.updateDisplayedUser(this.loginForm.get('userID').value);
          this.getKeyIDs();
        } else {
          console.log('Failed to register user');
          this.isHidden = false;
        }
      },
    );
  }

  public getKeyIDs() {
    const url = 'http://localhost:8080/getKeyIDs';
    this.http.get(url,
      {
        params: new HttpParams().set('userID', this.apiService.registeredUser)
      },
    ).subscribe(
      (res: Response) => {
        this.apiService.currentUserKeyIDs = Object.values(res);
        this.route.navigate(['/crypto']);
      },
    );
  }

}

