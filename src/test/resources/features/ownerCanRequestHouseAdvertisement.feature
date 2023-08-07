Feature: Owner can add housing advertisement request
  As an owner
  I can request admin to add new housing
  So tenants can make reservations on it

  Background:
    Given the database consist of data in the file "src/test/resources/sqlData/testData.xml"
    * the user is on login page
    * he choose sign in as "owner"
    When he fill username as "owner1" and password as "123"
    And press sign in button
    Then homePanel will appear in the owner page form

  Scenario Outline: Add housing with invalid data
    Given an owner go to "add housing" tab
    When owner fill new housing info as follow:
      | name   | location   | rent   | water   | electricity   | services   | floors   | apart   | picture   |
      | <name> | <location> | <rent> | <water> | <electricity> | <services> | <floors> | <apart> | <picture> |
    And owner press on submit button
    Then owner should see this message "<err_msg>"

    Examples:
      | name           | location           | rent | water | electricity | services             | floors | apart | picture           | err_msg                             |
      |                | New York           | 2000 | 0     | 0           | GYM                  | 2      | 3     | cozy_cottage.jpeg | Empty field.                        |
      | City Apartment | New York           | 2000 | 0     | 1           | GYM                  | 2      | 3     | cozy_cottage.jpeg | House name already exist.           |
      | Cozy House     |                    | 2000 | 1     | 0           | GYM                  | 2      | 3     | cozy_cottage.jpeg | Empty field.                        |
      | Cozy House     | New York           | abc  | 1     | 1           | GYM                  | 2      | 3     | cozy_cottage.jpeg | Rent must be Non-negative integer.  |
      | Cozy House     | New York           | -400 | 1     | 1           | GYM                  | 2      | 3     | cozy_cottage.jpeg | Rent must be Non-negative integer.  |
      | Cozy House     | New York           | 2000 | 0     | 0           | GYM                  | Two    | 3     | cozy_cottage.jpeg | Floor must be Non-negative integer. |
      | Cozy House     | New York           | 2000 | 0     | 0           |                      | 2      | 3     | cozy_cottage.jpeg | Empty field.                        |
      | Cozy House     | New York           | 2000 | 0     | 1           | GYM                  | 2      | 3     | cozy_cottage.jpg  | Image cannot be opened.             |
      | Cozy House     | New York           | 2000 | 1     | 1           | GYM                  | 2      | -5    | cozy_cottage.jpeg | Apart must be Non-negative integer. |

  Scenario: Add housing with valid data
    Given an owner go to "add housing" tab
    When owner fill new housing info as follow:
      | name    | location  | rent | water | electricity | services   | floors | apart | picture           |
      | newName | Palestine | 4100 | 0     | 1           | GYM        | 4      | 3     | cozy_cottage.jpeg |
    And owner press on submit button
    Then owner should see this message "Add Housing request was sent to admin."
    And new housing with name "newName" will added to database