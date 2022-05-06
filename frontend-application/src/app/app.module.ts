import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import {RouterModule, Routes} from "@angular/router";
import {HttpClientModule} from "@angular/common/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { LoginpageComponent } from './views/loginpage/loginpage.component';
import { NotesComponent } from './views/notes/notes.component';
import {ColorPickerModule} from "ngx-color-picker";

const routes: Routes = [
  {path:"login", component:LoginpageComponent}
];

@NgModule({
  declarations: [
    AppComponent,
    LoginpageComponent,
    NotesComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(routes),
    ColorPickerModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
