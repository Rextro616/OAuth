package org.flechaamarilla.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import org.flechaamarilla.dto.*;
import org.flechaamarilla.entity.*;
import org.flechaamarilla.repository.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@AllArgsConstructor
public class UserInteractionService {

    CommentRepository commentRepository;
    RatingRepository ratingRepository;
    FavoriteRepository favoriteRepository;
    MangaRepository mangaRepository;
    ChapterRepository chapterRepository;

    // ===== COMENTARIOS =====

    @Transactional
    public Comment createComment(CommentCreateDTO dto, User currentUser) {
        Chapter chapter = chapterRepository.findById(dto.chapterId);
        if (chapter == null) {
            throw new NotFoundException("Capítulo no encontrado");
        }

        Comment comment = new Comment();
        comment.user = currentUser;
        comment.chapter = chapter;
        comment.content = dto.content;
        comment.createdAt = new Date();

        commentRepository.persist(comment);
        return comment;
    }

    @Transactional
    public Comment updateComment(Long commentId, String newContent, User currentUser) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new NotFoundException("Comentario no encontrado");
        }

        // Verificar que el usuario sea el autor del comentario
        if (!comment.user.id.equals(currentUser.id) && currentUser.role != User.UserRole.ADMIN && currentUser.role != User.UserRole.MODERATOR) {
            throw new BadRequestException("No tienes permiso para editar este comentario");
        }

        comment.content = newContent;
        comment.edited = true;

        commentRepository.persist(comment);
        return comment;
    }

    @Transactional
    public void deleteComment(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new NotFoundException("Comentario no encontrado");
        }

        // Verificar que el usuario sea el autor del comentario o un moderador/admin
        if (!comment.user.id.equals(currentUser.id) && currentUser.role != User.UserRole.ADMIN && currentUser.role != User.UserRole.MODERATOR) {
            throw new BadRequestException("No tienes permiso para eliminar este comentario");
        }

        commentRepository.delete(comment);
    }

    public List<Comment> getCommentsByChapter(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId);
        if (chapter == null) {
            throw new NotFoundException("Capítulo no encontrado");
        }

        return commentRepository.findByChapter(chapter);
    }

    public List<Comment> getCommentsByUser(User user) {
        return commentRepository.findByUser(user);
    }

    // ===== CALIFICACIONES =====

    @Transactional
    public Rating rateManga(RatingCreateDTO dto, User currentUser) {
        Manga manga = mangaRepository.findById(dto.mangaId);
        if (manga == null) {
            throw new NotFoundException("Manga no encontrado");
        }

        // Verificar si el usuario ya ha calificado este manga
        Rating existingRating = ratingRepository.findByUserAndManga(currentUser, manga);

        if (existingRating != null) {
            // Actualizar calificación existente
            existingRating.score = dto.score;
            existingRating.ratedAt = new Date();
            ratingRepository.persist(existingRating);

            // Recalcular promedio
            updateMangaRating(manga);

            return existingRating;
        } else {
            // Crear nueva calificación
            Rating rating = new Rating();
            rating.user = currentUser;
            rating.manga = manga;
            rating.score = dto.score;
            rating.ratedAt = new Date();

            ratingRepository.persist(rating);

            // Actualizar promedio y contador de calificaciones del manga
            updateMangaRating(manga);

            return rating;
        }
    }

    @Transactional
    public void deleteRating(Long mangaId, User currentUser) {
        Manga manga = mangaRepository.findById(mangaId);
        if (manga == null) {
            throw new NotFoundException("Manga no encontrado");
        }

        Rating rating = ratingRepository.findByUserAndManga(currentUser, manga);
        if (rating == null) {
            throw new NotFoundException("No has calificado este manga");
        }

        ratingRepository.delete(rating);

        // Recalcular promedio
        updateMangaRating(manga);
    }

    private void updateMangaRating(Manga manga) {
        List<Rating> ratings = ratingRepository.findByManga(manga);

        if (ratings.isEmpty()) {
            manga.averageRating = 0;
            manga.ratingCount = 0;
        } else {
            double sum = 0;
            for (Rating r : ratings) {
                sum += r.score;
            }

            manga.averageRating = (float) (sum / ratings.size());
            manga.ratingCount = ratings.size();
        }

        mangaRepository.persist(manga);
    }

    public Rating getUserRating(Long mangaId, User currentUser) {
        Manga manga = mangaRepository.findById(mangaId);
        if (manga == null) {
            throw new NotFoundException("Manga no encontrado");
        }

        return ratingRepository.findByUserAndManga(currentUser, manga);
    }

    // ===== FAVORITOS =====

    @Transactional
    public Favorite addToFavorites(Long mangaId, User currentUser) {
        Manga manga = mangaRepository.findById(mangaId);
        if (manga == null) {
            throw new NotFoundException("Manga no encontrado");
        }

        // Verificar si ya está en favoritos
        Favorite existingFavorite = favoriteRepository.findByUserAndManga(currentUser, manga);
        if (existingFavorite != null) {
            return existingFavorite; // Ya está en favoritos
        }

        Favorite favorite = new Favorite();
        favorite.user = currentUser;
        favorite.manga = manga;
        favorite.addedAt = new Date();
        favorite.lastReadChapter = 0; // No ha leído ningún capítulo aún

        favoriteRepository.persist(favorite);
        return favorite;
    }

    @Transactional
    public void removeFromFavorites(Long mangaId, User currentUser) {
        Manga manga = mangaRepository.findById(mangaId);
        if (manga == null) {
            throw new NotFoundException("Manga no encontrado");
        }

        Favorite favorite = favoriteRepository.findByUserAndManga(currentUser, manga);
        if (favorite == null) {
            throw new NotFoundException("Este manga no está en tus favoritos");
        }

        favoriteRepository.delete(favorite);
    }

    @Transactional
    public Favorite updateReadProgress(ReadProgressDTO dto, User currentUser) {
        Manga manga = mangaRepository.findById(dto.mangaId);
        if (manga == null) {
            throw new NotFoundException("Manga no encontrado");
        }

        // Verificar que el capítulo existe
        Chapter chapter = chapterRepository.findByMangaAndNumber(manga, dto.lastReadChapter);
        if (chapter == null) {
            throw new NotFoundException("Capítulo no encontrado");
        }

        Favorite favorite = favoriteRepository.findByUserAndManga(currentUser, manga);

        if (favorite == null) {
            // Si no está en favoritos, lo añadimos automáticamente
            favorite = new Favorite();
            favorite.user = currentUser;
            favorite.manga = manga;
            favorite.addedAt = new Date();
        }

        favorite.lastReadChapter = dto.lastReadChapter;
        favoriteRepository.persist(favorite);

        return favorite;
    }

    public List<Favorite> getUserFavorites(User user) {
        return favoriteRepository.findByUser(user);
    }

    public Favorite getFavoriteStatus(Long mangaId, User currentUser) {
        Manga manga = mangaRepository.findById(mangaId);
        if (manga == null) {
            throw new NotFoundException("Manga no encontrado");
        }

        return favoriteRepository.findByUserAndManga(currentUser, manga);
    }

    public long getMangaFavoriteCount(Long mangaId) {
        Manga manga = mangaRepository.findById(mangaId);
        if (manga == null) {
            throw new NotFoundException("Manga no encontrado");
        }

        return favoriteRepository.countFavorites(manga);
    }

    // ===== CONVERSIONES A DTOs =====

    public CommentResponseDTO commentToResponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.id)
                .user(UserInfoDTO.builder()
                        .id(comment.user.id)
                        .username(comment.user.username)
                        .displayName(comment.user.displayName)
                        .profileImageUrl(comment.user.profileImageUrl)
                        .build())
                .chapterId(comment.chapter.id)
                .content(comment.content)
                .createdAt(comment.createdAt)
                .edited(comment.edited)
                .build();
    }

    public List<CommentResponseDTO> commentsToResponseDTOList(List<Comment> comments) {
        return comments.stream()
                .map(this::commentToResponseDTO)
                .collect(Collectors.toList());
    }

    public RatingResponseDTO ratingToResponseDTO(Rating rating) {
        return RatingResponseDTO.builder()
                .id(rating.id)
                .mangaId(rating.manga.id)
                .mangaTitle(rating.manga.title)
                .score(rating.score)
                .ratedAt(rating.ratedAt)
                .build();
    }

    public FavoriteResponseDTO favoriteToResponseDTO(Favorite favorite, MangaService mangaService) {
        return FavoriteResponseDTO.builder()
                .id(favorite.id)
                .manga(mangaService.toResponseDTO(favorite.manga, favorite.user))
                .addedAt(favorite.addedAt)
                .lastReadChapter(favorite.lastReadChapter)
                .build();
    }

    public List<FavoriteResponseDTO> favoritesToResponseDTOList(List<Favorite> favorites, MangaService mangaService) {
        return favorites.stream()
                .map(fav -> favoriteToResponseDTO(fav, mangaService))
                .collect(Collectors.toList());
    }
}