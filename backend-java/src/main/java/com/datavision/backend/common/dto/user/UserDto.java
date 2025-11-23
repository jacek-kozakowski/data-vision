package com.datavision.backend.common.dto.user;

import com.datavision.backend.user.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    @NotNull
    private Long id;
    @NotBlank
    private String username;

    public UserDto(User user){
        this.id = user.getId();
        this.username = user.getUsername();
    }
}
