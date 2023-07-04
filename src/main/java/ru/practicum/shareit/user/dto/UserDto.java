package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Setter
@Getter
public class UserDto {

    long id;
    @NotBlank
    String name;
    @NotNull
    @Email
    String email;

}
