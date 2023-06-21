package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;
import javax.persistence.*;

@Entity
@Table(name = "items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String description;
    Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User owner;

    public Item withId(long id) {
        return Item.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .build();
    }

    public Item withOwner(User owner) {
        return Item.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .build();
    }

}
