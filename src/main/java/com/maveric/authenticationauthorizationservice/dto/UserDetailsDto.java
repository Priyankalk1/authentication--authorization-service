package com.maveric.authenticationauthorizationservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.maveric.authenticationauthorizationservice.enums.Type;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDto {

    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;
    private String email;
    private String address;
    private String dateOfBirth;
    private Type gender;
    private String _id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;
}
