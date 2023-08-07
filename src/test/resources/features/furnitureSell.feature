Feature: show and sell Furniture
  As a tenant
  I can see available furniture in the shop
  and list my own furniture for sell

  Background:
    Given the database consist of data in the file "src/test/resources/sqlData/testData.xml"
    * the user is on login page
    * he choose sign in as "tenant"
    When he fill username as "Amro" and password as "123"
    And press sign in button
    Then homePanel will appear in the tenant page form

  Scenario: see all available furniture
    When he go to available furniture tap
    Then he should see all available furniture:
      | furniture_id | name         | price |
      | 1            | Sofa         | 500   |
      | 2            | Dining Table | 800   |
    When he click on 0 row index in the furniture table
    Then he should see this furniture info:
      | furniture_id | tenant_name | name         | description                              | tenant_phone |
      | 1            | Amro        | Sofa         | Comfortable sofa with leather upholstery | 0592793930   |

  Scenario: add furniture with valid input
    Given he go to available furniture tap
    Given he click on add new furniture button
    And enter these new furniture info in fields:
      | name  | price | description          |
      | phone | 8000  | Apple phone not used |
    And press add new furniture submit button
    Then he should redirected to all furniture panel
    And a new furniture will be added at last of table:
      | name  | price |
      | phone | 8000  |

  Scenario Outline: add furniture with invalid data
    Given he click on add new furniture button
    And enter these new furniture info in fields:
      | name   | price    | description   |
      | <name> | <price>  | <description> |
    And press add new furniture submit button
    Then error message will displayed "<error_msg>"

    Examples:
      | name       | price       | description             | error_msg                           |
      | phone      | -200        | some description here   | Price must be Non-negative integer.  |
      | Chair      | 5000        |                         | Empty field.                        |
      |            | 1000        | some description here   | Invalid name.                       |
      | Sofa       | 5nis        | some description here   | Price must be Non-negative integer.  |
