package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Value;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder
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
