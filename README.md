# Проект "LocalNotes"


## Описание

Данное приложение состоит из двух частей: 
1. REST сервер на Spring Boot
2. UI приложение на Angular

Приложение предназначено для создания/редактирования/удаления заметок, а также для добавления напоминаний.

## Запуск проекта

Для запуска проекта необходимо JDK 11, Docker, Node.js, Angular-CLI.

### Запуск backend модуля
1. Установить Docker с официального сайта.
2. После запуска докера, необходимо открыть в командной строке каталог `dockers` и выполнить команду `docker compose up`.
3. Запустить [LocalNotesApplication](backend-application/src/main/java/com/localnotes/LocalNotesApplication.java), указав локальный профиль в VM options `-Dspring.profiles.active=local`.

### Запуск frontend модуля
1. Установить Node.JS с официального сайта.
2. В командной строке выполнить `npm install`.
3. После успешной установки, выполнить команду `npm install @angular/cli` для установки Angular-CLI.
4. В командной строке перейти в каталог модуля `frontend-application` и выполнить команду `ng serve`.