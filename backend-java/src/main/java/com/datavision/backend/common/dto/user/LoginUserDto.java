package com.datavision.backend.common.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserDto {
    @NotBlank
    private String username;
    @NotBlank
    @Size(min= 8)
    private String password;
}
