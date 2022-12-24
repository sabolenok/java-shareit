package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemRequestServiceImpl itemRequestService;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    private ItemRequest itemRequest;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("test description");

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1);
        itemRequestDto.setDescription("test description");
    }

    @Test
    void createNewItemRequestTest() throws Exception {
        when(itemRequestService.addNewItemRequest(anyInt(), any()))
                .thenReturn(itemRequest);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(itemRequestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['description']").value(itemRequestDto.getDescription()));
    }

    @Test
    void getOneItemRequestTest() throws Exception {
        when(itemRequestService.getById(anyInt(), anyInt()))
                .thenReturn(itemRequest);
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(itemRequestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['description']").value(itemRequestDto.getDescription()));
    }

    @Test
    void getAllItemRequestsTest() throws Exception {
        when(itemRequestService.getAll(anyInt()))
                .thenReturn(List.of(itemRequest));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemRequestDto.getDescription()));
    }

    @Test
    void getAllItemRequestsWithPaginationTest() throws Exception {
        when(itemRequestService.getAllWithPagination(anyInt(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }
}
