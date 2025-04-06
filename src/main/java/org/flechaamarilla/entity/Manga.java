package org.flechaamarilla.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "mangas")
public class Manga extends PanacheEntity {

    @Column(nullable = false)
    public String title;

    @Column(length = 2000)
    public String description;

    @Column
    public String coverImageUrl;

    @ManyToOne
    public User creator;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date createdAt;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date updatedAt;

    @Column
    public boolean completed = false;

    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL)
    public List<Chapter> chapters = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "manga_genres", joinColumns = @JoinColumn(name = "manga_id"))
    @Column(name = "genre")
    public Set<String> genres = new HashSet<>();

    @Column
    public long viewCount = 0;

    @Column
    public float averageRating = 0.0f;

    @Column
    public int ratingCount = 0;

    public Manga() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Método conveniente para actualizar la fecha de última actualización
    public void updateTimestamp() {
        this.updatedAt = new Date();
    }
}