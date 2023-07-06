package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Transactional
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {"db.name=test"})
@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    static UserRepository userRepository;
    @Autowired
    static ItemRepository itemRepository;
    Booking booking;
    static User owner;
    static Item item;
    static Pageable pageable = PageRequest.of(0,2);

    @BeforeAll
    static void beforeAll() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@email.com");
        userRepository.save(owner);
        item = new Item();
        item.setName("Item Name");
        item.setDescription("Item description");
        item.setAvailable(Boolean.TRUE);
        item.setOwner(owner);
        itemRepository.save(item);
    }


    @BeforeEach
    void setUp() {
        booking = new Booking();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@email.com");
        booker = userRepository.save(booker);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.of(2023,1,1,12,0,0));
        booking.setEnd(LocalDateTime.of(2023,1,2,12,0,0));
        bookingRepository.save(booking);

        Page<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), pageable);

        assertThat(bookings.get().count(), is(1));
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc() {
    }

    @Test
    void findAllByBookerIdAndEndBeforeOrderByStartDesc() {
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc() {
    }

    @Test
    void findAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
    }

    @Test
    void findAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
    }

    @Test
    void findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByStartDesc() {
    }

    @Test
    void findAllByItemId() {
    }

    @Test
    void findSuccessfulUserBooking() {
    }

    @Test
    void hasBooking() {
    }

    @Test
    void findAllByItemIdAndOwnerId() {
    }
}