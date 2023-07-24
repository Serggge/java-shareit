package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItemId(long itemId);

    @Query("select c " +
            "from Comment as c " +
            "join c.author as a " +
            "join c.item as it " +
            "where it.id in ?1")
    List<Comment> findAllByItemId(Iterable<Long> ids);
}
