package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserPatchDto;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }


    @Test
    void createUserSuccess() throws Exception {

        UserRequestDto request = new UserRequestDto("dima", 25, "asd@mail.ru");
        UserResponseDto response = new UserResponseDto(
                1L, "dima", 25, "asd@mail.ru",
                LocalDateTime.of(2026, 9, 25, 21, 4));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.age").value(response.getAge()))
                .andExpect(jsonPath("$.email").value(response.getEmail()))
                .andExpect(jsonPath("$.createdAt").exists());


        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("asd@mail.ru");
    }

    @Test
    void createUserWithDuplicateEmailReturnsConflict() throws Exception {

        User user = new User(null, "dima", 25, "asd@mail.ru",
                LocalDateTime.now());

        userRepository.save(user);

        UserRequestDto request = new UserRequestDto("sasha", 30, "asd@mail.ru");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo(user.getName());
        assertThat(users.get(0).getAge()).isEqualTo(user.getAge());
        assertThat(users.get(0).getEmail()).isEqualTo(user.getEmail());

    }

    @Test
    void findAll() throws Exception {

        User user1 = new User(null, "dima", 25, "asd@mail.ru", LocalDateTime.now());
        User user2 = new User(null, "sasha", 30, "www@mail.ru", LocalDateTime.now());
        userRepository.saveAll(List.of(user1, user2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("dima"))
                .andExpect(jsonPath("$[0].age").value(25))
                .andExpect(jsonPath("$[0].email").value("asd@mail.ru"))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[1].name").value("sasha"))
                .andExpect(jsonPath("$[1].age").value(30))
                .andExpect(jsonPath("$[1].email").value("www@mail.ru"))
                .andExpect(jsonPath("$[1].createdAt").exists());


        assertThat(userRepository.findAll()).hasSize(2);
    }

    @Test
    void findUserById() throws Exception {
        User user = new User(null, "dima", 25, "asd@mail.ru", LocalDateTime.now());

        User savedUser = userRepository.save(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", savedUser.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value(savedUser.getName()))
                .andExpect(jsonPath("$.age").value(savedUser.getAge()))
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()))
                .andExpect(jsonPath("$.createdAt").exists());

        assertThat(userRepository.findById(savedUser.getId())).contains(savedUser);
    }

    @Test
    void updateUser() throws Exception {
        User savedUser = userRepository.save(
                new User(null, "dima", 25, "asd@mail.ru", LocalDateTime.now()));

        UserRequestDto request = new UserRequestDto("sasha", 35, "www@mail.ru");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("sasha"))
                .andExpect(jsonPath("$.age").value(35))
                .andExpect(jsonPath("$.email").value("www@mail.ru"))
                .andExpect(jsonPath("$.createdAt").exists());


        User updated = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("sasha");
        assertThat(updated.getAge()).isEqualTo(35);
        assertThat(updated.getEmail()).isEqualTo("www@mail.ru");
    }

    @Test
    void updateUserNotFound() throws Exception {
        UserRequestDto request = new UserRequestDto("sasha", 35, "www@mail.ru");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    void updateUserPatchName() throws Exception {
        User savedUser = userRepository.save(
                new User(null, "dima", 25, "asd@mail.ru", LocalDateTime.now()));

        UserPatchDto request = new UserPatchDto("sasha", null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("sasha"))            // изменилось
                .andExpect(jsonPath("$.age").value(25))                  // не затёрто
                .andExpect(jsonPath("$.email").value("asd@mail.ru"))     // не затёрто
                .andExpect(jsonPath("$.createdAt").exists());

        User updated = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("sasha");
        assertThat(updated.getAge()).isEqualTo(25);
        assertThat(updated.getEmail()).isEqualTo("asd@mail.ru");
    }

    @Test
    void updateUserPatchAge() throws Exception {
        User savedUser = userRepository.save(
                new User(null, "dima", 25, "asd@mail.ru", LocalDateTime.now()));

        UserPatchDto request = new UserPatchDto(null, 35, null);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value("dima"))             // не затёрто
                .andExpect(jsonPath("$.age").value(35))                  // изменилось
                .andExpect(jsonPath("$.email").value("asd@mail.ru"));    // не затёрто

        User updated = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("dima");
        assertThat(updated.getAge()).isEqualTo(35);
        assertThat(updated.getEmail()).isEqualTo("asd@mail.ru");
    }

    @Test
    void updateUserPatchEmail() throws Exception {
        User savedUser = userRepository.save(
                new User(null, "dima", 25, "asd@mail.ru", LocalDateTime.now()));

        UserPatchDto request = new UserPatchDto(null, null, "www@mail.ru");

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value("dima"))             // не затёрто
                .andExpect(jsonPath("$.age").value(25))                  // не затёрто
                .andExpect(jsonPath("$.email").value("www@mail.ru"));    // изменилось

        User updated = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("dima");
        assertThat(updated.getAge()).isEqualTo(25);
        assertThat(updated.getEmail()).isEqualTo("www@mail.ru");
    }

    @Test
    void updateUserPatchAllFields() throws Exception {
        User savedUser = userRepository.save(
                new User(null, "dima", 25, "asd@mail.ru", LocalDateTime.now()));

        UserPatchDto request = new UserPatchDto("sasha", 35, "www@mail.ru");

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value("sasha"))
                .andExpect(jsonPath("$.age").value(35))
                .andExpect(jsonPath("$.email").value("www@mail.ru"));

        User updated = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("sasha");
        assertThat(updated.getAge()).isEqualTo(35);
        assertThat(updated.getEmail()).isEqualTo("www@mail.ru");
    }

    @Test
    void updateUserPatchNotFound() throws Exception {
        UserPatchDto request = new UserPatchDto("sasha", null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    void findUserByIdNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", 999L))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteUserByIdNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", 999L))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteUserById() throws Exception {

        User user = new User(null, "dima", 25, "asd@mail.ru", LocalDateTime.now());

        User savedUser = userRepository.save(user);

        assertThat(userRepository.findById(savedUser.getId())).isPresent();

        mockMvc.perform(MockMvcRequestBuilders.
                        delete("/api/users/{id}", savedUser.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(content().string(""));


        assertThat(userRepository.findById(savedUser.getId())).isEmpty();

    }
}