import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {Category} from "./model/Category";
import {CategoryService} from "./service/category.service";
import {HttpErrorResponse} from "@angular/common/http";
import {Status} from "./model/Status";
import {Note} from "./model/Note";
import {NoteService} from "./service/note.service";
import {CreateCategoryRequest} from "./model/CreateCategoryRequest";
import {CreateNoteRequest} from "./model/CreateNoteRequest";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'Local Notes';
  publicId = localStorage.getItem("id");
  token = localStorage.getItem("token");
  isLogged;
  notes: Note[];
  categories: Category[] = [];
  selectedCategoryToView: Category;
  selectedCategoryToEdit: Category;
  selectedColor: string;
  totalNotesCount: number;
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
      this.selectedCategoryToView = this.allCategories;
      this.updateInfo();
    }
  }


  setIsLogged(value: boolean) {
    this.isLogged = value;
  }

  updateInfo() {
    this.categoryService.getCategories().subscribe(data => {
      this.categories = data;
      this.totalNotesCount = 0;
      data.forEach(x => this.totalNotesCount += x.countOfNotes);
      this.allCategories.countOfNotes = this.totalNotesCount;
    }, (error: HttpErrorResponse) => {
      if (error.status == 403 || error.status == 401) {
        this.logout();
      }
    });
  }

  private checkToken(): boolean {
    return this.token != null && this.publicId != null;
  }

  showNotesByCategory(category: Category) {
    if (category != this.selectedCategoryToView && category != this.allCategories) {
      this.selectedCategoryToView = category;
      this.noteService.getNotesByCategory(category).subscribe(data => {
        this.notes = data;
      }, (error: HttpErrorResponse) => {
        if (error.status == 403 || error.status == 401) {
          this.logout();
        }
      });
    } else if (category === this.allCategories) {
      this.selectedCategoryToView = this.allCategories;
      this.noteService.getNotes().subscribe(data => {
        this.notes = data;
      })
    }
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
    // @ts-ignore
    let newNote = new CreateNoteRequest(newNoteForm.name, newNoteForm.desc, category, newNoteForm.favorite, this.publicId);
    this.noteService.createNote(newNote).subscribe(() => {
      this.categoryService.updateCategory(newNote.category).subscribe(() => {
        // @ts-ignore
        this.updateInfo();
        this.updateNotesInfo();
      });
    })
  }

  private updateNotesInfo() {
    this.noteService.getNotes().subscribe(data => {
      this.notes = data;
    }, (error: HttpErrorResponse) => {
      if (error.status == 403 || error.status == 401) {
        this.logout();
      }
    });
  }


  removeCategory() {
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
        this.updateInfo();
      }, (error: HttpErrorResponse) => {
        if (error.status == 403 || error.status == 401) {
          this.logout();
        }
      });
    }
  }

  onCreateCategory(value: any){
    // @ts-ignore
    let newCategory = new CreateCategoryRequest(value.nameCat, value.descCat, this.publicId);
    this.categoryService.createCategory(newCategory).subscribe(() => {
      this.updateInfo();
    }, (error: HttpErrorResponse) => {
      if (error.status == 403 || error.status == 401) {
        this.logout();
      }
    });
  }

  onUpdateCategory(value: any) {
    // @ts-ignore
    let updateCategory = new Category(value.id, value.nameCat, value.descCat, this.publicId,
      Status.ACTIVE, value.color, value.count);
    this.categoryService.updateCategory(updateCategory).subscribe(() => {
      this.updateInfo();
      this.updateNotesInfo();
    }, (error: HttpErrorResponse) => {
      if (error.status == 403 || error.status == 401) {
        this.logout();
      }
    });
  }



  changeColor(cats: Category) {
    this.selectedColor = cats.color != null ? cats.color : '#FFFFF';
    const container = document.getElementById('main-container');
    const button = document.createElement('button');
    button.type = 'button';
    button.style.display = 'none';
    button.setAttribute('data-toggle', 'modal');
    button.setAttribute('data-target', '#changeColorModal');
    container?.appendChild(button);
    button.click();
  }

  saveColor() {
    this.selectedCategoryToEdit.color = this.selectedColor;
  }



  selectCategory(category: Category) {
    this.selectedCategoryToEdit = category;
  }


  clearCategoryToDelete() {
    // @ts-ignore
    this.selectedCategoryToEdit = null;
  }

  logout() {
    this.isLogged = false;
    localStorage.removeItem("id")
    localStorage.removeItem("token")
    this.router.navigate(['login']);
  }
}
