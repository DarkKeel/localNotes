import { Injectable } from '@angular/core';
import {Category} from "../model/Category";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Note} from "../model/Note";

@Injectable({
  providedIn: 'root'
})
export class NoteService {

  constructor(public http: HttpClient) { }

  getNotes() {
    const headers = new HttpHeaders({Authorization: 'Bearer_' + localStorage.getItem("token")});
    return this.http.get<Note[]>(environment.API_URL + "/api/v1/note/" + localStorage.getItem("id"), {headers});
  }

  getNotesByCategory(category: Category) {
    const headers = new HttpHeaders({Authorization: 'Bearer_' + localStorage.getItem("token")});
    return this.http.get<Note[]>(environment.API_URL + "/api/v1/note/" + localStorage.getItem("id") + "/" + category.name, {headers});
  }

  updateNote(editNote: Note) {
    const headers = new HttpHeaders({Authorization: 'Bearer_' + localStorage.getItem("token")});
    return this.http.put<Note>(
      environment.API_URL + "/api/v1/note/" + localStorage.getItem("id") + "/" + editNote.id, editNote,{headers});
  }

  deleteNote(id: string) {
    const headers = new HttpHeaders({Authorization: 'Bearer_' + localStorage.getItem("token")});
    return this.http.delete<void>(environment.API_URL + "/api/v1/note/" + localStorage.getItem("id") + "/" + id, {headers});
  }

  createNote(newNote: Note) {
    const headers = new HttpHeaders({Authorization: 'Bearer_' + localStorage.getItem("token")});
    return this.http.post<Note>(
      environment.API_URL + "/api/v1/note/" + localStorage.getItem("id"), newNote,{headers});
  }
}
