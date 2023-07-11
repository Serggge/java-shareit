package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
class UserRepositoryTest {

    @Autowired
    TestEntityManager em;
    @Autowired
    UserRepository userRepository;
    User user;

    @AfterAll
    static void afterAll(@Autowired UserRepository userRepository) {
        userRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("User name");
        user.setEmail("user@email.com");
    }

    @Test
    void verifyBootstrappingByPersistingUser() {
        assertThat(user.getId()).isNull();

        em.persist(user);

        assertThat(user.getId()).isNotNull();
    }

    @Test
    void verifyRepositoryByPersistingUser() {
        assertThat(user.getId()).isNull();

        userRepository.save(user);

        assertThat(user.getId()).isNotNull();
    }

    @Test
    void verifyRepositoryReturnUser() {
        userRepository.save(user);
        long userId = user.getId();

        Optional<User> returnedUser = userRepository.findById(userId);

        assertThat(returnedUser).isNotNull()
                .isPresent()
                .get()
                .isEqualTo(user);
    }

    @Test
    void findByEmailContainingIgnoreCase() {
        String email = user.getEmail().toUpperCase();
        userRepository.save(user);

        Optional<User> returnedUser = userRepository.findByEmailContainingIgnoreCase(email);

        assertThat(returnedUser).isNotNull()
                .isPresent()
                .get()
                .isEqualTo(user);
    }

}