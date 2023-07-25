Feature: Show available housing
  As a tenant
  I need to see available houses
  So I can select one and book accommodation

  Background:
    Given the database consist of data in the file "src/test/resources/sqlData/testData.xml"
    * the user is on login page
    * he choose sign in as "tenant"
    When he fill username as "Amro" and password as "123"
    And press sign in button
    Then homePanel will appear in the tenant page form

  Scenario: see all available housing and booking accommodation
    Given he is on the available housing panel
    Then he should see these houses available:
    | name              |
    | City Apartment    |
    | Country farmhouse |
    When he click on first row "City Apartment" housing
    And press Show House button
    Then he should see the following house information:
      | name             | location                  | owner_name | owner_phone | rent | water_inclusive | electricity_inclusive | services                       | floors | apart_per_floor |
      | City Apartment   | 123 Main Street, City A   | owner1     | 0592793930  | 1000 | 1               | 0                     | Gym, Swimming Pool, Parking    | 5      | 4               |
    And should see a housing picture
    Given he choose floor 2 and apartment 3
    And he click on the Book button
    Then Then a new reservation should be added to the database with the accepted field set to "0"
    And the booking details should be displayed on the panel