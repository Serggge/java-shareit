package ru.practicum.shareit.user.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {

    long id;
    @NotBlank
    String name;
    @NotNull
    @Email
    String email;

}
