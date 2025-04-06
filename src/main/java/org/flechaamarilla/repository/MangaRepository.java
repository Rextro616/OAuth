package org.flechaamarilla.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.flechaamarilla.entity.Manga;
import org.flechaamarilla.entity.User;

import java.util.List;

// Repositorio para Manga
@ApplicationScoped
public class MangaRepository implements PanacheRepository<Manga> {

    public List<Manga> findByCreator(User creator, int page, int size) {
        return find("creator", creator)
                .page(page, size)
                .list();
    }

    public List<Manga> findByGenre(String genre, int page, int size) {
        return find("genres like ?1", "%" + genre + "%")
                .page(page, size)
                .list();
    }

    public List<Manga> findPopular(int page, int size) {
        return find("ORDER BY viewCount DESC")
                .page(page, size)
                .list();
    }

    public List<Manga> findHighestRated(int page, int size) {
        return find("ratingCount > 5 ORDER BY averageRating DESC")
                .page(page, size)
                .list();
    }

    public List<Manga> searchByTitle(String query, int page, int size) {
        return find("title like ?1", "%" + query + "%")
                .page(page, size)
                .list();
    }
}
