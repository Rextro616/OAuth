package org.flechaamarilla.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import lombok.AllArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.flechaamarilla.dto.ChapterCreateDTO;
import org.flechaamarilla.dto.ChapterListItemDTO;
import org.flechaamarilla.dto.ChapterResponseDTO;
import org.flechaamarilla.dto.ChapterUpdateDTO;
import org.flechaamarilla.entity.Chapter;
import org.flechaamarilla.entity.User;
import org.flechaamarilla.service.AuthService;
import org.flechaamarilla.service.ChapterService;

import java.util.List;

@Path("/chapter")
@AllArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Capítulos", description = "Operaciones relacionadas con capítulos de mangas")
public class ChapterController {

    ChapterService chapterService;
    AuthService authService;

    @POST
    @RolesAllowed({"CREATOR", "ADMIN"})
    @Operation(summary = "Crear un nuevo capítulo")
    public ChapterResponseDTO createChapter(
            @Context SecurityContext securityContext,
            @Valid ChapterCreateDTO createDTO) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        Chapter chapter = chapterService.createChapter(createDTO, currentUser);
        return chapterService.toResponseDTO(chapter);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"CREATOR", "ADMIN"})
    @Operation(summary = "Actualizar un capítulo existente")
    public ChapterResponseDTO updateChapter(
            @PathParam("id") Long chapterId,
            @Context SecurityContext securityContext,
            @Valid ChapterUpdateDTO updateDTO) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        Chapter chapter = chapterService.updateChapter(chapterId, updateDTO, currentUser);
        return chapterService.toResponseDTO(chapter);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"CREATOR", "ADMIN"})
    @Operation(summary = "Eliminar un capítulo")
    public void deleteChapter(
            @PathParam("id") Long chapterId,
            @Context SecurityContext securityContext) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        chapterService.deleteChapter(chapterId, currentUser);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener un capítulo por ID")
    public ChapterResponseDTO getChapterById(
            @PathParam("id") Long chapterId) {

        Chapter chapter = chapterService.getChapterById(chapterId);
        chapterService.incrementViewCount(chapterId); // Incrementar contador de vistas

        return chapterService.toResponseDTO(chapter);
    }

    @GET
    @Path("/manga/{mangaId}")
    @Operation(summary = "Listar capítulos de un manga")
    public List<ChapterListItemDTO> getChaptersByManga(
            @PathParam("mangaId") Long mangaId) {

        List<Chapter> chapters = chapterService.getChaptersByManga(mangaId);
        return chapterService.toListItemDTOList(chapters);
    }

    @GET
    @Path("/manga/{mangaId}/chapter/{chapterNumber}")
    @Operation(summary = "Obtener un capítulo por número")
    public ChapterResponseDTO getChapterByNumber(
            @PathParam("mangaId") Long mangaId,
            @PathParam("chapterNumber") int chapterNumber) {

        Chapter chapter = chapterService.getChapterByMangaAndNumber(mangaId, chapterNumber);
        chapterService.incrementViewCount(chapter.id); // Incrementar contador de vistas

        return chapterService.toResponseDTO(chapter);
    }

    @GET
    @Path("/recent")
    @Operation(summary = "Obtener capítulos recientes")
    public List<ChapterListItemDTO> getRecentChapters(
            @QueryParam("limit") @DefaultValue("20") int limit) {

        List<Chapter> chapters = chapterService.getRecentChapters(limit);
        return chapterService.toListItemDTOList(chapters);
    }
}