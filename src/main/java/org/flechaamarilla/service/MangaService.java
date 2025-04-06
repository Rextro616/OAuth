package org.flechaamarilla.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import org.flechaamarilla.dto.MangaCreateDTO;
import org.flechaamarilla.dto.MangaResponseDTO;
import org.flechaamarilla.dto.MangaUpdateDTO;
import org.flechaamarilla.dto.UserInfoDTO;
import org.flechaamarilla.entity.Favorite;
import org.flechaamarilla.entity.Manga;
import org.flechaamarilla.entity.User;
import org.flechaamarilla.repository.FavoriteRepository;
import org.flechaamarilla.repository.MangaRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@AllArgsConstructor
public class MangaService {

    MangaRepository mangaRepository;
    FavoriteRepository favoriteRepository;

    @Transactional
    public Manga createManga(MangaCreateDTO dto, User creator) {
        // Verificar que el usuario tenga rol de creador
        if (creator.role != User.UserRole.CREATOR && creator.role != User.UserRole.ADMIN) {
            throw new ForbiddenException("Solo los creadores pueden publicar mangas");
        }

        Manga manga = new Manga();
        manga.title = dto.title;
        manga.description = dto.description;
        manga.coverImageUrl = dto.coverImageUrl;
        manga.creator = creator;
        manga.createdAt = new Date();
        manga.updatedAt = new Date();

        if (dto.genres != null) {
            manga.genres.addAll(dto.genres);
        }

        mangaRepository.persist(manga);
        return manga;
    }

    @Transactional
    public Manga updateManga(Long mangaId, MangaUpdateDTO dto, User currentUser) {
        Manga manga = mangaRepository.findById(mangaId);
        if (manga == null) {
            throw new NotFoundException("Manga no encontrado");
        }

        // Verificar que el usuario sea el creador o un admin
        if (!manga.creator.id.equals(currentUser.id) && currentUser.role != User.UserRole.ADMIN) {
            throw new ForbiddenException("No tienes permiso para editar este manga");
        }

        // Actualizar campos si se proporcionan
        if (dto.title != null) {
            manga.title = dto.title;
        }

        if (dto.description != null) {
            manga.description = dto.description;
        }

        if (dto.coverImageUrl != null) {
            manga.coverImageUrl = dto.coverImageUrl;
        }

        if (dto.genres != null) {
            manga.genres.clear();
            manga.genres.addAll(dto.genres);
        }

        if (dto.completed != null) {
            manga.completed = dto.completed;
        }

        manga.updatedAt = new Date();
        mangaRepository.persist(manga);

        return manga;
    }

    @Transactional
    public void deleteManga(Long mangaId, User currentUser) {
        Manga manga = mangaRepository.findById(mangaId);
        if (manga == null) {
            throw new NotFoundException("Manga no encontrado");
        }

        // Verificar que el usuario sea el creador o un admin
        if (!manga.creator.id.equals(currentUser.id) && currentUser.role != User.UserRole.ADMIN) {
            throw new ForbiddenException("No tienes permiso para eliminar este manga");
        }

        mangaRepository.delete(manga);
    }

    public Manga getMangaById(Long mangaId) {
        Manga manga = mangaRepository.findById(mangaId);
        if (manga == null) {
            throw new NotFoundException("Manga no encontrado");
        }
        return manga;
    }

    public List<Manga> getMangasByCreator(User creator, int page, int size) {
        return mangaRepository.findByCreator(creator, page, size);
    }

    public List<Manga> getMangasByGenre(String genre, int page, int size) {
        return mangaRepository.findByGenre(genre, page, size);
    }

    public List<Manga> getPopularMangas(int page, int size) {
        return mangaRepository.findPopular(page, size);
    }

    public List<Manga> getHighestRatedMangas(int page, int size) {
        return mangaRepository.findHighestRated(page, size);
    }

    public List<Manga> searchMangas(String query, int page, int size) {
        return mangaRepository.searchByTitle(query, page, size);
    }

    @Transactional
    public void incrementViewCount(Long mangaId) {
        Manga manga = mangaRepository.findById(mangaId);
        if (manga != null) {
            manga.viewCount++;
            mangaRepository.persist(manga);
        }
    }

    // Método para convertir una entidad Manga a un DTO de respuesta
    public MangaResponseDTO toResponseDTO(Manga manga, User currentUser) {
        boolean inUserFavorites = false;

        // Verificar si el manga está en favoritos del usuario actual
        if (currentUser != null) {
            Favorite favorite = favoriteRepository.findByUserAndManga(currentUser, manga);
            inUserFavorites = (favorite != null);
        }

        return MangaResponseDTO.builder()
                .id(manga.id)
                .title(manga.title)
                .description(manga.description)
                .coverImageUrl(manga.coverImageUrl)
                .creator(UserInfoDTO.builder()
                        .id(manga.creator.id)
                        .username(manga.creator.username)
                        .displayName(manga.creator.displayName)
                        .profileImageUrl(manga.creator.profileImageUrl)
                        .build())
                .createdAt(manga.createdAt)
                .updatedAt(manga.updatedAt)
                .completed(manga.completed)
                .genres(manga.genres)
                .viewCount(manga.viewCount)
                .averageRating(manga.averageRating)
                .ratingCount(manga.ratingCount)
                .chapterCount(manga.chapters.size())
                .inUserFavorites(inUserFavorites)
                .build();
    }

    // Convertir lista de mangas a DTOs
    public List<MangaResponseDTO> toResponseDTOList(List<Manga> mangas, User currentUser) {
        return mangas.stream()
                .map(manga -> toResponseDTO(manga, currentUser))
                .collect(Collectors.toList());
    }
}