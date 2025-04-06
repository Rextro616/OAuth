package org.flechaamarilla.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flechaamarilla.dto.ImageUploadResponseDTO;
import org.flechaamarilla.entity.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class CloudinaryService {

    @ConfigProperty(name = "cloudinary.cloud_name")
    String cloudName;

    @ConfigProperty(name = "cloudinary.api_key")
    String apiKey;

    @ConfigProperty(name = "cloudinary.api_secret")
    String apiSecret;

    @ConfigProperty(name = "cloudinary.secure", defaultValue = "true")
    boolean secure;

    private Cloudinary cloudinary;

    @Inject
    FirebaseService firebaseService;

    @PostConstruct
    void initialize() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", secure
        ));
        log.info("Cloudinary initialized with cloud_name: {}", cloudName);
    }

    /**
     * Sube una imagen desde un archivo a Cloudinary
     * @param file El archivo a subir
     * @param folder La carpeta de destino en Cloudinary (opcional)
     * @param user El usuario que está subiendo la imagen (para notificaciones)
     * @return DTO con la información de la imagen subida
     */
    public ImageUploadResponseDTO uploadImage(File file, String folder, User user) {
        try {
            Map params = ObjectUtils.asMap(
                    "public_id", UUID.randomUUID().toString(),
                    "overwrite", true,
                    "resource_type", "image"
            );

            // Si se proporciona una carpeta, añadirla a los parámetros
            if (folder != null && !folder.isEmpty()) {
                params.put("folder", folder);
            }

            Map uploadResult = cloudinary.uploader().upload(file, params);
            ImageUploadResponseDTO responseDTO = mapToResponseDTO(uploadResult);

            // Enviar notificación si el usuario tiene token FCM
            if (user != null && user.fcmToken != null && !user.fcmToken.isEmpty()) {
                String notificationTitle = "Imagen subida correctamente";
                String notificationBody = "Tu imagen se ha subido exitosamente a " + folder;

                // Datos adicionales para la notificación
                Map<String, String> data = new HashMap<>();
                data.put("imageUrl", responseDTO.secureUrl);
                data.put("publicId", responseDTO.publicId);
                data.put("type", folder);

                firebaseService.sendNotification(user.fcmToken, notificationTitle, notificationBody, data);
            }

            return responseDTO;
        } catch (IOException e) {
            log.error("Error uploading image to Cloudinary", e);
            throw new InternalServerErrorException("Error al subir la imagen a Cloudinary: " + e.getMessage());
        }
    }

    /**
     * Sobrecarga del método uploadImage sin usuario (para compatibilidad)
     */
    public ImageUploadResponseDTO uploadImage(File file, String folder) {
        return uploadImage(file, folder, null);
    }

    /**
     * Sube una imagen desde un InputStream a Cloudinary
     * @param inputStream El InputStream con los datos de la imagen
     * @param folder La carpeta de destino en Cloudinary (opcional)
     * @param user El usuario que está subiendo la imagen (para notificaciones)
     * @return DTO con la información de la imagen subida
     */
    public ImageUploadResponseDTO uploadImage(InputStream inputStream, String folder, User user) {
        try {
            // Crear un archivo temporal
            Path tempFile = Files.createTempFile("cloudinary-upload-", ".tmp");
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            File file = tempFile.toFile();
            file.deleteOnExit(); // Marcar para eliminación al salir de la JVM

            // Subir el archivo a Cloudinary
            ImageUploadResponseDTO result = uploadImage(file, folder, user);

            // Eliminar el archivo temporal
            Files.delete(tempFile);

            return result;
        } catch (IOException e) {
            log.error("Error creating temporary file or uploading to Cloudinary", e);
            throw new InternalServerErrorException("Error al procesar o subir la imagen: " + e.getMessage());
        }
    }

    /**
     * Sobrecarga del método uploadImage sin usuario (para compatibilidad)
     */
    public ImageUploadResponseDTO uploadImage(InputStream inputStream, String folder) {
        return uploadImage(inputStream, folder, null);
    }

    /**
     * Elimina una imagen de Cloudinary por su public_id
     * @param publicId El public_id de la imagen a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean deleteImage(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (IOException e) {
            log.error("Error deleting image from Cloudinary", e);
            return false;
        }
    }

    /**
     * Mapea el resultado de Cloudinary a nuestro DTO
     */
    private ImageUploadResponseDTO mapToResponseDTO(Map<String, Object> uploadResult) {
        return ImageUploadResponseDTO.builder()
                .publicId((String) uploadResult.get("public_id"))
                .url((String) uploadResult.get("url"))
                .secureUrl((String) uploadResult.get("secure_url"))
                .format((String) uploadResult.get("format"))
                .width(((Number) uploadResult.get("width")).intValue())
                .height(((Number) uploadResult.get("height")).intValue())
                .size(((Number) uploadResult.get("bytes")).longValue())
                .resourceType((String) uploadResult.get("resource_type"))
                .build();
    }
}