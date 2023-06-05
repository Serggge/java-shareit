package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

@Value
@Builder
@Jacksonized
public class User {

    long id;
    @NotBlank
    String name;
    @NotNull
    @Email
    String email;

    public User withId(long id) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

}
