package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @Transient
    private Item item;
    @Column(name = "item_id")
    private int itemId;
    @Transient
    private User booker;
    @Column(name = "booker_id")
    private int userId;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
