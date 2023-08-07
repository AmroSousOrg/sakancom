Feature: Admin can accept or reject housing advertisement request
  As an admin
  I can accept or reject housing advertisement requests
  So owners can receive reservation requests

  Background:
    Given the database consist of data in the file "src/test/resources/sqlData/testData.xml"
    * the user is on login page
    * he choose sign in as "admin"
    When he fill username as "Admin" and password as "Admin"
    And press sign in button
    Then homePanel will appear in the admin page form

  Scenario: accept advertisements
    When an admin go to "requests" tab
    Then he should see all advertisements:
      | name         | location              | rent | owner   |
      | Cozy Cottage | 789 Elm Road, City C  | 1500 | owner1  |
    When he select 0 row index in the requests table
    And press request house details button
    Then he should see advertisement info:
      | housing_id | owner   | services                       |
      | 3          | owner1  | Roof Deck, Concierge Service   |
    When admin press on accept advertisement button
    Then new housing with this info will be added to database:
      | name          | owner_id |
      | Cozy Cottage  | 1        |

  Scenario: reject advertisements
    When an admin go to "requests" tab
    Then he should see all advertisements:
      | name         | location              | rent | owner   |
      | Cozy Cottage | 789 Elm Road, City C  | 1500 | owner1  |
    When he select 0 row index in the requests table
    And press request house details button
    Then he should see advertisement info:
      | housing_id | owner   | services                       |
      | 3          | owner1  | Roof Deck, Concierge Service   |
    When admin press on reject advertisement button
    Then housing advertisement with id 3 will be deleted from database
