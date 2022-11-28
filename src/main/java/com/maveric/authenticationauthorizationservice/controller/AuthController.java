package com.maveric.authenticationauthorizationservice.controller;

import com.maveric.authenticationauthorizationservice.dto.*;
import com.maveric.authenticationauthorizationservice.exceptions.AccountCreationFailedException;
import com.maveric.authenticationauthorizationservice.exceptions.InvalidCredentialsException;
import com.maveric.authenticationauthorizationservice.feignconsumer.UserServiceConsumer;
import com.maveric.authenticationauthorizationservice.model.UserPrincipal;
import com.maveric.authenticationauthorizationservice.service.UserService;
import com.maveric.authenticationauthorizationservice.util.JwtUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    UserServiceConsumer userServiceConsumer;

    /* Checks user credentials for login and returns JWT token along with user information*/
    @PostMapping("auth/login")
    public ResponseEntity<AuthResponseDto> authLogin(@Valid @RequestBody AuthRequestDto authRequestDto) {
        log.info("API call for login validation");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDto.getEmail(), authRequestDto.getPassword())
            );
        }
        catch (BadCredentialsException e) {
            log.error("Incorrect username or password");
            throw new InvalidCredentialsException("Incorrect username or password");
        }
        final UserPrincipal userPrincipal = (UserPrincipal)userService
                .loadUserByUsername(authRequestDto.getEmail());

        final String jwt = jwtTokenUtil.generateToken(userPrincipal);
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setToken(jwt);
        authResponseDto.setUser(userPrincipal.getUser());
        log.info("Login successful, JWT token returned.");
        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }

    /* Creates user for login and returns JWT token along with user information*/
    @PostMapping("auth/signup")
    public ResponseEntity<AuthResponseDto> authSignUp(@Valid @RequestBody UserDetailsDto userDetailsDto) {
        log.info("API call for signup");
        ResponseEntity<UserDetailsDto> userDetailsDtoRespEntity = userServiceConsumer.createUser(userDetailsDto);
        UserDetailsDto userDetailsDtoResp = userDetailsDtoRespEntity.getBody();
        AuthResponseDto authResponseDto = new AuthResponseDto();
        if(userDetailsDtoResp!=null)
        {
            UserPrincipal userPrincipal = new UserPrincipal(userDetailsDtoResp);
            authResponseDto.setToken(jwtTokenUtil.generateToken(userPrincipal));
            authResponseDto.setUser(userPrincipal.getUser());
        }
        else {
            log.error("Account creation failed, Check User service!");
            throw new AccountCreationFailedException("Account creation failed");
        }
        log.info("Signup successful, JWT token returned.");
        return new ResponseEntity<>(authResponseDto, HttpStatus.CREATED);
    }

    /* Validates the token */
    @PostMapping("auth/validate")
    public ResponseEntity<GateWayResponseDto> validateToken(@Valid @RequestBody GateWayRequestDto gateWayRequestDto) {
        GateWayResponseDto resp = jwtTokenUtil.validateToken(gateWayRequestDto.getToken());
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    /* End point to test authentication through JWT token  */
    @GetMapping("/hello")
    public String sampleAPI(@RequestHeader String userEmail,@RequestHeader String userId) {
        return "Hello "+userEmail+"---"+userId+", \nWelcome to Maveric Bank!";
    }


}
