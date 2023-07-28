Feature: admin can see all furniture in the shop
  As an admin
  I can see all furniture advertisement in the shop

  Background:
    Given the database consist of data in the file "src/test/resources/sqlData/testData.xml"
    * the user is on login page
    * he choose sign in as "admin"
    When he fill username as "Admin" and password as "Admin"
    And press sign in button
    Then homePanel will appear in the admin page form

  Scenario: accept advertisements
    When an admin go to "furniture" tab
    Then admin should see all furniture:
      | furniture_id | name         | price |
      | 1            | Sofa         | 500   |
      | 2            | Dining Table | 800   |
    When admin select 1 row index in the furniture table
    Then admin should see furniture info:
      | furniture_id | tenant    | name         | description                              | tenant_phone   |
      | 2            | Ahmad     | Dining Table | Solid wood dining table with six chairs  | 0598447485     |