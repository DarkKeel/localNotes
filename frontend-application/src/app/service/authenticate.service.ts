import { Injectable } from '@angular/core';
import {AuthRequest} from "../model/AuthRequest";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {AuthResponse} from "../model/AuthResponse";

@Injectable({
  providedIn: 'root'
})
export class AuthenticateService {

  constructor(public http: HttpClient) { }

  getToken(user: AuthRequest) {
    return this.http.post<AuthResponse>(environment.API_URL + "/api/v1/auth/login", user);
  }
}
