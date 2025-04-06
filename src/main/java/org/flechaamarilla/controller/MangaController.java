package org.flechaamarilla.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import lombok.AllArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.flechaamarilla.dto.MangaCreateDTO;
import org.flechaamarilla.dto.MangaResponseDTO;
import org.flechaamarilla.dto.MangaUpdateDTO;
import org.flechaamarilla.entity.Manga;
import org.flechaamarilla.entity.User;
import org.flechaamarilla.service.AuthService;
import org.flechaamarilla.service.MangaService;

import java.util.List;

@Path("/manga")
@AllArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Mangas", description = "Operaciones relacionadas con mangas")
public class MangaController {

    MangaService mangaService;
    AuthService authService;

    @POST
    @RolesAllowed({"CREATOR", "ADMIN"})
    @Operation(summary = "Crear un nuevo manga")
    public MangaResponseDTO createManga(
            @Context SecurityContext securityContext,
            @Valid MangaCreateDTO createDTO) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        Manga manga = mangaService.createManga(createDTO, currentUser);
        return mangaService.toResponseDTO(manga, currentUser);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"CREATOR", "ADMIN"})
    @Operation(summary = "Actualizar un manga existente")
    public MangaResponseDTO updateManga(
            @PathParam("id") Long mangaId,
            @Context SecurityContext securityContext,
            @Valid MangaUpdateDTO updateDTO) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        Manga manga = mangaService.updateManga(mangaId, updateDTO, currentUser);
        return mangaService.toResponseDTO(manga, currentUser);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"CREATOR", "ADMIN"})
    @Operation(summary = "Eliminar un manga")
    public void deleteManga(
            @PathParam("id") Long mangaId,
            @Context SecurityContext securityContext) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        mangaService.deleteManga(mangaId, currentUser);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener un manga por ID")
    public MangaResponseDTO getMangaById(
            @PathParam("id") Long mangaId,
            @Context SecurityContext securityContext) {

        User currentUser = null;
        if (securityContext.getUserPrincipal() != null) {
            String username = securityContext.getUserPrincipal().getName();
            currentUser = authService.getCurrentUser(username);
        }

        Manga manga = mangaService.getMangaById(mangaId);
        mangaService.incrementViewCount(mangaId); // Incrementar contador de vistas

        return mangaService.toResponseDTO(manga, currentUser);
    }

    @GET
    @Path("/creator/{creatorId}")
    @Operation(summary = "Listar mangas por creador")
    public List<MangaResponseDTO> getMangasByCreator(
            @PathParam("creatorId") Long creatorId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @Context SecurityContext securityContext) {

        User creator = authService.getCurrentUser(creatorId);

        User currentUser = null;
        if (securityContext.getUserPrincipal() != null) {
            String username = securityContext.getUserPrincipal().getName();
            currentUser = authService.getCurrentUser(username);
        }

        List<Manga> mangas = mangaService.getMangasByCreator(creator, page, size);
        return mangaService.toResponseDTOList(mangas, currentUser);
    }

    @GET
    @Path("/genre/{genre}")
    @Operation(summary = "Listar mangas por género")
    public List<MangaResponseDTO> getMangasByGenre(
            @PathParam("genre") String genre,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @Context SecurityContext securityContext) {

        User currentUser = null;
        if (securityContext.getUserPrincipal() != null) {
            String username = securityContext.getUserPrincipal().getName();
            currentUser = authService.getCurrentUser(username);
        }

        List<Manga> mangas = mangaService.getMangasByGenre(genre, page, size);
        return mangaService.toResponseDTOList(mangas, currentUser);
    }

    @GET
    @Path("/popular")
    @Operation(summary = "Listar mangas populares")
    public List<MangaResponseDTO> getPopularMangas(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @Context SecurityContext securityContext) {

        User currentUser = null;
        if (securityContext.getUserPrincipal() != null) {
            String username = securityContext.getUserPrincipal().getName();
            currentUser = authService.getCurrentUser(username);
        }

        List<Manga> mangas = mangaService.getPopularMangas(page, size);
        return mangaService.toResponseDTOList(mangas, currentUser);
    }

    @GET
    @Path("/highest-rated")
    @Operation(summary = "Listar mangas mejor valorados")
    public List<MangaResponseDTO> getHighestRatedMangas(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @Context SecurityContext securityContext) {

        User currentUser = null;
        if (securityContext.getUserPrincipal() != null) {
            String username = securityContext.getUserPrincipal().getName();
            currentUser = authService.getCurrentUser(username);
        }

        List<Manga> mangas = mangaService.getHighestRatedMangas(page, size);
        return mangaService.toResponseDTOList(mangas, currentUser);
    }

    @GET
    @Path("/search")
    @Operation(summary = "Buscar mangas por título")
    public List<MangaResponseDTO> searchMangas(
            @QueryParam("query") String query,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @Context SecurityContext securityContext) {

        User currentUser = null;
        if (securityContext.getUserPrincipal() != null) {
            String username = securityContext.getUserPrincipal().getName();
            currentUser = authService.getCurrentUser(username);
        }

        List<Manga> mangas = mangaService.searchMangas(query, page, size);
        return mangaService.toResponseDTOList(mangas, currentUser);
    }
}