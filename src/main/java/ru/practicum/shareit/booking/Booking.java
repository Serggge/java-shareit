package ru.practicum.shareit.booking;

import lombok.Data;
import org.hibernate.annotations.Immutable;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import javax.persistence.*;

@Entity
@Immutable
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;
    @Enumerated(EnumType.STRING)
    private Status status;
}
