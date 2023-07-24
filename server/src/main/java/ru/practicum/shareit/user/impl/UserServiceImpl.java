package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailExistingException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor__ = @Autowired)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User add(User user) {
        user = userRepository.save(user);
        log.info("Создан новый пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        checkUserAlreadyRegistered(user);
        user = updateFields(user);
        user = userRepository.save(user);
        log.info("Пользователь обновлён: {}", user);
        return user;
    }

    @Override
    public User getById(long userId) {
        log.debug("Запрос на получение пользователя по id: " + userId);
        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь с id=%d не найден", userId)));
    }

    @Override
    public List<User> getAll() {
        log.debug("Запрос на получение всех пользователей");
        return userRepository.findAll();
    }

    @Override
    public void deleteById(long userId) {
        checkUserExistence(userId);
        userRepository.deleteById(userId);
        log.info("Удалён пользователь id: " + userId);
    }

    @Override
    public void checkUserExistence(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }
    }

    private void checkUserAlreadyRegistered(User user) {
        if (user.getEmail() == null) {
            return;
        }
        Optional<User> registeredUser = userRepository.findByEmailContainingIgnoreCase(user.getEmail());
        if (registeredUser.isPresent() && !registeredUser.get().getId().equals(user.getId())) {
            throw new EmailExistingException(
                    String.format("Пользователь с email: %s уже зарегистрирован", user.getEmail()));
        }
    }

    private User updateFields(User user) {
        User savedUser = userRepository.findById(user.getId()).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь с id=%d не найден", user.getId())));
        String name = user.getName() == null ? savedUser.getName() : user.getName();
        String email = user.getEmail() == null ? savedUser.getEmail() : user.getEmail();
        return User.builder()
                .id(user.getId())
                .name(name)
                .email(email)
                .build();
    }

}
