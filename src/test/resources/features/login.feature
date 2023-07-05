Feature: Login page
  As a user I need to login to the system so I can take advantage
  of the services

  Scenario Outline: user sign in as a tenant
    Given the user is on login page
    And he choose sign in as tenant
    When he fill username as "<username>" and password as "<password>"
    And press submit button
    Then give the correct status as "<status>" and message as "<message>"
    And navigate to the tenant page if "<status>" is success

      Examples:
        | username | password    | status  | message                           |
        | Amro     |  123        | success | no message                        |
        | Amro     |  1234       | fail    | Invalid username and/or password. |
        | Amr      |  123        | fail    | Invalid username and/or password. |
        | Amro     |             | fail    | Invalid username and/or password. |
        | Ahmad    |  12345      | success | no message                        |
