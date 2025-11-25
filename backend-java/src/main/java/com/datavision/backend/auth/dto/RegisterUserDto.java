package com.datavision.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {
    @NotBlank
    @Size(min= 3, max= 25)
    private String username;
    @NotBlank
    @Size(min= 8)
    private String password;
}
