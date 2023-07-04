package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookingService bookingService;
    static BookingDto bookingDto;
    static ItemDto itemDto;
    static UserDto userDto;

    @BeforeAll
    static void beforeAll() {
        bookingDto = new BookingDto();
        itemDto = new ItemDto();
        userDto = new UserDto();
    }

    @BeforeEach
    void setUp() {
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(bookingDto.getStart().plusDays(1));
        bookingDto.setItemId(2L);

        itemDto.setId(2L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Простая дрель");
        itemDto.setAvailable(Boolean.TRUE);

        userDto.setId(3L);
        userDto.setName("user");
        userDto.setEmail("user@ya.ru");
    }

    @AfterEach
    void tearDown() {
        bookingDto = new BookingDto();
        itemDto = new ItemDto();
        userDto = new UserDto();
    }

    @Test
    @SneakyThrows
    void create() {
        when(bookingService.addNew(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));

        verify(bookingService, times(1)).addNew(userDto.getId(), bookingDto);
    }

    @Test
    void approve() {
    }

    @Test
    void returnById() {
    }

    @Test
    void returnUserBookings() {
    }

    @Test
    void returnItemsBookings() {
    }
}