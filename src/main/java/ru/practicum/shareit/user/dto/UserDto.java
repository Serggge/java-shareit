package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Value
@Builder
@Jacksonized
public class UserDto {

    long id;
    @NotBlank
    String name;
    @NotNull
    @Email
    String email;

}
