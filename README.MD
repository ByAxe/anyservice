- [x] ...deleted ~19 tasks
- [x] Implement converters
- [x] Register them into configuration
- [x] Implement findFirstByUserName method
- [x] Add password hashing on creation of a user
- [x] Add message sources
- [x] Catch exception in Controller
- [x] Implement "create" in Service layer
- [x] Implement "create" in Controller
- [x] Fix Brief-Detailed confilicts
- [x] Remove not used messages from i18n
- [x] Implement validation of update and create operations
- [x] Implement "update" in Service layer
- [x] Implement "update" in Controller
- [x] Recreate Database from "script.sql"
- [x] Set up basic tests with TestNG
- [x] Cover basic UserController operations with tests
- [x] Make all the tests pass
- [x] Cover all the validation scenarios with tests
- [x] Cover hash Function with tests
- [x] Cover create and update corner cases with tests

    - [~] Add Spring Security to application
        - [x] Add "country", "address", "role", "state", "passwordUpdateDate" to sql
        - [x] Add countries dictionary and some values to it
        - [x] Add new fields to Entity, DTO, Converters
        - [x] Add changePassword method
        - [x] Remove password updating from update method (@DynamicUpdate should make it now)
        - [x] Add Spring Security classes for JWT support
        - [x] Add new fields to test_sql
        - [x] Add comparison of hashes on Log In
        - [x] Create SecurityController for logins and working with JWT
        - [x] Make the project compiliable
        - [x] Make all the tests pass
        - [x] Cover case when role || state is null when passed Front -> DB and vise versa
        - [x] Cover validatePasswordForChange method with tests
        - [x] Clean up and make reusable integration tests' abstraction
        - [ ] Cover changePassword method  with tests
        - [ ] Add resultMatches to ICRUDOperations methods to add flexibility to it
        - [ ] Make sure via tests, that password is not updating by any of update operations
        - [ ] Cover SecurityController with tests
        - [ ] Make all the tests pass
        - [ ] Merge spring-security branch into master

- [ ] Add "trials" for password changing
- [ ] Create IUserService interface and replace all calls to UserService by IUserService calls
- [ ] Add comments to script.sql & test_script.sql
- [ ] Deploy basic application on Google Cloud
- [ ] Add amazon s3 to application
- [ ] Add countries to UserEntity and all the chain
- [ ] Implement file storage for Users
- [ ] Write documentation for each and every method\class
- [ ] Reach test coverage at least of 85%
- [ ] Replace all the hardcoded messages on one language to MessageSource messages
- [ ] Make the TODO in UserValidator
- [ ] Add function "Restore password"
- [ ] Add Swagger to application

___
**Explanations**
Task completed  =>  [+]
Task in process =>  [~]
Task created    =>  [ ]
