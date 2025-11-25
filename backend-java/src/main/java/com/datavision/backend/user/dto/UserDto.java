package com.datavision.backend.user.dto;

import com.datavision.backend.user.model.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;

    public UserDto(User user){
        this.id = user.getId();
        this.username = user.getUsername();
    }
}
