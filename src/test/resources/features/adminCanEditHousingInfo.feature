Feature: admin can see and edit housing
  As an admin
  I can See all housing and edit some of there information

  Background:
    Given the database consist of data in the file "src/test/resources/sqlData/testData.xml"
    * the user is on login page
    * he choose sign in as "admin"
    When he fill username as "Admin" and password as "Admin"
    And press sign in button
    Then homePanel will appear in the admin page form

  Scenario: admin see all housing
    When an admin go to "housing" tab
    Then admin should see all houses:
      | name              | location                  | rent   | owner        |
      | City Apartment    | 123 Main Street, City A   | 1000   | owner1       |
      | Country farmhouse | 456 Park Avenue, City B   | 3000   | owner2       |
    When he select 1 row index in the houses table
    And admin press house details button
    Then he should see all house info:
      | housing_id | owner_name   | name              | location                | services                      |
      | 2          | owner2       | Country farmhouse | 456 Park Avenue, City B | Garden, Terrace, Security     |

  Scenario Outline: admin edit housing info with invalid data
    Given an admin go to "housing" tab
    And he select 0 row index in the houses table
    And admin press house details button
    When admin press edit house button
    And admin put these info in the fields:
      | name    | location     | rent   | water_inclusive | electricity_inclusive | services   | floors   | apart_per_floor |
      | <name>  | <location>   | <rent> | <water>         | <electricity>         | <services> | <floors> | <apart>         |
    And admin press save house edit button
    Then admin should see this message "<error_msg>"

    Examples:
      | name             | location                 | rent | water | electricity | services                       | floors | apart | error_msg                                           |
      | City Apartment   |                          | 1000 | 1     | 0           | Gym, Swimming Pool, Parking    | 5      | 4     | Empty field.                                        |
      | Country Farmhouse| 123 Main Street, City A  | 1000 | 1     | 0           | Gym, Swimming Pool, Parking    | 5      | 4     | House name already exist.                           |
      | City Apartment   | 123 Main Street, City A  | -50  | 1     | 0           | Gym, Swimming Pool, Parking    | 5      | 4     | Rent must be Non-negative integer.                  |
      | City Apartment   | 123 Main Street, City A  | 2000 | 1     | 0           | Gym, Swimming Pool, Parking    | ggg    | 4     | Floor must be Non-negative integer.                 |
      | City Apartment   | 123 Main Street, City A  | 1000 | 1     | 1           |                                | 5      | 4     | Empty field.                                        |

  Scenario: admin edit housing info with valid data
    Given an admin go to "housing" tab
    And he select 0 row index in the houses table
    And admin press house details button
    When admin press edit house button
    And admin put these info in the fields:
      | name           | location                | rent   | water_inclusive | electricity_inclusive | services           | floors   | apart_per_floor |
      | City Apartment | 123 Main Street, City C | 2000   | 1               | 1                     | Gym, Swimming Pool | 6        | 3               |
    And admin press save house edit button
    Then admin should see this message "House updated successfully."
    And house with these info exist in database
      | housing_id | name           | location                | rent   | water_inclusive | electricity_inclusive | services           | floors   | apart_per_floor |
      | 1          | City Apartment | 123 Main Street, City C | 2000   | 1               | 1                     | Gym, Swimming Pool | 6        | 3               |