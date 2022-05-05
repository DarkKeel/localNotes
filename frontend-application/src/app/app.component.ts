import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {Category} from "./model/Category";
import {CategoryService} from "./service/category.service";
import {HttpErrorResponse} from "@angular/common/http";
import {Status} from "./model/Status";
import {User} from "./model/User";
import {Note} from "./model/Note";
import {NoteService} from "./service/note.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'Local Notes';
  user: User;
  publicId = localStorage.getItem("id");
  token = localStorage.getItem("token");
  isLogged;
  categories: Category[];
  notes: Note[];
  categoryToDelete: Category;
  totalNotesCount: number;
  selectedCategory: Category;
  allCategories = new Category('-1', 'Все заметки', 'Список всех заметок',
    '',Status.ACTIVE, '', 0);

  constructor(private router:Router, private categoryService: CategoryService,
              private noteService: NoteService) {
    this.isLogged = this.checkToken();
  }

  ngOnInit(): void {
    this.runClock();
    if (!this.isLogged) {
      this.router.navigate(['login']);
    } else {
      this.selectedCategory = this.allCategories;
      this.updateInfo(this.publicId != null ? this.publicId : "");
    }
  }


  setIsLogged(value: boolean) {
    this.isLogged = value;
  }

  updateInfo(id: string) {
    this.categoryService.getCategories(id).subscribe(data => {
      this.categories = data;
      this.totalNotesCount = 0;
      data.forEach(x => this.totalNotesCount += x.countOfnotes);
      this.allCategories.countOfnotes = this.totalNotesCount;
    }, (error: HttpErrorResponse) => {
      if (error.status == 403 || error.status == 401) {
        this.isLogged = false;
        localStorage.removeItem("id")
        localStorage.removeItem("token")
        this.router.navigate(['login']);
      }
    });
  }

  private checkToken(): boolean {
    if (this.token != null && this.publicId != null) {
      return true;
    }
    return false;
  }

  showNotesByCategory(category: Category) {
    if (category != this.selectedCategory && category != this.allCategories) {
      this.selectedCategory = category;
      this.noteService.getNotesByCategory(category).subscribe(data => {
        this.notes = data;
      })
    } else if (category.name == this.allCategories.name) {
      this.noteService.getNotes().subscribe(data => {
        this.notes = data;
      })
    }
  }

  clearSelectedCategory() {
    this.selectedCategory = this.allCategories;
    this.showNotesByCategory(this.selectedCategory);
  }

  categorySettings() {

  }
  runClock() {
    window.setInterval(function(){
      let now = new Date();
      let clock = document.getElementById("clock");
      if (clock != null) {
        clock.innerHTML = now.toLocaleDateString('ru-ru', {day:"numeric", month:"long", year:"2-digit"})
          + '<br>' + now.toLocaleTimeString('ru-ru');
      }
    },1000);
  };

  onCreateNote(newNoteForm: any) {
    let category = this.categories.find(x => x.name === newNoteForm.category);
    if (newNoteForm.favorite === '') {
      newNoteForm.favorite = false;
    }
    if (category != null) {
      let newNote = new Note('', newNoteForm.name, newNoteForm.desc, category, newNoteForm.favorite,
        new Date(), new Date(), Status.ACTIVE);
      this.noteService.createNote(newNote).subscribe(data => {
        this.updateNotesInfo();
      })
    }
  }

  private updateNotesInfo() {
    this.noteService.getNotes().subscribe(data => {
      this.notes = data;
    }, (error: HttpErrorResponse) => {
      if (error.status == 403 || error.status == 401) {
        this.isLogged = false;
        localStorage.removeItem("id")
        localStorage.removeItem("token")
        this.router.navigate(['login']);
      }
    });
  }


  removeCategory(category: Category) {
    this.categoryToDelete = category;
    const container = document.getElementById('main-container');
    const button = document.createElement('button');
    button.type = 'button';
    button.style.display = 'none';
    button.setAttribute('data-toggle', 'modal');
    button.setAttribute('data-target', '#deleteCategoryConfirmModal');
    container?.appendChild(button);
    button.click();
  }


  onDeleteCategory(id: string | undefined) {
    if (id != null) {
      this.categoryService.deleteCategory(id).subscribe(data => {
        this.updateInfo(localStorage.getItem("id") || '');
      })
    }
  }

  onCreateCategory(value: any){
    // @ts-ignore
    let newCategory = new Category('', value.nameCat, value.descCat, localStorage.getItem("id"),
      Status.ACTIVE, null, 0);
    this.categoryService.createCategory(newCategory).subscribe(data => {
      this.updateInfo(localStorage.getItem("id") || '');
    })
  }

}
