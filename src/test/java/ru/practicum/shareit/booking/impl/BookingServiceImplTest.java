package ru.practicum.shareit.booking.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BookingDateTimeException;
import ru.practicum.shareit.exception.BookingNotAvailableException;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserAccessException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.impl.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.impl.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserService userService;
    BookingMapper bookingMapper = new BookingMapperImpl(new UserMapperImpl(), new ItemMapperImpl());
    Random random = new Random();
    BookingDto bookingDto;
    Booking booking;
    Item item;
    User owner;
    User booker;
    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        bookingService.setBookingMapper(bookingMapper);

        owner = new User();
        owner.setId((long) random.nextInt(32));
        owner.setName("User name");
        owner.setEmail("user@email.com");

        itemRequest = new ItemRequest();
        itemRequest.setId((long) random.nextInt(32));
        itemRequest.setDescription("Description for item request");
        itemRequest.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        itemRequest.setUser(owner);

        item = new Item();
        item.setId((long) random.nextInt(32));
        item.setName("Item name");
        item.setDescription("Item description");
        item.setAvailable(Boolean.TRUE);
        item.setOwner(owner);
        item.setItemRequest(itemRequest);

        bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS).plusDays(1));
        bookingDto.setEnd(bookingDto.getStart().plusDays(1));
        bookingDto.setItemId(item.getId());

        booking = new Booking();
        booking.setId((long) random.nextInt(32));
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booker = new User(owner.getId() + 1, "Booker name", "booker@email.com");
        booking.setBooker(booker);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addNew_whenStartOrEndIsNull_thenThrowBookingDateTimeException() {
        bookingDto.setStart(null);
        bookingDto.setEnd(null);

        BookingDateTimeException exception = assertThrows(BookingDateTimeException.class, () ->
                bookingService.addNew(owner.getId(), bookingDto));

        assertThat(exception.getMessage(), equalTo("Некорректные даты бронирования"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addNew_whenItemIdNotExist_thenThrowItemNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () ->
                bookingService.addNew(owner.getId(), bookingDto));

        assertThat(exception.getMessage(), equalTo(String.format("Вещь с id=%d не найдена", bookingDto.getItemId())));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addNew_whenBookingNotAvailable_thenThrowBookingNotAvailableException() {
        item.setAvailable(Boolean.FALSE);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        BookingNotAvailableException exception = assertThrows(BookingNotAvailableException.class, () ->
                bookingService.addNew(owner.getId(), bookingDto));

        assertThat(exception.getMessage(), equalTo("вещь недоступна для бронирования"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addNew_whenItemAlreadyBooked_thenThrowBookingNotAvailableException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByDate(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));

        BookingNotAvailableException exception = assertThrows(BookingNotAvailableException.class, () ->
                bookingService.addNew(owner.getId(), bookingDto));

        assertThat(exception.getMessage(), equalTo("вещь недоступна для бронирования"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addNew_whenBookerIdEqualsOwnerId_thenThrowBookingNotFoundException() {
        booking.setBooker(owner);
        when(userService.getById(anyLong())).thenReturn(owner);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByDate(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class, () ->
                bookingService.addNew(owner.getId(), bookingDto));

        assertThat(exception.getMessage(), equalTo("Владелец не может бронировать свою вещь"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addNew_whenAddNew_thenReturnSavedBooking() {
        long bookingId = random.nextInt(32);
        when(userService.getById(anyLong())).thenReturn(booker);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByDate(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocationOnMock -> {
            Booking argBooking = invocationOnMock.getArgument(0, Booking.class);
            argBooking.setId(bookingId);
            return argBooking;
        });

        BookingDto savedDto = bookingService.addNew(booker.getId(), bookingDto);

        booking.setId(bookingId);
        booking.setStatus(Status.WAITING);
        assertThat(savedDto, notNullValue());
        assertThat(savedDto.getId(), equalTo(bookingId));
        assertThat(savedDto.getStatus(), equalTo(Status.WAITING));
        assertThat(savedDto, equalTo(bookingMapper.mapToDto(booking)));
        verify(userService).getById(booker.getId());
        verify(itemRepository).findById(item.getId());
        verify(bookingRepository).findBookingByDate(item.getId(), bookingDto.getStart(), bookingDto.getEnd());
        verify(bookingRepository).save(any());
    }

    @Test
    void approve_whenBookingIdNotExist_thenThrowBookingNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class, () ->
                bookingService.approve(booker.getId(), booking.getId(), "APPROVED"));

        assertThat(exception.getMessage(), equalTo("Бронирование не найдено"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approve_whenOwnerIdNotEqualUserId_thenThrowUserNotFoundException() {
        when(userService.getById(anyLong())).thenReturn(booker);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                bookingService.approve(booker.getId(), booking.getId(), "APPROVED"));

        assertThat(exception.getMessage(), equalTo("Вещь не пренадлежит пользователю"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approve_whenBookingStatusIsNotWaiting_thenThrowIllegalArgumentException() {
        booking.setStatus(Status.APPROVED);
        when(userService.getById(anyLong())).thenReturn(owner);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bookingService.approve(owner.getId(), booking.getId(), "APPROVED"));

        assertThat(exception.getMessage(), equalTo("Статус был подтверждён владельцем ранее"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approve_whenRequestHasApproveQueryEqualTrue_thenSaveEntityWithStatusApprove() {
        booking.setStatus(Status.WAITING);
        when(userService.getById(anyLong())).thenReturn(owner);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocationOnMock ->
                invocationOnMock.getArgument(0, Booking.class));

        BookingDto savedDto = bookingService.approve(owner.getId(), booking.getId(), "true");

        assertThat(savedDto.getStatus(), equalTo(Status.APPROVED));
        verify(userService).getById(owner.getId());
        verify(bookingRepository).findById(booking.getId());
        verify(bookingRepository).save(any());
    }

    @Test
    void approve_whenRequestHasApproveQueryEqualFalse_thenSaveEntityWithStatusRejected() {
        booking.setStatus(Status.WAITING);
        when(userService.getById(anyLong())).thenReturn(owner);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocationOnMock ->
                invocationOnMock.getArgument(0, Booking.class));

        BookingDto savedDto = bookingService.approve(owner.getId(), booking.getId(), "false");

        assertThat(savedDto.getStatus(), equalTo(Status.REJECTED));
        verify(userService).getById(owner.getId());
        verify(bookingRepository).findById(booking.getId());
        verify(bookingRepository).save(any());
    }


    @Test
    void getById_whenBookingIdNotExist_thenThrowBookingNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class, () ->
                bookingService.getById(booking.getId(), booker.getId()));

        assertThat(exception.getMessage(), equalTo("Бронирование не найдено"));
    }

    @Test
    void getById_whenUserIdNotEqualsBookerIdOrOwnerId_thenThrowUserAccessException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        UserAccessException exception = assertThrows(UserAccessException.class, () ->
                bookingService.getById(booking.getId(), -1));

        assertThat(exception.getMessage(),
                equalTo("Пользователю запрещёно просматривать информацию по бронированию"));
    }

    @Test
    void getById_returnBookingDto() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto savedDto = bookingService.getById(booking.getId(), booker.getId());

        assertThat(savedDto, equalTo(bookingMapper.mapToDto(booking)));
        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    void getUserBookings_whenIncomingIncorrectState_thenThrowIllegalArgumentException() {
        String state = "state";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bookingService.getUserBookings(booker.getId(), state, 0, 2));

        assertThat(exception.getMessage(), equalTo("Unknown state: " + state));
    }

    @Test
    void getUserBookings_whenIncomingStateIsALL_thenReturnAllUserBookings() {
        String state = "ALL";
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getUserBookings(booker.getId(), state, 0, 2);

        assertThat(result, allOf(notNullValue(), hasSize(1)));
        assertThat(result, hasItem(bookingMapper.mapToDto(booking)));
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(booker.getId(), PageRequest.of(0, 2));
    }

    @Test
    void getUserBookings_whenIncomingStateIsPast_thenReturnPastUserBookings() {
        String state = "PAST";
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any())).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getUserBookings(booker.getId(), state, 0, 2);

        assertThat(result, allOf(notNullValue(), hasSize(1)));
        assertThat(result, hasItem(bookingMapper.mapToDto(booking)));
        verify(bookingRepository).findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any());
    }

    @Test
    void getUserBookings_whenIncomingStateIsFuture_thenReturnFutureUserBookings() {
        String state = "FUTURE";
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any())).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getUserBookings(booker.getId(), state, 0, 2);

        assertThat(result, allOf(notNullValue(), hasSize(1)));
        assertThat(result, hasItem(bookingMapper.mapToDto(booking)));
        verify(bookingRepository).findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any());
    }

    @Test
    void getUserBookings_whenIncomingStateIsCurrent_thenReturnCurrentUserBookings() {
        String state = "CURRENT";
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any())).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getUserBookings(booker.getId(), state, 0, 2);

        assertThat(result, allOf(notNullValue(), hasSize(1)));
        assertThat(result, hasItem(bookingMapper.mapToDto(booking)));
        verify(bookingRepository).findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any());
    }

    @Test
    void getUserBookings_whenIncomingStateIsWaiting_thenReturnWaitingUserBookings() {
        String state = "WAITING";
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any())).
                thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getUserBookings(booker.getId(), state, 0, 2);

        assertThat(result, allOf(notNullValue(), hasSize(1)));
        assertThat(result, hasItem(bookingMapper.mapToDto(booking)));
        verify(bookingRepository).findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.WAITING,
                PageRequest.of(0,2));
    }

    @Test
    void getUserBookings_whenIncomingStateIsRejected_thenReturnRejectedUserBookings() {
        String state = "REJECTED";
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any())).
                thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getUserBookings(booker.getId(), state, 0, 2);

        assertThat(result, allOf(notNullValue(), hasSize(1)));
        assertThat(result, hasItem(bookingMapper.mapToDto(booking)));
        verify(bookingRepository).findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.REJECTED,
                PageRequest.of(0,2));
    }

    @Test
    void getItemsBookings_whenIncomingIncorrectState_thenThrowIllegalArgumentException() {
        String state = "state";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bookingService.getItemsBookings(owner.getId(), state, 0, 2));

        assertThat(exception.getMessage(), equalTo("Unknown state: " + state));
    }

    @Test
    void getItemsBookings_whenIncomingStateIsALL_thenReturnAllUserBookings() {
        String state = "ALL";
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getItemsBookings(owner.getId(), state, 0, 2);

        assertThat(result, allOf(notNullValue(), hasSize(1)));
        assertThat(result, hasItem(bookingMapper.mapToDto(booking)));
        verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(owner.getId(), PageRequest.of(0, 2));
    }

    @Test
    void getItemsBookings_whenIncomingStateIsPast_thenReturnPastUserBookings() {
        String state = "PAST";
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any())).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getItemsBookings(owner.getId(), state, 0, 2);

        assertThat(result, allOf(notNullValue(), hasSize(1)));
        assertThat(result, hasItem(bookingMapper.mapToDto(booking)));
        verify(bookingRepository).findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any());
    }

    @Test
    void getItemsBookings_whenIncomingStateIsFuture_thenReturnFutureUserBookings() {
        String state = "FUTURE";
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any())).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getItemsBookings(owner.getId(), state, 0, 2);

        assertThat(result, allOf(notNullValue(), hasSize(1)));
        assertThat(result, hasItem(bookingMapper.mapToDto(booking)));
        verify(bookingRepository).findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any());
    }

    @Test
    void getItemsBookings_whenIncomingStateIsCurrent_thenReturnCurrentUserBookings() {
        String state = "CURRENT";
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any())).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getItemsBookings(owner.getId(), state, 0, 2);

        assertThat(result, allOf(notNullValue(), hasSize(1)));
        assertThat(result, hasItem(bookingMapper.mapToDto(booking)));
        verify(bookingRepository).findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any());
    }

    @Test
    void getItemsBookings_whenIncomingStateIsWaiting_thenReturnWaitingUserBookings() {
        String state = "WAITING";
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any())).
                thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getItemsBookings(owner.getId(), state, 0, 2);

        assertThat(result, allOf(notNullValue(), hasSize(1)));
        assertThat(result, hasItem(bookingMapper.mapToDto(booking)));
        verify(bookingRepository).findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.WAITING,
                PageRequest.of(0,2));
    }

    @Test
    void getItemsBookings_whenIncomingStateIsRejected_thenReturnRejectedUserBookings() {
        String state = "REJECTED";
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any())).
                thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getItemsBookings(owner.getId(), state, 0, 2);

        assertThat(result, allOf(notNullValue(), hasSize(1)));
        assertThat(result, hasItem(bookingMapper.mapToDto(booking)));
        verify(bookingRepository).findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.REJECTED,
                PageRequest.of(0,2));
    }

}