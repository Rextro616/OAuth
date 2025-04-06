package org.flechaamarilla.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.flechaamarilla.entity.Chapter;
import org.flechaamarilla.entity.Manga;

import java.util.List;

// Repositorio para Chapter
@ApplicationScoped
public class ChapterRepository implements PanacheRepository<Chapter> {

    public List<Chapter> findByManga(Manga manga) {
        return find("manga = ?1 ORDER BY chapterNumber", manga).list();
    }

    public Chapter findByMangaAndNumber(Manga manga, int chapterNumber) {
        return find("manga = ?1 and chapterNumber = ?2", manga, chapterNumber).firstResult();
    }

    public List<Chapter> findRecentChapters(int limit) {
        return find("draft = false ORDER BY publishDate DESC")
                .page(0, limit)
                .list();
    }
}
