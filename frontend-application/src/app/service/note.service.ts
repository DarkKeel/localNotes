import { Injectable } from '@angular/core';
import {Category} from "../model/Category";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Note} from "../model/Note";
import {CreateNoteRequest} from "../model/CreateNoteRequest";

@Injectable({
  providedIn: 'root'
})
export class NoteService {

  constructor(public http: HttpClient) { }

  getNotes() {
    const headers = this.getAuthHeaders();
    return this.http.get<Note[]>(environment.API_URL + "/api/v1/note/" + localStorage.getItem("id"), {headers});
  }

  getNotesByCategory(category: Category) {
    const headers = this.getAuthHeaders();
    return this.http.get<Note[]>(environment.API_URL + "/api/v1/note/" + localStorage.getItem("id") + "/" + category.name, {headers});
  }

  updateNote(editNote: Note) {
    const headers = this.getAuthHeaders();
    return this.http.put<void>(
      environment.API_URL + "/api/v1/note/", editNote,{headers});
  }

  deleteNote(id: string) {
    const headers = this.getAuthHeaders();
    return this.http.delete<void>(environment.API_URL + "/api/v1/note/" + localStorage.getItem("id") + "/" + id, {headers});
  }

  createNote(newNote: CreateNoteRequest) {
    const headers = this.getAuthHeaders();
    return this.http.post<void>(
      environment.API_URL + "/api/v1/note/", newNote,{headers});
  }

  private getAuthHeaders(): HttpHeaders {
    return new HttpHeaders({Authorization: 'Bearer_' + localStorage.getItem("token")});
  }
}
