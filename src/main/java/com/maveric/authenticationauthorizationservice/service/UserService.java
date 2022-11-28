package com.maveric.authenticationauthorizationservice.service;

import com.maveric.authenticationauthorizationservice.dto.UserDetailsDto;
import com.maveric.authenticationauthorizationservice.exceptions.UserNotFoundException;
import com.maveric.authenticationauthorizationservice.feignconsumer.UserServiceConsumer;
import com.maveric.authenticationauthorizationservice.model.UserPrincipal;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.maveric.authenticationauthorizationservice.constants.Constants.USER_NOT_FOUND_MESSAGE;

@Service
public class UserService implements UserDetailsService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserServiceConsumer userServiceConsumer;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Check email against user details in user service");
        ResponseEntity<UserDetailsDto> userDetailsDto = userServiceConsumer.getUserDetailsByEmail(email);
        if(userDetailsDto.getBody()!=null)
        {
            log.info("User found in DB to authenticate");
            return new UserPrincipal(userDetailsDto.getBody());
        }
        else {
            log.error("User email not found in DB");
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE+email);
        }
    }
}
