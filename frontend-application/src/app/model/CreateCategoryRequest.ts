export class CreateCategoryRequest {
  name: string;
  description: string;
  userId: string;

  constructor(name: string, description: string, userId: string) {
    this.name = name;
    this.description = description;
    this.userId = userId;
  }
}
