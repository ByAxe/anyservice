package com.anyservice.web.controller;

import com.anyservice.core.enums.UserRole;
import com.anyservice.dto.user.UserDetailed;
import com.anyservice.service.api.IUserService;
import com.anyservice.service.user.UserHolder;
import com.anyservice.web.security.JwtUtil;
import com.anyservice.web.security.dto.InfiniteToken;
import com.anyservice.web.security.dto.Login;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@Log4j2
@RestController
@RequestMapping("/api/v1/user")
public class SecurityController {

    private final IUserService userService;
    private final JwtUtil jwtUtil;

    private final UserHolder holder;

    public SecurityController(JwtUtil jwtUtil, IUserService userService,
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

        return new ResponseEntity<>(null, httpHeaders, OK);
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

    /**
     * Allowed only for authenticated users
     *
     * @return role of a user that's made a request
     */
    @GetMapping("/authenticated")
    public ResponseEntity<?> checkIfAuthenticated() {
        UserRole role = holder.getUser().getRole();

        int ordinal = role.ordinal();

        return new ResponseEntity<>(ordinal, OK);
    }
}
