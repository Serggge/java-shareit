package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(long userId);

    List<User> findAll();

    void deleteById(long userId);

    boolean existsById(long userId);

    Optional<Long> findUserIdByEmail(String email);

}
