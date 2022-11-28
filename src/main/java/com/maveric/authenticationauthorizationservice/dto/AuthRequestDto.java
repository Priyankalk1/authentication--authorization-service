package com.maveric.authenticationauthorizationservice.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDto {

    @NotBlank(message = "Email Id is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    private String password;
}
