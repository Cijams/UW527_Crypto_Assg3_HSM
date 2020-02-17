import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  loginForm: FormGroup;
  
  constructor() { }

  ngOnInit(): void {
  }

  onClickMe() {
    console.log(this.loginForm.get('username').value);
  }

}
