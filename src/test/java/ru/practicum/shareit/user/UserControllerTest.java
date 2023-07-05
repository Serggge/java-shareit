package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.impl.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    UserService userService;
    @InjectMocks
    UserController userController;
    ObjectMapper objectMapper = new ObjectMapper();
    MockMvc mvc;
    UserDto userDto;
    User user;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
        UserMapper userMapper = new UserMapperImpl();
        ReflectionTestUtils.setField(userController, "userMapper", userMapper);

        userDto = new UserDto();
        userDto.setName("UserName");
        userDto.setEmail("user@user.com");

        user = userMapper.toEntity(userDto);
        user.setId(1L);
    }

    @Test
    @SneakyThrows
    void create() {
        when(userService.add(any(User.class))).thenReturn(user);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userService, times(1)).add(any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @SneakyThrows
    void updateInfo() {
        when(userService.update(any(User.class))).thenReturn(user);

        mvc.perform(patch("/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userService, times(1)).update(any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @SneakyThrows
    void returnById() {
        when(userService.getById(anyLong())).thenReturn(user);

        mvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userService, times(1)).getById(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    @SneakyThrows
    void returnAll() {
        User secondUser = new User(user.getId() + 1, "Second user", "second@user.com");
        when(userService.getAll()).thenReturn(List.of(user, secondUser));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(user.getId().intValue(),
                        secondUser.getId().intValue())))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(user.getName(), secondUser.getName())))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder(user.getEmail(), secondUser.getEmail())));

        verify(userService, times(1)).getAll();
        verifyNoMoreInteractions(userService);
    }

    @Test
    @SneakyThrows
    void remove() {
        doNothing().when(userService).deleteById(anyLong());

        mvc.perform(delete("/users/{id}", user.getId()))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(user.getId());
    }
}