package org.flechaamarilla.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import org.flechaamarilla.dto.ChapterCreateDTO;
import org.flechaamarilla.dto.ChapterListItemDTO;
import org.flechaamarilla.dto.ChapterResponseDTO;
import org.flechaamarilla.dto.ChapterUpdateDTO;
import org.flechaamarilla.entity.Chapter;
import org.flechaamarilla.entity.Manga;
import org.flechaamarilla.entity.User;
import org.flechaamarilla.repository.ChapterRepository;
import org.flechaamarilla.repository.MangaRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@AllArgsConstructor
public class ChapterService {

    ChapterRepository chapterRepository;
    MangaRepository mangaRepository;

    @Transactional
    public Chapter createChapter(ChapterCreateDTO dto, User currentUser) {
        Manga manga = mangaRepository.findById(dto.mangaId);
        if (manga == null) {
            throw new NotFoundException("Manga no encontrado");
        }

        // Verificar que el usuario sea el creador del manga o un admin
        if (!manga.creator.id.equals(currentUser.id) && currentUser.role != User.UserRole.ADMIN) {
            throw new ForbiddenException("No tienes permiso para añadir capítulos a este manga");
        }

        // Verificar que no exista un capítulo con el mismo número
        Chapter existingChapter = chapterRepository.findByMangaAndNumber(manga, dto.chapterNumber);
        if (existingChapter != null) {
            throw new BadRequestException("Ya existe un capítulo con ese número");
        }

        Chapter chapter = new Chapter();
        chapter.manga = manga;
        chapter.title = dto.title;
        chapter.chapterNumber = dto.chapterNumber;
        chapter.description = dto.description;
        chapter.publishDate = new Date();
        chapter.pages.addAll(dto.pages);
        chapter.draft = dto.draft != null ? dto.draft : false;

        chapterRepository.persist(chapter);

        // Actualizar la fecha de actualización del manga
        manga.updatedAt = new Date();
        mangaRepository.persist(manga);

        return chapter;
    }

    @Transactional
    public Chapter updateChapter(Long chapterId, ChapterUpdateDTO dto, User currentUser) {
        Chapter chapter = chapterRepository.findById(chapterId);
        if (chapter == null) {
            throw new NotFoundException("Capítulo no encontrado");
        }

        // Verificar que el usuario sea el creador del manga o un admin
        if (!chapter.manga.creator.id.equals(currentUser.id) && currentUser.role != User.UserRole.ADMIN) {
            throw new ForbiddenException("No tienes permiso para editar este capítulo");
        }

        // Actualizar campos si se proporcionan
        if (dto.title != null) {
            chapter.title = dto.title;
        }

        if (dto.description != null) {
            chapter.description = dto.description;
        }

        if (dto.pages != null) {
            chapter.pages.clear();
            chapter.pages.addAll(dto.pages);
        }

        if (dto.draft != null) {
            chapter.draft = dto.draft;
        }

        chapterRepository.persist(chapter);

        // Actualizar la fecha de actualización del manga
        chapter.manga.updatedAt = new Date();
        mangaRepository.persist(chapter.manga);

        return chapter;
    }

    @Transactional
    public void deleteChapter(Long chapterId, User currentUser) {
        Chapter chapter = chapterRepository.findById(chapterId);
        if (chapter == null) {
            throw new NotFoundException("Capítulo no encontrado");
        }

        // Verificar que el usuario sea el creador del manga o un admin
        if (!chapter.manga.creator.id.equals(currentUser.id) && currentUser.role != User.UserRole.ADMIN) {
            throw new ForbiddenException("No tienes permiso para eliminar este capítulo");
        }

        chapterRepository.delete(chapter);

        // Actualizar la fecha de actualización del manga
        chapter.manga.updatedAt = new Date();
        mangaRepository.persist(chapter.manga);
    }

    public Chapter getChapterById(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId);
        if (chapter == null) {
            throw new NotFoundException("Capítulo no encontrado");
        }
        return chapter;
    }

    public Chapter getChapterByMangaAndNumber(Long mangaId, int chapterNumber) {
        Manga manga = mangaRepository.findById(mangaId);
        if (manga == null) {
            throw new NotFoundException("Manga no encontrado");
        }

        Chapter chapter = chapterRepository.findByMangaAndNumber(manga, chapterNumber);
        if (chapter == null) {
            throw new NotFoundException("Capítulo no encontrado");
        }

        return chapter;
    }

    public List<Chapter> getChaptersByManga(Long mangaId) {
        Manga manga = mangaRepository.findById(mangaId);
        if (manga == null) {
            throw new NotFoundException("Manga no encontrado");
        }

        return chapterRepository.findByManga(manga);
    }

    public List<Chapter> getRecentChapters(int limit) {
        return chapterRepository.findRecentChapters(limit);
    }

    @Transactional
    public void incrementViewCount(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId);
        if (chapter != null) {
            chapter.viewCount++;
            chapterRepository.persist(chapter);
        }
    }

    // Método para convertir una entidad Chapter a un DTO de respuesta completa
    public ChapterResponseDTO toResponseDTO(Chapter chapter) {
        return ChapterResponseDTO.builder()
                .id(chapter.id)
                .mangaId(chapter.manga.id)
                .mangaTitle(chapter.manga.title)
                .title(chapter.title)
                .chapterNumber(chapter.chapterNumber)
                .description(chapter.description)
                .publishDate(chapter.publishDate)
                .pages(chapter.pages)
                .viewCount(chapter.viewCount)
                .draft(chapter.draft)
                .build();
    }

    // Método para convertir una entidad Chapter a un DTO de listado (sin páginas)
    public ChapterListItemDTO toListItemDTO(Chapter chapter) {
        return ChapterListItemDTO.builder()
                .id(chapter.id)
                .mangaId(chapter.manga.id)
                .title(chapter.title)
                .chapterNumber(chapter.chapterNumber)
                .publishDate(chapter.publishDate)
                .viewCount(chapter.viewCount)
                .build();
    }

    // Convertir lista de capítulos a DTOs para listado
    public List<ChapterListItemDTO> toListItemDTOList(List<Chapter> chapters) {
        return chapters.stream()
                .map(this::toListItemDTO)
                .collect(Collectors.toList());
    }
}