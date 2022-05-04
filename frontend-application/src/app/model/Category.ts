import {Status} from "./Status";

export class Category {
  id: string;
  name: string;
  description: string;
  userId: string;
  status: Status;
  color: string;
  countOfnotes: number;

  constructor(id: string, name: string, description: string, userId: string,
              status: Status, color: string, countOfnotes: number) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.userId = userId;
    this.status = status;
    this.color = color;
    this.countOfnotes = countOfnotes;
  }
}
