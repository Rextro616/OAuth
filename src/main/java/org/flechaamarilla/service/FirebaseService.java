package org.flechaamarilla.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ApplicationScoped
public class FirebaseService {

    @ConfigProperty(name = "firebase.credentials.path")
    String firebaseCredentialsPath;

    @ConfigProperty(name = "firebase.notification.enabled", defaultValue = "true")
    boolean notificationsEnabled;

    private FirebaseApp firebaseApp;
    /**
     * -- GETTER --
     *  Verifica si el servicio está inicializado correctamente
     */
    @Getter
    private boolean initialized = false;

    @PostConstruct
    void initialize() {
        if (!notificationsEnabled) {
            log.info("Firebase notifications are disabled. Skipping initialization.");
            return;
        }

        try {
            FileInputStream serviceAccount = new FileInputStream(firebaseCredentialsPath);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                firebaseApp = FirebaseApp.initializeApp(options);
            } else {
                firebaseApp = FirebaseApp.getInstance();
            }

            initialized = true;
            log.info("Firebase initialized successfully");
        } catch (IOException e) {
            log.error("Failed to initialize Firebase", e);
        }
    }

    /**
     * Envía una notificación a un token FCM específico
     */
    public boolean sendNotification(String token, String title, String body) {
        return sendNotification(token, title, body, null);
    }

    /**
     * Envía una notificación a un token FCM específico con datos adicionales
     */
    public boolean sendNotification(String token, String title, String body, Map<String, String> data) {
        if (!notificationsEnabled || !initialized || token == null || token.isEmpty()) {
            log.info("Notification not sent: notifications disabled, not initialized, or invalid token");
            return false;
        }

        try {
            // Construir el mensaje
            Message.Builder messageBuilder = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());

            // Añadir datos adicionales si existen
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            // Enviar el mensaje
            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            log.info("Successfully sent notification: {}", response);
            return true;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send notification to token: {}", token, e);
            return false;
        }
    }

    /**
     * Envía una notificación a múltiples tokens FCM
     */
    public int sendMulticastNotification(List<String> tokens, String title, String body, Map<String, String> data) {
        if (!notificationsEnabled || !initialized || tokens == null || tokens.isEmpty()) {
            log.info("Multicast notification not sent: notifications disabled, not initialized, or no tokens");
            return 0;
        }

        try {
            // Construir el mensaje multicast
            MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());

            // Añadir datos adicionales si existen
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            // Enviar el mensaje
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(messageBuilder.build());
            log.info("Multicast messages sent: {} successful, {} failed",
                    response.getSuccessCount(), response.getFailureCount());
            return response.getSuccessCount();
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send multicast notification", e);
            return 0;
        }
    }

    /**
     * Envía una notificación a un tema específico
     */
    public boolean sendTopicNotification(String topic, String title, String body, Map<String, String> data) {
        if (!notificationsEnabled || !initialized) {
            log.info("Topic notification not sent: notifications disabled or not initialized");
            return false;
        }

        try {
            // Construir el mensaje para el tema
            Message.Builder messageBuilder = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());

            // Añadir datos adicionales si existen
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            // Enviar el mensaje
            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            log.info("Successfully sent notification to topic {}: {}", topic, response);
            return true;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send notification to topic: {}", topic, e);
            return false;
        }
    }

    /**
     * Suscribe tokens a un tema
     */
    public void subscribeToTopic(List<String> tokens, String topic) {
        if (!notificationsEnabled || !initialized) {
            log.info("Topic subscription skipped: notifications disabled or not initialized");
            return;
        }

        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .subscribeToTopic(tokens, topic);
            log.info("Successfully subscribed to topic {}: {} successful, {} failed",
                    topic, response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("Failed to subscribe to topic: {}", topic, e);
        }
    }

    /**
     * Cancela suscripción de tokens a un tema
     */
    public void unsubscribeFromTopic(List<String> tokens, String topic) {
        if (!notificationsEnabled || !initialized) {
            log.info("Topic unsubscription skipped: notifications disabled or not initialized");
            return;
        }

        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(tokens, topic);
            log.info("Successfully unsubscribed from topic {}: {} successful, {} failed",
                    topic, response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("Failed to unsubscribe from topic: {}", topic, e);
        }
    }

}