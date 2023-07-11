package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookingService bookingService;
    static BookingDto bookingDto;

    @BeforeAll
    static void beforeAll() {
        bookingDto = new BookingDto();
    }

    @BeforeEach
    void setUp() {
        bookingDto.setStart(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        bookingDto.setEnd(bookingDto.getStart().plusDays(1));
        bookingDto.setItemId(2L);
    }

    @AfterEach
    void tearDown() {
        bookingDto = new BookingDto();
    }

    @Test
    @SneakyThrows
    void create() {
        long userId = 1L;
        when(bookingService.addNew(anyLong(), any(BookingDto.class))).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class));

        verify(bookingService, times(1)).addNew(userId, bookingDto);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void approve() {
        String status = "APPROVED";
        long userId = 1L;
        bookingDto.setStatus(Status.valueOf(status));
        when(bookingService.approve(anyLong(), anyLong(), anyString())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", status)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.status", is(status)));

        verify(bookingService, times(1)).approve(userId, bookingDto.getId(), status);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void returnById() {
        long userId = 1L;
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                .header("X-Sharer-User-Id", userId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart()
                        .truncatedTo(ChronoUnit.SECONDS).toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd()
                        .truncatedTo(ChronoUnit.SECONDS).toString())))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class));

        verify(bookingService, times(1)).getById(bookingDto.getId(), userId);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void returnUserBookings() {
        long userId = 1L;
        String state = "ALL";
        List<BookingDto> dtos = List.of(new BookingDto(), new BookingDto());
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(dtos);

        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", userId)
                .param("state", state)
                .param("from", "0")
                .param("size", "2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(bookingService, times(1)).getUserBookings(userId, state, 0, 2);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @SneakyThrows
    void returnItemsBookings() {
        long userId = 1L;
        String state = "ALL";
        List<BookingDto> dtos = List.of(new BookingDto(), new BookingDto());
        when(bookingService.getItemsBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(dtos);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(bookingService, times(1)).getItemsBookings(userId, state, 0, 2);
        verifyNoMoreInteractions(bookingService);
    }
}