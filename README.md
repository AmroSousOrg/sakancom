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
  
- src
  
  - main
    
    - java
      - sakancom
        - common
        - pages
         
    - resource
      - images
      
  - test
    - java
      - sakancom.test
        
    - resource
      - features

- target
  - HtmlReports

---

## Install 

### Database setup 

* you need to import two databases into MariaDB system
  - sakancom_db.sql 
    - this is the main database, contains users data
  - sakancom_test.sql 
    - this is the test database, contains specific data for testing purposes.

* also you need to create user account with this info
  - username: sw_team
  - password: 12345

* database port: 3306

### project setup 

* you need to import the repo into your IDE (IntelliJ) and maven will install all dependencies

* you need cucumber plugin to support Gherkin language
