import {Category} from "./Category";
import {Status} from "./Status";

export class Note {
  id: string;
  name: string;
  description: string;
  category: Category;
  favorite: boolean;
  created: Date;
  updated: Date;
  status: Status;
  userId: string;

  constructor(id: string, name: string, description: string, category: Category, favorite: boolean, created: Date, updated: Date, status: Status, userId: string) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.category = category;
    this.favorite = favorite;
    this.created = created;
    this.updated = updated;
    this.status = status;
    this.userId = userId;
  }
}
