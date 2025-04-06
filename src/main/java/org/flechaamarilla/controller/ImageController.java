package org.flechaamarilla.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.flechaamarilla.dto.ImageUploadResponseDTO;
import org.flechaamarilla.service.CloudinaryService;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Path("/images")
@AllArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Imágenes", description = "Operaciones para subir y gestionar imágenes")
public class ImageController {

    CloudinaryService cloudinaryService;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed({"CREATOR", "ADMIN"})
    @Operation(
            summary = "Subir una imagen",
            description = "Permite subir una imagen a Cloudinary. Devuelve la URL y otros detalles de la imagen subida."
    )
    public ImageUploadResponseDTO uploadImage(
            @MultipartForm MultipartFormDataInput input,
            @QueryParam("folder") String folder) {

        try {
            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
            org.jboss.resteasy.plugins.providers.multipart.InputPart inputPart = uploadForm.get("file").getFirst();

            InputStream inputStream = inputPart.getBody(InputStream.class, null);
            return cloudinaryService.uploadImage(inputStream, folder);
        } catch (IOException e) {
            throw new InternalServerErrorException("Error al procesar la subida de la imagen: " + e.getMessage());
        }
    }

    @POST
    @Path("/upload/manga-cover")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed({"CREATOR", "ADMIN"})
    @Operation(
            summary = "Subir portada de manga",
            description = "Endpoint especializado para subir portadas de manga. Las imágenes se guardarán en la carpeta 'manga-covers'."
    )
    public ImageUploadResponseDTO uploadMangaCover(@MultipartForm MultipartFormDataInput input) {
        try {
            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
            org.jboss.resteasy.plugins.providers.multipart.InputPart inputPart = uploadForm.get("file").getFirst();

            InputStream inputStream = inputPart.getBody(InputStream.class, null);
            return cloudinaryService.uploadImage(inputStream, "manga-covers");
        } catch (IOException e) {
            throw new InternalServerErrorException("Error al procesar la subida de la portada: " + e.getMessage());
        }
    }

    @POST
    @Path("/upload/chapter-page")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed({"CREATOR", "ADMIN"})
    @Operation(
            summary = "Subir página de capítulo",
            description = "Endpoint especializado para subir páginas de capítulos. Las imágenes se guardarán en la carpeta 'chapter-pages'."
    )
    public ImageUploadResponseDTO uploadChapterPage(
            @MultipartForm MultipartFormDataInput input,
            @QueryParam("mangaId") Long mangaId,
            @QueryParam("chapterNumber") Integer chapterNumber) {

        try {
            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
            org.jboss.resteasy.plugins.providers.multipart.InputPart inputPart = uploadForm.get("file").getFirst();

            InputStream inputStream = inputPart.getBody(InputStream.class, null);

            // Crear una estructura de carpetas específica para este manga/capítulo
            String folder = String.format("chapter-pages/manga_%d/chapter_%d", mangaId, chapterNumber);
            return cloudinaryService.uploadImage(inputStream, folder);
        } catch (IOException e) {
            throw new InternalServerErrorException("Error al procesar la subida de la página: " + e.getMessage());
        }
    }

    @POST
    @Path("/upload/profile-image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed({"CREATOR", "READER", "MODERATOR", "ADMIN"})
    @Operation(
            summary = "Subir imagen de perfil",
            description = "Endpoint especializado para subir imágenes de perfil. Las imágenes se guardarán en la carpeta 'profile-images'."
    )
    public ImageUploadResponseDTO uploadProfileImage(@MultipartForm MultipartFormDataInput input) {
        try {
            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
            org.jboss.resteasy.plugins.providers.multipart.InputPart inputPart = uploadForm.get("file").getFirst();

            InputStream inputStream = inputPart.getBody(InputStream.class, null);
            return cloudinaryService.uploadImage(inputStream, "profile-images");
        } catch (IOException e) {
            throw new InternalServerErrorException("Error al procesar la subida de la imagen de perfil: " + e.getMessage());
        }
    }

    @DELETE
    @Path("/{publicId}")
    @RolesAllowed({"CREATOR", "ADMIN"})
    @Operation(
            summary = "Eliminar una imagen",
            description = "Elimina una imagen de Cloudinary utilizando su public_id."
    )
    public boolean deleteImage(@PathParam("publicId") String publicId) {
        return cloudinaryService.deleteImage(publicId);
    }
}