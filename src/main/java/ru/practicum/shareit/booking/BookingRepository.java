package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId,
                                                                           LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, Status status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId,
                                                                           LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long userId, Status status);

    List<Booking> findAllByItemId(long itemId);

    @Query(value = "select b.id from bookings as b " +
            "join users as u on u.id = b.user_id " +
            "join items as it on it.id = b.item_id " +
            "where it.id = ?1 and u.id = ?2 " +
            "and status = 'APPROVED' " +
            "and start_booking < NOW() " +
            "limit 1", nativeQuery = true)
    Optional<Long> findSuccessfulUserBooking(long itemId, long bookerId);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as u " +
            "join b.item as it " +
            "where it.id = ?1 " +
            "and (b.start > ?2 and b.start < ?3 " +
            "or b.end>?2 and b.end<?3 " +
            "or b.start < ?2 and b.end > ?3)")
    Optional<Booking> hasBooking(long itemId, LocalDateTime start, LocalDateTime end);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as u " +
            "join b.item as it " +
            "where it.id in ?1")
    List<Booking> findAllByItemIdAndOwnerId(Iterable<Long> ids);

}
