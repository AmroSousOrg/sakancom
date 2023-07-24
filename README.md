# Sakancom App 

## Description

* java application to manage booking process and rent electronically and sell used furniture
* built on Agile, BDD, TDD technique

---

## Github

* to switch to your branch
  ` git checkout -b <your_branch> `

* to show status of changes
  ` git status `

* to show you branches
  ` git branch `

* to save changes and *push* to origin repo
```
    git add .
    git commit -m "message"
    git push origin <your_branch>
```
* to pull changes from github to local repo
  ` git pull origin <branch> `

* to merge branch A into B .. go to B and type
  ` git merge <branch_A> `

---

## Architecture and files 

- ' pages package ' : contains the pages logic and design
- ' common package ' : contains helper classes such as database functions 
    and validation logic and others
- ' exceptions package ' : contains custom exceptions that help in write
    the logic and communication between classes
- ' test/resources/sqlData ' file : used in seed the test database
    with data in unit testing, to avoid collisions with real data
  (something like mocking)
- ' test/resources/features ' file : contains BDD feature files.
- ' test/java/sakancom.test ' package : contains the testing code
  (step definitions and test runners and database seeder).

---

## Install 

### Database setup 

* you need to create two databases into MySql (MariaDB) server
  - sakancom_db.sql 
    - this is the main database, contains users data
  - sakancom_test.sql 
    - this is the test database, used in unit testing.

  ``` 
      You have to import sql files into your local server 
     - sakancom_db.sql
     - sakancom_test.sql 
  ```
* also you need to create user account in MySql with this info
  - username: sw_team
  - password: 12345

* database port: 3306

* NOTE: if you need to change these settings you need to edit 
  ' ourConfig.txt ' file 

### project setup 

* you need to import the repo into your IDE (IntelliJ) and maven will install all dependencies

* you need cucumber plugin to support Gherkin language

#### Now you have the project ... *Happy Testing :)* 