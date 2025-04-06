package org.flechaamarilla.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "chapters")
public class Chapter extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "manga_id", nullable = false)
    public Manga manga;

    @Column(nullable = false)
    public String title;

    @Column(nullable = false)
    public int chapterNumber;

    @Column
    public String description;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date publishDate;

    @ElementCollection
    @CollectionTable(name = "chapter_pages", joinColumns = @JoinColumn(name = "chapter_id"))
    @Column(name = "page_url")
    @OrderColumn(name = "page_number")
    public List<String> pages = new ArrayList<>();

    @Column
    public long viewCount = 0;

    @Column
    public boolean draft = false;

    public Chapter() {
        this.publishDate = new Date();
    }
}