Feature: admin can see all reservations
  As an admin
  I can see all reservations via the system
  and I can accept or reject reservations

  Background:
    Given the database consist of data in the file "src/test/resources/sqlData/testData.xml"
    * the user is on login page
    * he choose sign in as "admin"
    When he fill username as "Admin" and password as "Admin"
    And press sign in button
    Then homePanel will appear in the admin page form

  Scenario: see all reservations
    When an admin go to "reservations" tab
    Then he should see all reservations:
      | reservation_id | tenant_id | housing_id | reservation_date       | floor_num | apart_num | accepted |
      | 1              | 1         | 1          | 2023-07-15 15:12:58    | 3         | 2         | 1        |
      | 2              | 2         | 1          | 2023-07-15 15:12:58    | 2         | 1         | 1        |
    When he select 0 row index in the reservations table
    And press reservation details button
    Then he should see this reservation info:
      | reservation_id | tenant_id | housing_id | owner_id |
      | 1              | 1         | 1          | 1        |