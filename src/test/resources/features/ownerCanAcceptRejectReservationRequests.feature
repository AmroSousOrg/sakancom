Feature: Owner can accept or reject reservations
  As an owner
  I can accept or reject reservation requests to my housing

  Background:
    Given the database consist of data in the file "src/test/resources/sqlData/testData.xml"
    * the user is on login page
    * he choose sign in as "owner"
    When he fill username as "owner1" and password as "123"
    And press sign in button
    Then homePanel will appear in the owner page form

  Scenario: see all reservations
    When an owner go to "booking requests" tab
    Then owner should see all requests to his housing
      | reservation_id | housing_id | tenant_id | reservation_date    | floor | apart |
      | 3              | 1          | 1         | 2023-07-15T15:12:58 | 3     | 1     |
    When owner choose 0 row in requests table
    And owner press request details button
    Then owner should see reservation details

  Scenario: accept reservation request
    Given an owner go to "booking requests" tab
    And owner choose 0 row in requests table
    And owner press on accept button
    Then reservation with id 3 will accepted

  Scenario: reject reservation request
    Given an owner go to "booking requests" tab
    And owner choose 0 row in requests table
    And owner press on reject button
    Then reservation with id 3 will deleted
