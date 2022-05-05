import {Component, Input, OnInit, Output} from '@angular/core';
import {Category} from "../../model/Category";
import {Note} from "../../model/Note";
import {Router} from "@angular/router";
import {NoteService} from "../../service/note.service";
import {AppComponent} from "../../app.component";
import {HttpErrorResponse} from "@angular/common/http";
import {Status} from "../../model/Status";

@Component({
  selector: 'app-notes',
  templateUrl: './notes.component.html',
  styleUrls: ['./notes.component.css']
})
export class NotesComponent implements OnInit {

  @Input()
  category: Category;
  categoriesList: Category[];
  @Input()
  notes: Note[];
  editNote: Note;
  favorite: boolean = false;

  constructor(private router: Router, private noteService:NoteService,
              private app: AppComponent) { }

  ngOnInit(): void {
    this.updateInfo();
  }

  showDescription(description: string): string {
    let desc = description.length > 100 ? description.substring(0, 100) : description;
    if (desc != description) {
      let index = desc.lastIndexOf(" ");
      desc = desc.substring(0, index).concat("...");
    }
    return desc;
  }

  private updateInfo() {
    this.noteService.getNotes().subscribe(data => {
      this.notes = data;
      this.categoriesList = this.app.categories;
    }, (error: HttpErrorResponse) => {
      if (error.status == 403 || error.status == 401) {
        this.app.isLogged = false;
        localStorage.removeItem("id")
        localStorage.removeItem("token")
        this.router.navigate(['login']);
      }
    });
  }

  onUpdateNote(tmpNote: any) {
    this.editNote.name = tmpNote.name;
    this.editNote.description = tmpNote.desc;
    let category1 = this.categoriesList.find(x => x.name === tmpNote.category);
    this.editNote.category = category1 != null ? category1 : this.categoriesList[0];
    this.editNote.favorite = tmpNote.favorite;
    this.editNote.updated = new Date();
    this.noteService.updateNote(this.editNote).subscribe(data => {
      this.updateInfo();
    });
  }

  onOpenModal(note: Note, mode: string) {
    const container = document.getElementById('main-container');
    const button = document.createElement('button');
    button.type = 'button';
    button.style.display = 'none';
    button.setAttribute('data-toggle', 'modal');
    if (mode === 'view') {
      this.editNote = note;
      button.setAttribute('data-target', '#viewNoteModal');
    }
    if (mode === 'edit') {
      this.editNote = note;
      button.setAttribute('data-target', '#editNoteModal');
    }
    if (mode === 'delete') {
      this.editNote = note;
      button.setAttribute('data-target', '#deleteNoteModal');
    }
    container?.appendChild(button);
    button.click();
  }

  checkStatus(editNote: Note): boolean {
    if (editNote != null) {
      return editNote.status !== Status.ACTIVE;
    } else {
      return false;
    }
  }

  onDeleteNote(id: string | undefined) {
    if (id != null) {
      this.noteService.deleteNote(id).subscribe(data => {
        this.updateInfo();
      });
    }
  }
}
