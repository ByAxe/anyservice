package com.anyservice.service.validators;

import com.anyservice.dto.user.UserDetailedNew;
import com.anyservice.entity.UserEntity;
import com.anyservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserValidator {

    private final UserRepository userRepository;

    @Value("${password.length.min}")
    private int passwordMinLength;

    @Value("${password.length.max}")
    private int passwordMaxLength;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Map<String, Object> validateCreation(UserDetailedNew user) {
        Map<String, Object> errors = new HashMap<>();


        String userName = user.getUserName();

        UserEntity userFoundByUserName = userRepository.findFirstByUserName(userName);

        if (userFoundByUserName != null) {
            errors.put("userName", "User with such userName already exists");
        }

        String password = user.getPassword();

        if (password == null) {
            errors.put("password", "Password should not be empty");
        } else {
            if (password.length() < passwordMinLength) {
                errors.put("password_length", "Password must have at least " + passwordMinLength + " characters.");
            } else if (password.length() > passwordMaxLength) {
                errors.put("password_length", "Password must not be longer that " + passwordMaxLength);
            }

            for (char ch : password.toCharArray()) {
                if (!Character.isLetter(ch) && !Character.isDigit(ch)) {
                    errors.put("password_content", "Password can only contain letters and numbers.");
                }
            }
        }

        return errors;
    }
}
