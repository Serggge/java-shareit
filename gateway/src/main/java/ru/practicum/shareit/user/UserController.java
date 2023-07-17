package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.user.dto.UserDto;
import javax.validation.Valid;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto requestDto) {
        log.info("Creating user {}", requestDto);
        return userClient.addUser(requestDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUserInfo(@PathVariable("id") long userId,
                              @RequestBody UserDto requestDto) {
        log.info("Change user info {}, userId={}", requestDto, userId);
        return userClient.update(userId, requestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> returnUserById(@PathVariable("id") long userId) {
        log.info("Get user by ID={}", userId);
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> returnUsers() {
        log.info("Get all users");
        return userClient.getUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> remove(@PathVariable("id") long userId) {
        log.info("Deleting user by ID={}", userId);
        return userClient.deleteById(userId);
    }

}
