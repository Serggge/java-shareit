package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemService itemService;
    ItemDto itemDto;
    ItemOwnerDto itemOwnerDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("ItemName");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(Boolean.TRUE);

        itemOwnerDto = new ItemOwnerDto();
        itemOwnerDto.setId(itemDto.getId());
        itemOwnerDto.setName(itemDto.getName());
        itemOwnerDto.setDescription(itemDto.getDescription());
        itemOwnerDto.setAvailable(itemDto.getAvailable());
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    @SneakyThrows
    void create() {
        long userId = 1L;
        when(itemService.add(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(Boolean.TRUE), Boolean.class));

        verify(itemService, times(1)).add(userId, itemDto);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    @SneakyThrows
    void changeInfo() {
        long userId = 1L;
        long itemId = itemDto.getId();
        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(Boolean.TRUE), Boolean.class));

        verify(itemService, times(1)).update(userId, itemId, itemDto);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    @SneakyThrows
    void returnItemById() {
        long userId = 1L;
        long itemId = itemDto.getId();
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemOwnerDto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(Boolean.TRUE), Boolean.class));

        verify(itemService, times(1)).getById(userId, itemId);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    @SneakyThrows
    void returnUserItems() {
        long userId = 1L;
        String from = "0";
        String size = "2";
        ItemOwnerDto firstDto = new ItemOwnerDto();
        ItemOwnerDto secondDto = new ItemOwnerDto();
        firstDto.setId(1);
        secondDto.setId(2);
        List<ItemOwnerDto> dtos = List.of(firstDto, secondDto);
        when(itemService.getItemsByUserId(anyLong(), anyInt(), anyInt())).thenReturn(dtos);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", from)
                        .param("size", size)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)));

        verify(itemService, times(1)).getItemsByUserId(userId,
                Integer.parseInt(from), Integer.parseInt(size));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    @SneakyThrows
    void returnByQuery() {
        String text = "";
        String from = "0";
        String size = "2";
        ItemDto secondDto = new ItemDto();
        secondDto.setId(2);
        List<ItemDto> dtos = List.of(itemDto, secondDto);
        when(itemService.getByQuery(anyString(), anyInt(), anyInt())).thenReturn(dtos);

        mockMvc.perform(get("/items/search")
                .param("text", text)
                .param("from", from)
                .param("size", size)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder((int) itemDto.getId(), 2)));

        verify(itemService, times(1)).getByQuery(text,
                Integer.parseInt(from), Integer.parseInt(size));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    @SneakyThrows
    void createComment() {
        long userId = 1L;
        long itemId = itemDto.getId();
        CommentDto commentDto = new CommentDto(1L, "some text", "author name",
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated()
                        .truncatedTo(ChronoUnit.SECONDS).toString())));

        verify(itemService, times(1)).addComment(userId, itemId, commentDto);
        verifyNoMoreInteractions(itemService);
    }
}