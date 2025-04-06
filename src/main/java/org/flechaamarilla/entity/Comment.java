package org.flechaamarilla.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.util.Date;

// Comentarios en capítulos
@Entity
@Table(name = "comments")
public class Comment extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    public Chapter chapter;

    @Column(nullable = false, length = 1000)
    public String content;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date createdAt;

    @Column
    public boolean edited = false;

    public Comment() {
        this.createdAt = new Date();
    }
}

