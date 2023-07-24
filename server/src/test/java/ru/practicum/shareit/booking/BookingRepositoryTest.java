package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
class BookingRepositoryTest {

    @Autowired
    TestEntityManager em;
    @Autowired
    BookingRepository bookingRepository;
    Booking booking;
    static User booker;
    static Item item;
    static Pageable page = PageRequest.of(0, 2);

    @BeforeAll
    static void beforeAll(@Autowired UserRepository userRepository,
                          @Autowired ItemRepository itemRepository) {
        booker = new User();
        booker.setName("Booker name");
        booker.setEmail("booker@email.com");
        userRepository.save(booker);

        User owner = new User();
        owner.setName("Owner name");
        owner.setEmail("owner@mail.com");
        userRepository.save(owner);

        item = new Item();
        item.setName("Item name");
        item.setDescription("Item description");
        item.setAvailable(Boolean.TRUE);
        item.setOwner(owner);
        itemRepository.save(item);
    }

    @AfterAll
    static void afterAll(@Autowired UserRepository userRepository,
                         @Autowired BookingRepository bookingRepository,
                         @Autowired ItemRepository itemRepository) {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(booking.getStart().plusDays(1));
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);
    }

    @Test
    void verifyBootstrappingByPersistingBooking() {
        assertThat(booking.getId()).isNull();

        em.persist(booking);

        assertThat(booking.getId()).isNotNull();
    }

    @Test
    void verifyRepositoryByPersistingBooking() {
        assertThat(booking.getId()).isNull();

        bookingRepository.save(booking);

        assertThat(booking.getId()).isNotNull();
    }

    @Test
    void verifyRepositoryReturnBooking() {
        bookingRepository.save(booking);
        long bookingId = booking.getId();

        Optional<Booking> returned = bookingRepository.findById(bookingId);

        assertThat(returned).isNotNull()
                .isPresent()
                .get()
                .isEqualTo(booking);
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
        bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), page);

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(booking);
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(booker.getId(),
                LocalDateTime.now(), page);

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(booking);
    }

    @Test
    void findAllByBookerIdAndEndBeforeOrderByStartDesc() {
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(booker.getId(),
                LocalDateTime.now(), page);

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(booking);
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        LocalDateTime now = LocalDateTime.now();
        bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                booker.getId(), now, now, page);

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(booking);
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        Status status = Status.APPROVED;
        booking.setStatus(status);
        bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                booker.getId(), status, page);

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(booking);
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc() {
        bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(item.getOwner().getId(), page);

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(booking);
    }

    @Test
    void findAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        booking.setStart(now.plusDays(1));
        bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                item.getOwner().getId(), now, page);

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(booking);
    }

    @Test
    void findAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        booking.setEnd(now.minusDays(1));
        bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                item.getOwner().getId(), now, page);

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(booking);
    }

    @Test
    void findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        booking.setStart(now.minusDays(1));
        booking.setEnd(now.plusDays(1));
        bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                item.getOwner().getId(), now, now, page);

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(booking);
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByStartDesc() {
        Status status = Status.REJECTED;
        booking.setStatus(status);
        bookingRepository.save(booking);

        Page<Booking> result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                item.getOwner().getId(), status, page);

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(booking);
    }

    @Test
    void findAllByItemId() {
        bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findAllByItemId(item.getId());

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(booking);
    }

    @Test
    void findAllByCollectionItemId() {
        bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findAllByItemId(Set.of(item.getId()));

        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(booking);
    }

    @Test
    void findSuccessfulUserBooking() {
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);

        Optional<Long> result = bookingRepository.findSuccessfulUserBooking(item.getId(), booker.getId());

        assertThat(result).isNotNull()
                .isPresent()
                .get()
                .isEqualTo(booking.getId());
    }

    @Test
    void findBookingWithSameDate() {
        LocalDateTime now = LocalDateTime.now();
        booking.setStart(now.minusDays(1));
        booking.setEnd(now.plusDays(1));
        bookingRepository.save(booking);

        Optional<Booking> result = bookingRepository.findBookingWithSameDate(item.getId(), now, now.plusDays(2));

        assertThat(result).isNotNull()
                .isPresent()
                .get()
                .isEqualTo(booking);
    }

}