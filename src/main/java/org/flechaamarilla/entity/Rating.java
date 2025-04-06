package org.flechaamarilla.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.Date;

// Calificaciones de manga
@Entity
@Table(name = "ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "manga_id"})
})
public class Rating extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @ManyToOne
    @JoinColumn(name = "manga_id", nullable = false)
    public Manga manga;

    @Column(nullable = false)
    public int score; // 1-5 estrellas

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date ratedAt;

    public Rating() {
        this.ratedAt = new Date();
    }
}
