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
import org.flechaamarilla.dto.*;
import org.flechaamarilla.entity.Comment;
import org.flechaamarilla.entity.Favorite;
import org.flechaamarilla.entity.Rating;
import org.flechaamarilla.entity.User;
import org.flechaamarilla.service.AuthService;
import org.flechaamarilla.service.MangaService;
import org.flechaamarilla.service.UserInteractionService;

import java.util.List;

@Path("/interaction")
@AllArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Interacciones", description = "Operaciones relacionadas con interacciones de usuario (comentarios, calificaciones, favoritos)")
public class UserInteractionController {

    UserInteractionService interactionService;
    MangaService mangaService;
    AuthService authService;

    // ===== COMENTARIOS =====

    @POST
    @Path("/comment")
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(summary = "Crear un nuevo comentario")
    public CommentResponseDTO createComment(
            @Context SecurityContext securityContext,
            @Valid CommentCreateDTO createDTO) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        Comment comment = interactionService.createComment(createDTO, currentUser);
        return interactionService.commentToResponseDTO(comment);
    }

    @PUT
    @Path("/comment/{id}")
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(summary = "Actualizar un comentario existente")
    public CommentResponseDTO updateComment(
            @PathParam("id") Long commentId,
            @Context SecurityContext securityContext,
            String newContent) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        Comment comment = interactionService.updateComment(commentId, newContent, currentUser);
        return interactionService.commentToResponseDTO(comment);
    }

    @DELETE
    @Path("/comment/{id}")
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(summary = "Eliminar un comentario")
    public void deleteComment(
            @PathParam("id") Long commentId,
            @Context SecurityContext securityContext) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        interactionService.deleteComment(commentId, currentUser);
    }

    @GET
    @Path("/comment/chapter/{chapterId}")
    @Operation(summary = "Listar comentarios de un capítulo")
    public List<CommentResponseDTO> getCommentsByChapter(
            @PathParam("chapterId") Long chapterId) {

        List<Comment> comments = interactionService.getCommentsByChapter(chapterId);
        return interactionService.commentsToResponseDTOList(comments);
    }

    // ===== CALIFICACIONES =====

    @POST
    @Path("/rating")
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(summary = "Calificar un manga")
    public RatingResponseDTO rateManga(
            @Context SecurityContext securityContext,
            @Valid RatingCreateDTO createDTO) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        Rating rating = interactionService.rateManga(createDTO, currentUser);
        return interactionService.ratingToResponseDTO(rating);
    }

    @DELETE
    @Path("/rating/{mangaId}")
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(summary = "Eliminar calificación de un manga")
    public void deleteRating(
            @PathParam("mangaId") Long mangaId,
            @Context SecurityContext securityContext) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        interactionService.deleteRating(mangaId, currentUser);
    }

    @GET
    @Path("/rating/{mangaId}")
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(summary = "Obtener calificación del usuario para un manga")
    public RatingResponseDTO getUserRating(
            @PathParam("mangaId") Long mangaId,
            @Context SecurityContext securityContext) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        Rating rating = interactionService.getUserRating(mangaId, currentUser);
        if (rating == null) {
            return null;
        }
        return interactionService.ratingToResponseDTO(rating);
    }

    // ===== FAVORITOS =====

    @POST
    @Path("/favorite")
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(summary = "Añadir un manga a favoritos")
    public FavoriteResponseDTO addToFavorites(
            @Context SecurityContext securityContext,
            @Valid FavoriteCreateDTO createDTO) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        Favorite favorite = interactionService.addToFavorites(createDTO.mangaId, currentUser);
        return interactionService.favoriteToResponseDTO(favorite, mangaService);
    }

    @DELETE
    @Path("/favorite/{mangaId}")
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(summary = "Eliminar un manga de favoritos")
    public void removeFromFavorites(
            @PathParam("mangaId") Long mangaId,
            @Context SecurityContext securityContext) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        interactionService.removeFromFavorites(mangaId, currentUser);
    }

    @POST
    @Path("/read-progress")
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(summary = "Actualizar progreso de lectura")
    public FavoriteResponseDTO updateReadProgress(
            @Context SecurityContext securityContext,
            @Valid ReadProgressDTO progressDTO) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        Favorite favorite = interactionService.updateReadProgress(progressDTO, currentUser);
        return interactionService.favoriteToResponseDTO(favorite, mangaService);
    }

    @GET
    @Path("/favorites")
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(summary = "Listar favoritos del usuario")
    public List<FavoriteResponseDTO> getUserFavorites(
            @Context SecurityContext securityContext) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        List<Favorite> favorites = interactionService.getUserFavorites(currentUser);
        return interactionService.favoritesToResponseDTOList(favorites, mangaService);
    }

    @GET
    @Path("/favorite/{mangaId}")
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(summary = "Verificar si un manga está en favoritos")
    public FavoriteResponseDTO getFavoriteStatus(
            @PathParam("mangaId") Long mangaId,
            @Context SecurityContext securityContext) {

        String username = securityContext.getUserPrincipal().getName();
        User currentUser = authService.getCurrentUser(username);

        Favorite favorite = interactionService.getFavoriteStatus(mangaId, currentUser);
        if (favorite == null) {
            return null;
        }
        return interactionService.favoriteToResponseDTO(favorite, mangaService);
    }

    @GET
    @Path("/favorite/count/{mangaId}")
    @Operation(summary = "Contar cuántos usuarios tienen un manga en favoritos")
    public long getMangaFavoriteCount(
            @PathParam("mangaId") Long mangaId) {

        return interactionService.getMangaFavoriteCount(mangaId);
    }
}