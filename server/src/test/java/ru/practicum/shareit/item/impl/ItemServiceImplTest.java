package ru.practicum.shareit.item.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.hamcrest.MatcherAssert.assertThat;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BookingNotAvailableException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    ItemServiceImpl itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    UserService userService;
    @Mock
    ItemRequestRepository itemRequestRepository;
    ItemDto itemDto;
    Item item;
    ItemRequest itemRequest;
    User user;
    Comment firstComment;
    Comment secondComment;
    Booking booking;
    ItemMapper itemMapper = new ItemMapperImpl();
    CommentMapper commentMapper = new CommentMapperImpl();
    static Random random = new Random();

    @BeforeEach
    void setUp() {
        itemService.setItemMapper(itemMapper);
        itemService.setCommentMapper(commentMapper);

        itemDto = new ItemDto();
        itemDto.setName("Item Name");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(Boolean.TRUE);
        long requestId = random.nextInt(32);
        itemDto.setRequestId(requestId);

        itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("item request description");
        itemRequest.setCreated(LocalDateTime.of(2023, 5, 5, 12, 0, 30));

        user = new User();
        long userId = random.nextInt(32);
        user.setId(userId);
        user.setName("User name");
        user.setEmail("user@email.com");

        item = new Item();
        item.setId((long) random.nextInt(32));
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);

        firstComment = new Comment();
        firstComment.setId((long) random.nextInt(32));
        firstComment.setText("Text of first comment");
        firstComment.setItem(item);
        firstComment.setAuthor(new User());

        secondComment = new Comment();
        secondComment.setId(firstComment.getId() + 1);
        secondComment.setText("Text of second comment");
        secondComment.setItem(item);
        secondComment.setAuthor(new User());

        booking = new Booking();
        booking.setId((long) random.nextInt(32));
        booking.setStatus(Status.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(booking.getStart().plusHours(1));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void add_whenItemRequestIdNotPresent_thenReturnItemRequestNotFoundException() {
        long itemId = random.nextInt(32);
        when(userService.getById(anyLong())).thenReturn(user);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class, () ->
                itemService.add(user.getId(), itemDto));

        assertThat(exception.getMessage(), equalTo("Не найден запрос с id=" + itemRequest.getId()));
        verify(userService).getById(user.getId());
        verify(itemRequestRepository).findById(itemRequest.getId());
        verify(itemRepository, never()).save(any());
        verifyNoMoreInteractions(userService, itemRequestRepository);

    }

    @Test
    void add() {
        long itemId = random.nextInt(32);
        when(userService.getById(anyLong())).thenReturn(user);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocationOnMock -> {
            Item argItem = invocationOnMock.getArgument(0, Item.class);
            argItem.setId(itemId);
            return argItem;
        });

        ItemDto returnedDto = itemService.add(user.getId(), itemDto);

        assertThat(returnedDto, notNullValue());
        assertThat(returnedDto.getId(), equalTo(itemId));
        verify(userService).getById(user.getId());
        verify(itemRequestRepository).findById(itemRequest.getId());
        verify(itemRepository).save(any());
        verifyNoMoreInteractions(userService, itemRequestRepository, itemRepository);
    }

    @Test
    void update_whenDtoHasNewValues_thenUpdateEntityFields() {
        long itemId = random.nextInt(32);
        when(userService.getById(anyLong())).thenReturn(user);
        when(itemRepository.findOwnerIdByItemId(anyLong())).thenReturn(Optional.of(user.getId()));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(Item.builder()
                .id(itemId)
                .name("old name")
                .description("old description")
                .available(!itemDto.getAvailable())
                .build()));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocationOnMock -> {
            Item argItem = invocationOnMock.getArgument(0, Item.class);
            argItem.setId(itemId);
            return argItem;
        });

        ItemDto returnedDto = itemService.update(user.getId(), itemId, itemDto);

        assertThat(returnedDto, notNullValue());
        assertThat(returnedDto.getId(), equalTo(itemId));
        assertThat(returnedDto.getName(), equalTo(itemDto.getName()));
        assertThat(returnedDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(returnedDto.getAvailable(), equalTo(itemDto.getAvailable()));
        verify(userService).getById(user.getId());
        verify(itemRepository).findOwnerIdByItemId(itemId);
        verify(itemRepository).save(any());
        verifyNoMoreInteractions(userService, itemRepository, itemRepository);
    }

    @Test
    void update_whenDtoHasNullValues_thenDoNotUpdateEntityFields() {
        itemDto.setName(null);
        itemDto.setDescription(null);
        itemDto.setAvailable(null);
        long itemId = random.nextInt(32);
        when(userService.getById(anyLong())).thenReturn(user);
        when(itemRepository.findOwnerIdByItemId(anyLong())).thenReturn(Optional.of(user.getId()));
        Item oldEntity = Item.builder()
                .id(itemId)
                .name("old name")
                .description("old description")
                .available(Boolean.TRUE)
                .build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(oldEntity));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocationOnMock -> {
            Item argItem = invocationOnMock.getArgument(0, Item.class);
            argItem.setId(itemId);
            return argItem;
        });

        ItemDto returnedDto = itemService.update(user.getId(), itemId, itemDto);

        assertThat(returnedDto, notNullValue());
        assertThat(returnedDto.getId(), equalTo(itemId));
        assertThat(returnedDto.getName(), equalTo(oldEntity.getName()));
        assertThat(returnedDto.getDescription(), equalTo(oldEntity.getDescription()));
        assertThat(returnedDto.getAvailable(), equalTo(oldEntity.getAvailable()));
        verify(userService).getById(user.getId());
        verify(itemRepository).findOwnerIdByItemId(itemId);
        verify(itemRepository).save(any());
        verifyNoMoreInteractions(userService, itemRepository, itemRepository);
    }

    @Test
    void update_whenUserIdIsNotOwnerId_thenThrowItemNotFoundException() {
        long itemId = random.nextInt(32);
        when(userService.getById(anyLong())).thenReturn(user);
        when(itemRepository.findOwnerIdByItemId(anyLong())).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () ->
                itemService.update(user.getId(), itemId, itemDto));

        assertThat(exception.getMessage(), equalTo(
                String.format("Пользователь id=%d не является владельцем вещи id=%d", user.getId(), itemId)));
        verify(userService).getById(user.getId());
        verify(itemRepository).findOwnerIdByItemId(itemId);
        verify(itemRepository, never()).save(any());
        verifyNoMoreInteractions(userService, itemRepository, itemRepository);
    }

    @Test
    void update_whenItemIdNotExist_thenThrowItemNotFoundException() {
        long itemId = random.nextInt(32);
        when(userService.getById(anyLong())).thenReturn(user);
        when(itemRepository.findOwnerIdByItemId(anyLong())).thenReturn(Optional.of(user.getId()));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () ->
                itemService.update(user.getId(), itemId, itemDto));

        assertThat(exception.getMessage(), equalTo(String.format("Вещь с id=%d не найдена", itemId)));
        verify(userService).getById(user.getId());
        verify(itemRepository).findOwnerIdByItemId(itemId);
        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).save(any());
        verifyNoMoreInteractions(userService, itemRepository, itemRepository);
    }

    @Test
    void getById_whenItemNotExist_thenThrowItemNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () ->
                itemService.getById(user.getId(), item.getId()));

        assertThat(exception.getMessage(), equalTo(String.format("Вещь с id=%d не найдена", item.getId())));
        verify(itemRepository).findById(item.getId());
    }

    @Test
    void getById_whenUserIdNotOwner_thenReturnDtoWithoutBookings() {
        long itemId = random.nextInt(32);
        Item item = Item.builder()
                .id(itemId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(firstComment, secondComment));

        ItemOwnerDto returnedDto = itemService.getById(user.getId() + 1, itemId);

        assertThat(returnedDto, notNullValue());
        assertThat(returnedDto.getId(), equalTo(itemId));
        assertThat(returnedDto, hasProperty("comments"));
        assertThat(returnedDto.getComments(), hasSize(2));
        assertThat(returnedDto.getComments(), allOf(
                hasItem(commentMapper.mapToDto(firstComment)),
                hasItem(commentMapper.mapToDto(secondComment))));
        assertThat(returnedDto, hasProperty("lastBooking"));
        assertThat(returnedDto, hasProperty("nextBooking"));
        assertThat(returnedDto.getLastBooking(), nullValue());
        assertThat(returnedDto.getNextBooking(), nullValue());
        verify(itemRepository).findById(itemId);
        verify(commentRepository).findByItemId(itemId);
        verifyNoMoreInteractions(itemRepository, commentRepository);
    }

    @Test
    void getById_whenIsOwnerId_thenReturnDtoWithBookings() {
        long itemId = random.nextInt(32);
        Item item = Item.builder()
                .id(itemId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(firstComment, secondComment));
        Booking lastBooking = new Booking();
        lastBooking.setStart(LocalDateTime.now().minusDays(1));
        lastBooking.setEnd(lastBooking.getStart().plusHours(1));
        lastBooking.setItem(item);
        lastBooking.setBooker(new User());
        lastBooking.setStatus(Status.APPROVED);
        Booking nextBooking = new Booking();
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(nextBooking.getStart().plusHours(1));
        nextBooking.setItem(item);
        nextBooking.setBooker(new User());
        nextBooking.setStatus(Status.WAITING);
        when(bookingRepository.findAllByItemId(anyLong())).thenReturn(List.of(lastBooking, nextBooking));

        ItemOwnerDto returnedDto = itemService.getById(user.getId(), itemId);

        assertThat(returnedDto, notNullValue());
        assertThat(returnedDto.getId(), equalTo(itemId));
        assertThat(returnedDto, hasProperty("comments"));
        assertThat(returnedDto.getComments(), hasSize(2));
        assertThat(returnedDto.getComments(), allOf(
                hasItem(commentMapper.mapToDto(firstComment)),
                hasItem(commentMapper.mapToDto(secondComment))));
        assertThat(returnedDto, hasProperty("lastBooking"));
        assertThat(returnedDto, hasProperty("nextBooking"));
        assertThat(returnedDto.getLastBooking(), notNullValue());
        assertThat(returnedDto.getNextBooking(), notNullValue());
        verify(itemRepository).findById(itemId);
        verify(commentRepository).findByItemId(itemId);
        verify(bookingRepository).findAllByItemId(itemId);
        verifyNoMoreInteractions(itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void deleteById_whenItemExist_thenDeleteItem() {
        when(itemRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        doNothing().when(itemRepository).deleteById(anyLong());

        itemService.deleteById(itemDto.getId());

        verify(itemRepository).existsById(itemDto.getId());
        verify(itemRepository).deleteById(itemDto.getId());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getItemsByUserId() {
        doNothing().when(userService).checkUserExistence(anyLong());
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRepository.findAllByItemId(anyCollection())).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemId(anyCollection())).thenReturn(List.of(firstComment, secondComment));

        List<ItemOwnerDto> result = itemService.getItemsByUserId(user.getId(), 0, 2);

        assertThat(result, notNullValue());
        assertThat(result, hasSize(1));
        verify(userService).checkUserExistence(user.getId());
        verify(itemRepository).findAllByOwnerId(user.getId(), PageRequest.of(0, 2));
        verify(bookingRepository).findAllByItemId(Set.of(item.getId()));
        verify(commentRepository).findAllByItemId(Set.of(item.getId()));
        verifyNoMoreInteractions(userService, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void getItemsByUserId_whenItemNotExistThenThrowItemNotFoundException() {
        when(itemRepository.existsById(anyLong())).thenReturn(Boolean.FALSE);

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () ->
                itemService.deleteById(itemDto.getId()));

        assertThat(exception.getMessage(), equalTo(String.format("Вещь с id=%d не найдена", itemDto.getId())));
        verify(itemRepository).existsById(itemDto.getId());
        verify(itemRepository, never()).deleteById(anyLong());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getByQuery_whenQueryNotBlank_thenReturnItemListSizeEqualTwo() {
        String query = "mOlOt".toLowerCase();
        List<Item> items = new ArrayList<>();
        Item firstItem = Item.builder().id(1L).name("Molotok").build();
        items.add(firstItem);
        Item secondItem = Item.builder().id(2L).name("Mol").description("molotok").build();
        items.add(secondItem);
        when(itemRepository.findAllByQuery(anyString(), any())).thenReturn(new PageImpl<>(items));

        List<ItemDto> result = itemService.getByQuery(query, 0, 2);

        assertThat(result, allOf(notNullValue(), hasSize(2)));
        assertThat(result, allOf(hasItem(itemMapper.toDto(firstItem)), hasItem(itemMapper.toDto(secondItem))));
        verify(itemRepository).findAllByQuery(query.toLowerCase(), PageRequest.of(0, 2));
    }

    @Test
    void addComment_whenItemNotExist_thenThrowItemNotFoundException() {
        when(userService.getById(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () ->
                itemService.addComment(user.getId(), item.getId(), new CommentDto()));

        assertThat(exception.getMessage(), equalTo(String.format("Вещь с id=%d не найдена", item.getId())));
        verify(userService).getById(user.getId());
        verify(itemRepository).findById(item.getId());
        verifyNoMoreInteractions(userService, itemRepository);
    }

    @Test
    void addComment_whenUserIsNotBooking_thenThrowBookingNotAvailableException() {
        when(userService.getById(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findSuccessfulUserBooking(anyLong(), anyLong())).thenReturn(Optional.empty());

        BookingNotAvailableException exception = assertThrows(BookingNotAvailableException.class, () ->
                itemService.addComment(user.getId(), item.getId(), new CommentDto()));

        assertThat(exception.getMessage(), equalTo(
                String.format("Пользователь id=%d не пользовался вещью id=%d", user.getId(), item.getId())));
        verify(userService).getById(user.getId());
        verify(itemRepository).findById(item.getId());
        verify(bookingRepository).findSuccessfulUserBooking(item.getId(), user.getId());
        verifyNoMoreInteractions(userService, itemRepository);
    }

    @Test
    void addComment() {
        long commentId = random.nextInt();
        when(userService.getById(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findSuccessfulUserBooking(anyLong(), anyLong())).thenReturn(Optional.of(1L));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocationOnMock -> {
            Comment argComment = invocationOnMock.getArgument(0, Comment.class);
            argComment.setId(commentId);
            return argComment;
        });
        CommentDto commentDto = new CommentDto();
        String text = "Text of comment";
        commentDto.setText(text);
        commentDto.setAuthorName(user.getName());

        CommentDto returnedComment = itemService.addComment(user.getId(), item.getId(), commentDto);

        assertThat(returnedComment, notNullValue());
        assertThat(returnedComment.getId(), equalTo(commentId));
        verify(userService).getById(user.getId());
        verify(itemRepository).findById(item.getId());
        verify(bookingRepository).findSuccessfulUserBooking(item.getId(), user.getId());
        verify(commentRepository).save(any());
        verifyNoMoreInteractions(userService, itemRepository, bookingRepository, commentRepository);
    }
}