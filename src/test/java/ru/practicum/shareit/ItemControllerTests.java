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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemServiceImpl itemService;

    @MockBean
    ItemRepository itemRepository;

    private Item item;

    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1);
        item.setDescription("test item description");
        item.setName("test item");
        item.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setDescription("test item description");
        itemDto.setName("test item");
        itemDto.setAvailable(true);
    }

    @Test
    void createNewItemTest() throws Exception {
        when(itemService.addNewItem(anyInt(), any()))
                .thenReturn(item);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['description']").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['name']").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['available']").value(itemDto.getAvailable()));
    }

    @Test
    void getOneItemTest() throws Exception {
        when(itemService.getById(anyInt(), anyInt()))
                .thenReturn(item);
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['description']").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['name']").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['available']").value(itemDto.getAvailable()));
    }

    @Test
    void getAllItemsTest() throws Exception {
        when(itemService.getAll(anyInt()))
                .thenReturn(List.of(item));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void getAllItemsWithPaginationTest() throws Exception {
        when(itemService.getAllWithPagination(anyInt(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(item)));

        mockMvc.perform(get("/items/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllItemsWithoutPaginationParamsTest() throws Exception {
        when(itemService.getAllWithPagination(anyInt(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(itemService.getAll(anyInt()))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/items/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void updateItemTest() throws Exception {
        item.setDescription("new test item description");
        item.setName("test_item_new");
        when(itemService.put(anyInt(), anyInt(), any()))
                .thenReturn(item);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['description']").value("new test item description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['name']").value("test_item_new"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['available']").value(itemDto.getAvailable()));
    }

    @Test
    void searchItemsTest() throws Exception {
        when(itemService.search(anyInt(), anyString()))
                .thenReturn(List.of(item));
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void searchItemsWithPaginationTest() throws Exception {
        when(itemService.searchWithPagination(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(item)));

        mockMvc.perform(get("/items/search/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "test")
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void searchItemsWithoutPaginationParamsTest() throws Exception {
        when(itemService.searchWithPagination(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(itemService.search(anyInt(), anyString()))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/items/search/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void addCommentTest() throws Exception {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("test comment");

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1);
        commentDto.setText("test comment");

        when(itemService.addComment(anyInt(), anyInt(), any()))
                .thenReturn(comment);
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(commentDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['text']").value(commentDto.getText()));
    }
}
