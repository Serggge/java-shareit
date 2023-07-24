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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import java.time.LocalDateTime;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookingClient bookingClient;
    static BookingDto bookingDto;
    ResponseEntity<Object> response;

    @BeforeAll
    static void beforeAll() {
        bookingDto = new BookingDto();
    }

    @BeforeEach
    void setUp() {
        bookingDto.setStart(LocalDateTime.of(2024, 7, 18, 20, 10, 30));
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
        response = new ResponseEntity<>(bookingDto, HttpStatus.CREATED);
        when(bookingClient.addNew(anyLong(), any(BookingDto.class))).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.start",is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())));

        verify(bookingClient, times(1)).addNew(userId, bookingDto);
        verifyNoMoreInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void approve() {
        String status = "APPROVED";
        long bookingId = 2L;
        long userId = 1L;
        response = new ResponseEntity<>(bookingDto, HttpStatus.OK);
        when(bookingClient.approve(anyLong(), anyLong(), anyString())).thenReturn(response);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", status)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())));

        verify(bookingClient, times(1)).approve(userId, bookingId, status);
        verifyNoMoreInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void returnById() {
        long bookingId = 2L;
        long userId = 1L;
        response = new ResponseEntity<>(bookingDto, HttpStatus.OK);
        when(bookingClient.getBooking(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())));

        verify(bookingClient, times(1)).getBooking(userId, bookingId);
        verifyNoMoreInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void returnUserBookings() {
        long userId = 1L;
        response = new ResponseEntity<>(bookingDto, HttpStatus.OK);
        String state = "ALL";
        BookingState bookingState = BookingState.valueOf(state);
        when(bookingClient.getBookings(anyLong(), any(), anyInt(), anyInt())).thenReturn(response);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(bookingClient, times(1)).getBookings(userId, bookingState, 0, 2);
        verifyNoMoreInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void returnItemsBookings() {
        long userId = 1L;
        response = new ResponseEntity<>(bookingDto, HttpStatus.OK);
        String state = "ALL";
        BookingState bookingState = BookingState.valueOf(state);
        when(bookingClient.getItemsBookings(anyLong(), any(), anyInt(), anyInt())).thenReturn(response);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(bookingClient, times(1)).getItemsBookings(userId, bookingState, 0, 2);
        verifyNoMoreInteractions(bookingClient);
    }

}