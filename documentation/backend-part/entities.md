# Entities

1. User Entity 

| Field name  | Field type | Description                                   |
|-------------|------------|-----------------------------------------------|
| id          | long       | Unique id of product                          |
| publicId    | String     | Public id for users                           |
| name        | String     | Name of product. Should have a limit of chars |
| password    | String     | Password of user. Encoded                     |
 | role        | Role       | Type of access                                |
 | totalNotes  | int        | Count of all created notes                    |
 | openedNotes | int        | Count of all currently opened notes           |

2. Note Entity

| Field name  | Field type | Description                                |
|-------------|------------|--------------------------------------------|
| id          | long       | Unique id of Note                          |
| publicId    | String     | Public id for Note                         |
| name        | String     | Name of Note. Should have a limit of chars |
 | description | String     | Description of current Note                |
 | category    | Category   | Category of Note                           |
| userId      | String     | Public id of note's owner                  |
 | createDate  | Date       | Time of creation                           |
 | isActive    | boolean    | If true - note will be shown               |
 | isFavorite  | boolean    | If true - show first                       |

3. Category Entity

| Field name | Field type | Description                   |
|------------|------------|-------------------------------|
| id         | long       | Unique id of Category         |
| publicId   | String     | Public id for Category        |
| name       | String     | Name of Category.             |
 | userId     | String     | Public id of category's owner |

