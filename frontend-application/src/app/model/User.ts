export class User {
  id: string;
  username: string;
  firstName: string;
  lastname: string;
  email: string;

  constructor(id: string, username: string, firstName: string, lastname: string, email: string) {
    this.id = id;
    this.username = username;
    this.firstName = firstName;
    this.lastname = lastname;
    this.email = email;
  }
}
