# AnyService marketplace backend

## Tasks list
- [x] ...deleted 61 tasks
- [+] Add user verification logic
    - [+] Create verification method
    - [+] Add messages for email to i18n
    - [+] Create email sender
    - [+] Make all validation methods return Map<String, Object> and not void (unification)
    - [+] Cover with tests verifyUser method
    - [+] Covert with tests email sending
    - [+] Check isVerified & not allow to set legal status etc. if it's false
    - [+] Write integration test for whole verification chain
- [ ] Replace logging calls with lombok annotation @Log4j2
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
