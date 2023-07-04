Feature: Login page
  As a user I need to login to the system so I can take advantage
  of the services

  Scenario Outline: user sign in as a tenant
    Given the user is on login page
    When he fill username as "username" and password as "password"
    And press submit button
    Then search for user in the database
    And give the correct status as "status" and message as "message"

      Examples:
        | username | password | status  | message                          |
        | Amro     | '123'    | success | no message                       |
        | Amro     | '1234'   | fail    | invalid username and/or password |
        | Amr      | '123'    | fail    | invalid username and/or password |
        | Amro     | '12'     | fail    | invalid username and/or password |
