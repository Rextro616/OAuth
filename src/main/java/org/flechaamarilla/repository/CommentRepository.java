package org.flechaamarilla.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.flechaamarilla.entity.Chapter;
import org.flechaamarilla.entity.Comment;
import org.flechaamarilla.entity.User;

import java.util.List;

// Repositorio para Comment
@ApplicationScoped
public class CommentRepository implements PanacheRepository<Comment> {

    public List<Comment> findByChapter(Chapter chapter) {
        return find("chapter = ?1 ORDER BY createdAt DESC", chapter).list();
    }

    public List<Comment> findByUser(User user) {
        return find("user = ?1 ORDER BY createdAt DESC", user).list();
    }
}
