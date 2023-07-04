# Sakancom App 

## Description

* java application to manage booking process and rent electronically and sell used furniture
* built on Agile, BDD, TDD technique

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
  
## Architecture and files 
  
- src
  
  - main
    
    - java
      - sakancom
         
    - resource
      
  - test
    
    - java
      
      - sakancom.test
        
    - resource
      - features

- target
  - HtmlReports

## Install 

- you have to import sql file into phpMyAdmin server
  - database username : sw_team
  - database password : 12345
- import the project into intelliJ IDE
