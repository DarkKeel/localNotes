import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {User} from "../model/User";
import {Category} from "../model/Category";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  constructor(public http: HttpClient) { }

  getCategories(id: string) {
    const headers = new HttpHeaders({Authorization: 'Bearer_' + localStorage.getItem("token")});
    return this.http.get<Category[]>(environment.API_URL + "/api/v1/category/" + id, {headers});
  }

  createCategory(category: Category) {
    const headers = new HttpHeaders({Authorization: 'Bearer_' + localStorage.getItem("token")});
    return this.http.post<Category[]>(environment.API_URL + "/api/v1/category/" + localStorage.getItem("id"), category, {headers});
  }

  deleteCategory(id: string) {
    const headers = new HttpHeaders({Authorization: 'Bearer_' + localStorage.getItem("token")});
    return this.http.delete<Category[]>(environment.API_URL + "/api/v1/category/" + localStorage.getItem("id") + "/" + id, {headers});
  }

  updateCategory(category: Category) {
    const headers = new HttpHeaders({Authorization: 'Bearer_' + localStorage.getItem("token")});
    return this.http.put<Category[]>(environment.API_URL + "/api/v1/category/" + localStorage.getItem("id") + "/" + category.id, category, {headers});
  }
}
