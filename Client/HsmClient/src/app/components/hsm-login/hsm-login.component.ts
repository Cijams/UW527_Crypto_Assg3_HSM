import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';


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

  ) { }

  ngOnInit() {
   this.loginForm = this.formBuilder.group({
     username: ['', Validators.required],
     password: ['', Validators.required]
   });

  }

  onClickMe() {
    console.log(this.loginForm.get('username').value);
  }

  
}
