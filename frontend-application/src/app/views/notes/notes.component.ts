import {Component, Input, OnInit, Output} from '@angular/core';
import {Category} from "../../model/Category";
import {Note} from "../../model/Note";
import {Router} from "@angular/router";
import {NoteService} from "../../service/note.service";
import {AppComponent} from "../../app.component";
import {HttpErrorResponse} from "@angular/common/http";
import {Status} from "../../model/Status";
import {CategoryService} from "../../service/category.service";

@Component({
  selector: 'app-notes',
  templateUrl: './notes.component.html',
  styleUrls: ['./notes.component.css']
})
export class NotesComponent implements OnInit {

  @Input()
  category: Category;
  @Input()
  categoriesList: Category[];
  @Input()
  notes: Note[];
  editNote: Note;

  constructor(private router: Router, private noteService:NoteService,
              private app: AppComponent, private categoryService:CategoryService) { }

  ngOnInit(): void {
  }

  showDescription(description: string): string {
    let lines = description.split('\n');
    let desc;
    if (lines.length > 4) {
      desc = description.substring(0, description.indexOf(lines[3])).concat("...");
    } else {
      desc = description.length > 100 ? description.substring(0, 100) : description;
      if (desc != description) {
        let index = desc.lastIndexOf(" ");
        desc = desc.substring(0, index).concat("...");
      }
    }
    return desc;
  }

  updateSelectedNote(tmpNote: any) {
    this.editNote.name = tmpNote.name;
    this.editNote.description = tmpNote.desc;
    let category1 = this.categoriesList.find(x => x.name === tmpNote.category);
    // @ts-ignore
    this.editNote.category = category1;
    this.editNote.favorite = tmpNote.favorite;
    this.editNote.updated = new Date();
    this.noteService.updateNote(this.editNote).subscribe(() => {
      this.categoryService.updateCategory(this.editNote.category).subscribe( () => {
        this.app.updateInfo();
      }, (error: HttpErrorResponse) => {
        if (error.status == 403 || error.status == 401) {
          this.app.logout();
        }
      });
    }, (error: HttpErrorResponse) => {
      if (error.status == 403 || error.status == 401) {
        this.app.logout();
      }
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

  onDeleteNote(id: string | undefined) {
    if (id != null) {
      this.noteService.deleteNote(id).subscribe(() => {
        let note = this.notes.find(x => x.id === id);
        // @ts-ignore
        let cat = note.category;
        this.categoryService.updateCategory(cat).subscribe(() => {
          this.app.updateInfo();
        }, (error: HttpErrorResponse) => {
          if (error.status == 403 || error.status == 401) {
            this.app.logout();
          }
        });
      }, (error: HttpErrorResponse) => {
        if (error.status == 403 || error.status == 401) {
          this.app.logout();
        }
      });
    }
  }
}
