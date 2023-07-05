package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestService itemRequestService;
    ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("description for item request");
        itemRequestDto.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @SneakyThrows
    void create() {
        long userId = 1L;
        when(itemRequestService.add(anyLong(), any(ItemRequestDto.class))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequestDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated()
                        .truncatedTo(ChronoUnit.MILLIS).toString())));

        verify(itemRequestService, times(1)).add(userId, itemRequestDto);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    @SneakyThrows
    void returnUserRequests() {
        long userId = 1L;
        ItemRequestDto secondDto = new ItemRequestDto();
        secondDto.setId(itemRequestDto.getId() + 1);
        when(itemRequestService.getOwn(anyLong())).thenReturn(List.of(itemRequestDto, secondDto));

        mockMvc.perform(get("/requests")
                .header("X-Sharer-User-Id", userId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder((int) itemRequestDto.getId(),
                        (int) secondDto.getId())));

        verify(itemRequestService, times(1)).getOwn(userId);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    @SneakyThrows
    void returnAll() {
        long userId = 1L;
        String from = "0";
        String size = "2";
        ItemRequestDto secondDto = new ItemRequestDto();
        secondDto.setId(itemRequestDto.getId() + 1);
        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto, secondDto));

        mockMvc.perform(get("/requests/all")
                .header("X-Sharer-User-Id", userId)
                .param("from", from)
                .param("size", size)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder((int) itemRequestDto.getId(),
                        (int) secondDto.getId())));

        verify(itemRequestService, times(1)).getAll(userId,
                Integer.parseInt(from), Integer.parseInt(size));
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    @SneakyThrows
    void returnById() {
        long userId = 1L;
        long requestId = itemRequestDto.getId();
        when(itemRequestService.getById(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated()
                        .truncatedTo(ChronoUnit.SECONDS).toString())));

        verify(itemRequestService, times(1)).getById(userId, requestId);
        verifyNoMoreInteractions(itemRequestService);
    }
}