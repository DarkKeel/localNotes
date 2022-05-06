import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {AuthenticateService} from "../../service/authenticate.service";
import {AuthRequest} from "../../model/AuthRequest";
import {AppComponent} from "../../app.component";
import {UserReg} from "../../model/UserReg";

@Component({
  selector: 'app-login',
  templateUrl: './loginpage.component.html',
  styleUrls: ['./loginpage.component.css']
})
export class LoginpageComponent implements OnInit {
  errorMessage: any;
  isError = false;

  constructor(private router:Router, private auth:AuthenticateService,
              private app: AppComponent) { }

  ngOnInit() {
  }

  loginUser(event: any): void {
    event.preventDefault();
    const target = event.target;
    const username = target.querySelector('#username').value;
    const password = target.querySelector('#password').value;

    this.auth.getToken(new AuthRequest(username, password)).subscribe(data => {
      localStorage.setItem("id", data.id);
      localStorage.setItem("token", data.token);
      this.app.setIsLogged(true);
      this.app.updateInfo(data.id);
      this.router.navigate(['']);
    }, (error: HttpErrorResponse) => {
      this.errorMessage = error.status;
      if (error.status == 401 || error.status == 403) {
        this.errorMessage = 'Incorrect login and/or password'
      } else {
        this.errorMessage = 'Something went wrong.'
      }
      this.isError = true;
    });
  }

  registerNewUser(value: any) {
    let newUser: UserReg = new UserReg(value.regUsername, value.regFirstname,
      value.regLastname, value.regEmail, value.regPassword);
    this.auth.createUser(newUser).subscribe(data => {
      let username = data.username;
      let password = newUser.password;
      this.auth.getToken(new AuthRequest(username, password)).subscribe(data => {
        localStorage.setItem("id", data.id);
        localStorage.setItem("token", data.token);
        this.app.setIsLogged(true);
        this.app.updateInfo(data.id);
        this.router.navigate(['']);
      }, (error: HttpErrorResponse) => {
        this.errorMessage = error.status;
        if (error.status == 401 || error.status == 403) {
          this.errorMessage = 'Incorrect login and/or password'
        } else {
          this.errorMessage = 'Something went wrong.'
        }
        this.isError = true;
      });
    })
  }
}
