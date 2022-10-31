package controller;

import com.maveric.authenticationauthorizationservice.dto.*;
import com.maveric.authenticationauthorizationservice.exceptions.AccountCreationFailedException;
import com.maveric.authenticationauthorizationservice.exceptions.InvalidCredentialsException;
import com.maveric.authenticationauthorizationservice.feignconsumer.UserServiceConsumer;
import com.maveric.authenticationauthorizationservice.model.UserPrincipal;
import com.maveric.authenticationauthorizationservice.service.UserService;
import com.maveric.authenticationauthorizationservice.util.JwtUtil;
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
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDto.getEmail(), authRequestDto.getPassword())
            );
        }
        catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Incorrect username or password");
        }
        final UserPrincipal userPrincipal = (UserPrincipal)userService
                .loadUserByUsername(authRequestDto.getEmail());

        final String jwt = jwtTokenUtil.generateToken(userPrincipal);
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setToken(jwt);
        authResponseDto.setUser(userPrincipal.getUser());
        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }

    /* Creates user for login and returns JWT token along with user information*/
    @PostMapping("auth/signup")
    public ResponseEntity<AuthResponseDto> authSignUp(@Valid @RequestBody UserDetailsDto userDetailsDto) {
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
            throw new AccountCreationFailedException("Account creation failed");
        }
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
    public String sampleAPI() {
        return "Hello Maveric!";
    }


}
