package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
class ItemRequestRepositoryTest {

    @Autowired
    TestEntityManager em;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    ItemRequest itemRequest;
    static User user;

    @BeforeAll
    static void setUpData(@Autowired UserRepository userRepository) {
        user = new User();
        user.setName("User name");
        user.setEmail("user@email.com");
        userRepository.save(user);
    }

    @AfterAll
    static void afterAll(@Autowired ItemRequestRepository itemRequestRepository,
                         @Autowired UserRepository userRepository) {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        itemRequest = new ItemRequest();
        itemRequest.setDescription("Description");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setUser(user);
    }

    @Test
    void verifyBootstrappingByPersistingItemRequest() {
        assertThat(itemRequest.getId()).isNull();

        em.persist(itemRequest);

        assertThat(itemRequest.getId()).isNotNull();
    }

    @Test
    void verifyRepositoryByPersistingItemRequest() {
        assertThat(itemRequest.getId()).isNull();

        itemRequestRepository.save(itemRequest);

        assertThat(itemRequest.getId()).isNotNull();
    }

    @Test
    void verifyRepositoryReturnItemRequest() {
        itemRequestRepository.save(itemRequest);
        long userId = itemRequest.getId();

        Optional<ItemRequest> returned = itemRequestRepository.findById(userId);

        assertThat(returned).isNotNull()
                .isPresent()
                .get()
                .isEqualTo(itemRequest);
    }

    @Test
    void findAllByUserIdOrderByCreatedDesc() {
        long userId = user.getId();
        itemRequestRepository.save(itemRequest);

        List<ItemRequest> result = itemRequestRepository.findAllByUserIdOrderByCreatedDesc(userId);

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(itemRequest);
    }

    @Test
    void findByUserIdNot_returnEmtyResult() {
        long userId = user.getId();
        itemRequestRepository.save(itemRequest);

        Page<ItemRequest> emptyResult = itemRequestRepository.findByUserIdNot(userId, PageRequest.of(0, 2));

        assertThat(emptyResult).isNotNull()
                .isEmpty();
    }

    @Test
    void findByUserIdNot_returnItemRequestList() {
        long diffUserId = user.getId() + 1;
        itemRequestRepository.save(itemRequest);

        Page<ItemRequest> notEmptyResult = itemRequestRepository
                .findByUserIdNot(diffUserId, PageRequest.of(0, 2));

        assertThat(notEmptyResult).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(itemRequest);
    }
}