package ru.practicum.shareit.user.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static long count;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User save(@Valid User user) {
        if (user.getId() == 0) {
            user = user.withId(++count);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> findAll() {
        return users.values()
                .stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(long userId) {
        users.remove(userId);
    }

    @Override
    public boolean existsById(long userId) {
        return users.containsKey(userId);
    }

    @Override
    public Optional<Long> findUserIdByEmail(String email) {
        return users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .map(User::getId)
                .findAny();
    }

}
