package org.flechaamarilla.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.flechaamarilla.entity.Favorite;
import org.flechaamarilla.entity.Manga;
import org.flechaamarilla.entity.User;

import java.util.List;

// Repositorio para Favorite
@ApplicationScoped
public class FavoriteRepository implements PanacheRepository<Favorite> {

    public List<Favorite> findByUser(User user) {
        return find("user = ?1 ORDER BY addedAt DESC", user).list();
    }

    public Favorite findByUserAndManga(User user, Manga manga) {
        return find("user = ?1 and manga = ?2", user, manga).firstResult();
    }

    public long countFavorites(Manga manga) {
        return count("manga", manga);
    }
}
