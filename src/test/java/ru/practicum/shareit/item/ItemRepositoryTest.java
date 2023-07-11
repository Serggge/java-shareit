package ru.practicum.shareit.item;

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
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
class ItemRepositoryTest {

    @Autowired
    TestEntityManager em;
    @Autowired
    ItemRepository itemRepository;
    Item item;
    static User owner;
    static ItemRequest itemRequest;
    static Pageable page = PageRequest.of(0, 2);

    @BeforeAll
    static void beforeAll(@Autowired UserRepository userRepository,
                          @Autowired ItemRequestRepository itemRequestRepository) {
        owner = new User();
        owner.setName("Owner name");
        owner.setEmail("owner@email.com");
        userRepository.save(owner);

        User requester = new User();
        requester.setName("Requester name");
        requester.setEmail("requester@mail.com");
        userRepository.save(requester);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Item request description");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setUser(requester);
        itemRequestRepository.save(itemRequest);
    }

    @AfterAll
    static void afterAll(@Autowired UserRepository userRepository,
                         @Autowired ItemRequestRepository itemRequestRepository,
                         @Autowired ItemRepository itemRepository) {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setName("Item name");
        item.setDescription("Item description");
        item.setAvailable(Boolean.TRUE);
        item.setOwner(owner);
        item.setItemRequest(itemRequest);
    }

    @Test
    void verifyBootstrappingByPersistingItem() {
        assertThat(item.getId()).isNull();

        em.persist(item);

        assertThat(item.getId()).isNotNull();
    }
    @Test
    void verifyRepositoryByPersistingItem() {
        assertThat(item.getId()).isNull();

        itemRepository.save(item);

        assertThat(item.getId()).isNotNull();
    }

    @Test
    void verifyRepositoryReturnItem() {
        itemRepository.save(item);
        long itemId = item.getId();

        Optional<Item> returned = itemRepository.findById(itemId);

        assertThat(returned).isNotNull()
                .isPresent()
                .get()
                .isEqualTo(item);
    }

    @Test
    void findAllByQuery_whenNameContainsQuery_thenReturnItem() {
        itemRepository.save(item);
        String partOfName = item.getName().substring(1, item.getName().length() - 1).toLowerCase();
        System.out.println(partOfName);

        Page<Item> result = itemRepository.findAllByQuery(partOfName, page);

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(item);
    }

    @Test
    void findAllByQuery_whenDescriptionContainsQuery_thenReturnItem() {
        itemRepository.save(item);
        String partOfName = item.getDescription().substring(1, item.getName().length() - 1).toLowerCase();
        System.out.println(partOfName);

        Page<Item> result = itemRepository.findAllByQuery(partOfName, page);

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(item);
    }

    @Test
    void findAllByOwnerId() {
        itemRepository.save(item);
        long ownerId = item.getOwner().getId();

        Page<Item> result = itemRepository.findAllByOwnerId(ownerId, page);

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(item);
    }

    @Test
    void findOwnerIdByItemId() {
        itemRepository.save(item);
        long itemId = item.getId();

        Optional<Long> foundOwnerId = itemRepository.findOwnerIdByItemId(itemId);

        assertThat(foundOwnerId).isNotNull()
                .isPresent()
                .get()
                .isEqualTo(item.getOwner().getId());
    }

    @Test
    void findAllByItemRequestId() {
        itemRepository.save(item);
        long itemRequestId = item.getItemRequest().getId();

        List<Item> result = itemRepository.findAllByItemRequestId(itemRequestId);

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(item);
    }

    @Test
    void findAllByCollectionOfItemRequestId() {
        itemRepository.save(item);

        List<Item> result = itemRepository.findAllByItemRequestId(Set.of(item.getItemRequest().getId()));

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(item);
    }
}