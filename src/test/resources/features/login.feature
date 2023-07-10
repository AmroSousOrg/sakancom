Feature: Login page
  As a user I need to login to the system so I can take advantage
  of the services

  @signIn
  Scenario Outline: user sign in
    Given the user is on login page
    And he choose sign in as "<role>"
    When he fill username as "<username>" and password as "<password>"
    And press sign in button
    Then give the correct status as "<status>" and message as "<message>"
    And navigate to the correct user page depending on "<status>" and "<role>"

      Examples:
        | role     | username     | password      | status  | message                           |
        | tenant   | Amro         |  123          | success | no message                        |
        | tenant   | Amro         |  1234         | fail    | Invalid username and/or password. |
        | tenant   | Amr          |  123          | fail    | Invalid username and/or password. |
        | tenant   | Amro         |               | fail    | Invalid username and/or password. |
        | tenant   | Ahmad        |  12345        | success | no message                        |
        | admin    | Admin        |  Admin        | success | no message                        |
        | admin    | Admin        |  admin        | fail    | Invalid username and/or password. |
        | owner    | owner        |  owner        | fail    | Invalid username and/or password. |
        | owner    | owner1       |  owner1       | success | no message                        |

