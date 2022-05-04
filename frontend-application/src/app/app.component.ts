import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {Category} from "./model/Category";
import {CategoryService} from "./service/category.service";
import {HttpErrorResponse} from "@angular/common/http";
import {Status} from "./model/Status";

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
  categories: Category[];
  totalNotesCount: number;
  selectedCategory: Category;

  constructor(private router:Router, private categoryService: CategoryService) {
    this.isLogged = this.checkToken();
  }

  ngOnInit(): void {
    if (!this.isLogged) {
      this.router.navigate(['login']);
    } else {
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
    this.selectedCategory = category;
  }

  clearSelectedCategory() {
    this.selectedCategory = new Category('-1', 'Все', '','',Status.ACTIVE, '', 0);
  }

  categorySettings() {

  }
}
