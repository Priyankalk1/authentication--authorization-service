package com.maveric.authenticationauthorizationservice.feignconsumer;

import com.maveric.authenticationauthorizationservice.dto.UserDetailsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name="user-service")
@Service
public interface UserServiceConsumer {
    @GetMapping("api/v1/users/getUserByEmail/{emailId}")
    public ResponseEntity<UserDetailsDto> getUserDetailsByEmail(@PathVariable String emailId);

    @PostMapping("api/v1/users")
    public ResponseEntity<UserDetailsDto> createUser(@RequestBody UserDetailsDto userDetailsDto);

}
