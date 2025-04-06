package org.flechaamarilla.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.flechaamarilla.entity.Manga;
import org.flechaamarilla.entity.Rating;
import org.flechaamarilla.entity.User;

import java.util.List;

// Repositorio para Rating
@ApplicationScoped
public class RatingRepository implements PanacheRepository<Rating> {

    public Rating findByUserAndManga(User user, Manga manga) {
        return find("user = ?1 and manga = ?2", user, manga).firstResult();
    }

    public List<Rating> findByManga(Manga manga) {
        return find("manga", manga).list();
    }

    public double getAverageRating(Manga manga) {
        return find("manga", manga)
                .stream()
                .mapToInt(r -> r.score)
                .average()
                .orElse(0.0);
    }
}
