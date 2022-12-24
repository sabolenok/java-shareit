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
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingServiceImpl bookingService;

    @MockBean
    BookingRepository bookingRepository;

    private Booking booking;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setId(1);
        booking.setStatus(BookingStatus.APPROVED);

        bookingDto = new BookingDto();
        bookingDto.setId(1);
        bookingDto.setStatus(BookingStatus.APPROVED);
    }

    @Test
    void createNewBookingTest() throws Exception {
        when(bookingService.addNewBooking(anyInt(), any()))
                .thenReturn(booking);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(bookingDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['status']").value(bookingDto.getStatus()));
    }

    @Test
    void getOneBookingTest() throws Exception {
        when(bookingService.getById(anyInt(), anyInt()))
                .thenReturn(booking);
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(bookingDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['status']").value(bookingDto.getStatus()));
    }

    @Test
    void updateBookingTest() throws Exception {
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingService.put(anyInt(), anyInt(), any()))
                .thenReturn(booking);
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.['id']").value(bookingDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['status']").value(BookingStatus.REJECTED));
    }

    @Test
    void getBookingsByUserTest() throws Exception {
        when(bookingService.getByUserId(anyInt(), anyString()))
                .thenReturn(List.of(booking));
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value(bookingDto.getStatus()));
    }

    @Test
    void getBookingsByUserWithPaginationTest() throws Exception {
        when(bookingService.getByUserIdWithPagination(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void getBookingsByOwnerTest() throws Exception {
        when(bookingService.getByOwnerId(anyInt(), anyString()))
                .thenReturn(List.of(booking));
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value(bookingDto.getStatus()));
    }

    @Test
    void getBookingsByOwnerWithPaginationTest() throws Exception {
        when(bookingService.getByOwnerIdWithPagination(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }
}
