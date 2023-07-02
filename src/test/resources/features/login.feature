
Feature: Login
  the user want to login to the system

  Scenario Outline: Tenant login
    Given The tenant not logged in
    When tenant enters "<username>" and "<password>"
    Then should verify the "status" is correct
    And give the correct message

    	Examples:
    		| username | password | status |
    		| Amro | "123" | success |
    		| anythingelse | anythingelse | fail |
    		| Ahmad | "123" | fail |
    		| Amro | "1234" | fail |