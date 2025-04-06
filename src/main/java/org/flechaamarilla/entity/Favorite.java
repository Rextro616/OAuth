package org.flechaamarilla.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.Date;

// Favoritos/Biblioteca del usuario
@Entity
@Table(name = "favorites", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "manga_id"})
})
public class Favorite extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @ManyToOne
    @JoinColumn(name = "manga_id", nullable = false)
    public Manga manga;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date addedAt;

    @Column
    public int lastReadChapter;

    public Favorite() {
        this.addedAt = new Date();
    }
}
