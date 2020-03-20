package com.anyservice.web.controller;

import com.anyservice.dto.user.UserDetailed;
import com.anyservice.service.user.UserHolder;
import com.anyservice.service.user.UserService;
import com.anyservice.web.security.JwtUtil;
import com.anyservice.web.security.dto.InfiniteToken;
import com.anyservice.web.security.dto.Login;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class SecurityController {
    private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    private final UserHolder holder;

    public SecurityController(JwtUtil jwtUtil, UserService userService,
                              UserHolder holder) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.holder = holder;
    }

    /**
     * Token generation for this user
     *
     * @param request all the data, to identify the user
     * @return token for user
     */
    @PostMapping("/login")
    public String login(@RequestBody Login request) {
        UserDetailed user = userService.findUserForLogin(request.getUserName(), request.getPassword());

        return jwtUtil.generateToken(user);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        UserDetailed userDetailed = holder.getUser();

        HttpHeaders httpHeaders = new HttpHeaders();

        // Does not make anything for now

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/refresh")
    public String refreshToken() {
        return jwtUtil.refreshToken();
    }

    //    @ApiOperation(value = "FOO", hidden = true)
    @PostMapping("/generate/infinite/token")
    public String generateInfiniteToken(@RequestBody InfiniteToken infiniteToken) {
        UserDetailed user = userService.findByUserName(infiniteToken.getUserName());
        return jwtUtil.generateInfiniteToken(user, infiniteToken.getTtl());
    }
}
