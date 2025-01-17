package controller;

import service.NotificationService;

import java.util.UUID;

public class NotificationServiceImpl implements NotificationService {
    @Override
    public void sendNotification(String message, UUID userUUID, String shortlink) {
        System.out.println("Notification for " + userUUID + " on " + shortlink + ": " + message);
    }
}
