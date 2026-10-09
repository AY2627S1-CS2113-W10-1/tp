# Developer Guide

## Acknowledgements

{list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

## Design & implementation

### Trip date configuration

`Parser` appoints `trip` input to `TripCommandParser`, which checks the `set`
subcommand and required `s/` and `e/` parameters using `ArgumentParser`. 
`DateUtil` handles strict calendar parsing into the required format. 
The resulting `TripSetCommand` extends `Command`
and updates the existing `Trip` held by `AppState`.

`Trip` keep tracks of its dates and validates both before mutation, throwing the
existing checked `TripJioException` for missing dates or an invalid range. This
protects the model even when called without the CLI parser. The command overrides
`execute` and `isMutating`, allowing the main loop to execute and save it through
the common `Command` abstraction. No separate exception hierarchy is needed.

`Storage` designates the `[trip]` section to `Trip`. Trip dates are formatted
as `start=DD-MM-YYYY` and `end=DD-MM-YYYY`. An incomplete loaded pair remains unset;
a reversed loaded pair is cleared and reported through the existing load warning.
The status and statistics commands are not implemented in this increment.

{Describe the design and implementation of the product. Use UML diagrams and short code snippets where applicable.}


## Product scope
### Target user profile

{Describe the target user profile}

### Value proposition

{Describe the value proposition: what problem does it solve?}

## User Stories

|Version| As a ... | I want to ... | So that I can ...|
|--------|----------|---------------|------------------|
|v1.0|new user|see usage instructions|refer to them when I forget how to use the application|
|v2.0|user|find a to-do item by name|locate a to-do without having to go through the entire list|

## Non-Functional Requirements

{Give non-functional requirements}

## Glossary

* *glossary item* - Definition

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}
