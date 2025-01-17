package service;

import java.util.UUID;

public interface NotificationService {
    void sendNotification(String message, UUID userUUID, String shortlink);
}
