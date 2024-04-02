Feature: a real movie can be added with roles to the database
  Scenario: movie search returns no results
    When the client searches a movie with the query asdasdasd
    Then the client receives status code of 200
    And the client receives no results
  Scenario: movie search returns results
    When the client searches a movie with the query Interstellar
    Then the client receives status code of 200
    And the client receives at least one result with the title Interstellar
  Scenario: movie gets added to the database
    Given the database is empty
    When the client adds a movie with the id from the search results
    Then the client receives status code of 201
    And the movie is saved in the database with the name Interstellar
    And the actors are saved in the database
    And the roles are saved in the database
  Scenario: movie retrival from the database
    When the client searches for the saved movie
    Then the client receives status code of 200
    And the client receives the movie named Interstellar
  Scenario: movie deletion from the database
    When the client deletes the saved movie
    Then the client receives status code of 204
    And the movie is deleted from the database
    And the roles are deleted from the database
