import {Status} from "./Status";

export class Category {
  id: string;
  name: string;
  description: string;
  userId: string;
  status: Status;

  constructor(id: string, name: string, description: string, userId: string, status: Status) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.userId = userId;
    this.status = status;
  }
}
