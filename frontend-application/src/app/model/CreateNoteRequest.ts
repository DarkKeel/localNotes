import {Category} from "./Category";

export class CreateNoteRequest {
  name: string;
  description: string;
  category: Category;
  favorite: boolean;
  userId: string;

  constructor(name: string, description: string, category: Category, favorite: boolean, userId: string) {
    this.name = name;
    this.description = description;
    this.category = category;
    this.favorite = favorite;
    this.userId = userId;
  }
}
