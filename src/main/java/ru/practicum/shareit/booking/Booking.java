package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bookings")
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ElementCollection
    @CollectionTable(name = "booking_days", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "date_of_booking")
    private Set<LocalDate> bookingDays = new HashSet<>();

}
