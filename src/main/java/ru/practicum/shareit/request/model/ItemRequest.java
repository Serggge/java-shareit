package ru.practicum.shareit.request.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "item_requests")
@Data
@NoArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private LocalDateTime created;
    @ManyToOne
    private User user;
    @Transient
    private Set<Item> items = new HashSet<>();

    public void addItems(Iterable<Item> itemCollection) {
        for (Item item : itemCollection) {
            items.add(item);
        }
    }

}
