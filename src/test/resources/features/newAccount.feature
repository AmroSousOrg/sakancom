Feature: Create new account
  As a new user
  I need to create a new account
  So that I can access the system

  Background:
    Given the user is on login page
    When press I don't have account button
    Then create account panel appears

  @validAccountCreation
  Scenario Outline: Create an account with valid data
    Given user choose create "<accountType>" role account
    And he fill the following details in create "<accountType>" account panel:
      | Field            | Value               |
      | name             | <name>              |
      | password         | <password>          |
      | confirm_pass     | <password>          |
      | email            | <email>             |
      | phone            | <phone>             |
      | age              | <age>               |
      | university_major | <university_major>  |
    When he clicks create account button
    Then a new "<accountType>" account with name "<name>" will be added to the database
    And "<accountType>" should see an "Account created successfully." message in create account

    Examples:
      | accountType | name   | password | email                 | phone      | age | university_major     |
      | tenant      | person | passkey  | my.mail@gmail.com     | 0561214477 | 30  | electric engineer    |
      | owner       | owner9 | pass123  | samir10@gmail.com     | 0561314873 |     |                      |

  @invalidCreationData
  Scenario Outline: Create an account with invalid data
    Given user choose create "<accountType>" role account
    And he fill the following details in create "<accountType>" account panel:
      | Field            | Value               |
      | name             | <name>              |
      | password         | <password>          |
      | confirm_pass     | <password>          |
      | email            | <invalid_email>     |
      | phone            | <phone>             |
      | age              | <age>               |
      | university_major | <university_major>  |
    When he clicks create account button
    Then "<accountType>" should see an "<error_message>" message in create account

    Examples:
      | accountType | name   | password | invalid_email        | phone      | age | university_major     | error_message              |
      | tenant      | owner9 | pass123  | something@gmail      | 0561314873 | 30  | electric engineer    | Invalid email.             |
      | tenant      | owner9 | pass123  | something@gmail.com  | 0561314873 | aaa | electric engineer    | Invalid age.               |
      | owner       | owner9 | pass123  | something@.com       | 0561314873 |     |                      | Invalid email.             |
      | owner       | owner9 | pass123  | something@mail.com   | 059hello66 |     |                      | Invalid phone.             |

  @invalidCreationUsername
  Scenario Outline: Create an account with already used name
    Given there is an "<accountType>" account with name "<existing_name>" already exist
    And user choose create "<accountType>" role account
    And he fill the following details in create "<accountType>" account panel:
      | Field            | Value               |
      | name             | <existing_name>     |
      | password         | <password>          |
      | confirm_pass     | <password>          |
      | email            | <email>             |
      | phone            | <phone>             |
      | age              | <age>               |
      | university_major | <university_major>  |
    When he clicks create account button
    Then "<accountType>" should see an "Username is already exist." message in create account

    Examples:
      | accountType | existing_name | password | email                 | phone      | age | university_major     |
      | tenant      | Amro          | passkey  | my.mail@gmail.com     | 0561214477 | 30  | electric engineer    |
      | owner       | owner1        | passkey  | my.mail@gmail.com     | 0561214477 |     |                      |
