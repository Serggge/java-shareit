package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor__ = @Autowired)
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user = userService.add(user);
        return userMapper.toDto(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateInfo(@PathVariable("id") long userId,
                              @RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto)
                .withId(userId);
        user = userService.update(user);
        return userMapper.toDto(user);
    }

    @GetMapping("/{id}")
    public UserDto returnById(@PathVariable("id") long userId) {
        User user = userService.getById(userId);
        return userMapper.toDto(user);
    }

    @GetMapping
    public List<UserDto> returnAll() {
        return userService.getAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable("id") long userId) {
        userService.deleteById(userId);
    }

}
