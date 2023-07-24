package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Enumerated;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import java.time.LocalDateTime;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User booker;
    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Item item;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "start_booking")
    private LocalDateTime start;
    @Column(name = "end_booking")
    private LocalDateTime end;

}
