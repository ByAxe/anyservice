 [+] ...deleted ~19 tasks
- [+] Implement converters
- [+] Register them into configuration
- [+] Implement findFirstByUserName method
- [+] Add password hashing on creation of a user
- [+] Add message sources
- [+] Catch exception in Controller
- [+] Implement "create" in Service layer
- [+] Implement "create" in Controller
- [+] Fix Brief-Detailed confilicts
- [+] Remove not used messages from i18n
- [+] Implement validation of update and create operations
- [+] Implement "update" in Service layer
- [+] Implement "update" in Controller
- [+] Recreate Database from "script.sql"
- [+] Set up basic tests with TestNG
- [+] Cover basic UserController operations with tests
- [+] Make all the tests pass
- [+] Cover all the validation scenarios with tests
- [+] Cover hash Function with tests
- [+] Cover create and update corner cases with tests

    - [~] Add Spring Security to application
        - [+] Add "country", "address", "role", "state", "passwordUpdateDate" to sql
        - [+] Add countries dictionary and some values to it
        - [+] Add new fields to Entity, DTO, Converters
        - [+] Add changePassword method
        - [+] Remove password updating from update method (@DynamicUpdate should make it now)
        - [+] Add Spring Security classes for JWT support
        - [+] Add new fields to test_sql
        - [+] Add comparison of hashes on Log In
        - [+] Create SecurityController for logins and working with JWT
        - [+] Make the project compiliable
        - [+] Make all the tests pass
        - [+] Cover case when role || state is null when passed Front -> DB and vise versa
        - [+] Cover validatePasswordForChange method with tests
        - [ ] Cover changePassword method  with tests
        - [ ] Make sure via tests, that password is not updating by any of update operations
        - [ ] Cover SecurityController with tests
        - [ ] Make all the tests pass
        - [ ] Merge spring-security branch into master

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
