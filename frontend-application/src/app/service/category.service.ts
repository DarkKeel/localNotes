import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {User} from "../model/User";
import {Category} from "../model/Category";
import {environment} from "../../environments/environment";
import {CreateCategoryRequest} from "../model/CreateCategoryRequest";

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  constructor(public http: HttpClient) { }

  getCategories() {
    const headers = this.getAuthHeaders();
    return this.http.get<Category[]>(environment.API_URL + "/api/v1/category/" + localStorage.getItem("id"), {headers});
  }

  createCategory(category: CreateCategoryRequest) {
    const headers = this.getAuthHeaders();
    return this.http.post<void>(environment.API_URL + "/api/v1/category/", category, {headers});
  }

  deleteCategory(id: string) {
    const headers = this.getAuthHeaders();
    return this.http.delete<void>(environment.API_URL + "/api/v1/category/" + localStorage.getItem("id") + "/" + id, {headers});
  }

  updateCategory(category: Category) {
    const headers = this.getAuthHeaders();
    return this.http.put<void>(environment.API_URL + "/api/v1/category/", category, {headers});
  }

  private getAuthHeaders(): HttpHeaders {
    return new HttpHeaders({Authorization: 'Bearer_' + localStorage.getItem("token")});
  }
}
