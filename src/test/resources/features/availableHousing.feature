Feature: Show available housing
  As a tenant
  I need to see available houses
  So I can select one and book accommodation

  Background:
    Given
  @showAvailableHouses
  Scenario: show available houses
    Given database contains 2 houses with following data:
      | name   | location   | owner_id | rent | water_inclusive | electricity_inclusive | services   | floors | apart_per_floor |
      | Rose   | Beitleed   | 2        | 120  | 1               | 0                     | nothing    | 5      | 3               |

