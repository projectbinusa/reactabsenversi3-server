package com.example.absensireact.controller;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Notifications;
import com.example.absensireact.service.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

    @Autowired
    private NotificationsService notificationsService;

    @GetMapping
    public List<Notifications> getAllNotifications() {
        return notificationsService.getAllNotif();
    }

    @GetMapping("/user/getByUser/{userId}")
    public ResponseEntity<?> getNotificationsByUser(@PathVariable Long userId) {
        try {
            List<Notifications> notifications = notificationsService.getNotfiUser(userId);
            return ResponseEntity.ok(notifications);
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/getByAdmin/{adminId}")
    public ResponseEntity<?> getNotificationsByAdmin(@PathVariable Long adminId) {
        try {
            List<Notifications> notifications = notificationsService.getNotfiAllByAdminId(adminId);
            return ResponseEntity.ok(notifications);
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/user/sendByadmin/{adminId}")
    public ResponseEntity<?> createNotification(@PathVariable Long adminId , @RequestParam Long userId , @RequestBody Notifications message) {
        try {
            Notifications notification = notificationsService.tambahNotif(adminId , userId, message);
            return ResponseEntity.ok(notification);
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/user/send/{idAdmin}")
    public ResponseEntity<Notifications> sendNotificationToAllUsers(
            @PathVariable Long idAdmin,
            @RequestBody Notifications notifications) {

        Notifications sentNotification = notificationsService.sendToAllUser(idAdmin, notifications);
        return ResponseEntity.ok(sentNotification);
    }

    @PutMapping("/editbyId/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id, @RequestBody Notifications notifications) {
        try {
            Notifications updatedNotification = notificationsService.editNotifById(id, notifications);
            return ResponseEntity.ok(updatedNotification);
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/editby/user/{userId}")
    public ResponseEntity<?> updateNotificationByUserId(@PathVariable Long userId, @RequestBody Notifications notifications) {
        try {
            Notifications updatedNotification = notificationsService.editNotifByUserId(userId, notifications);
            return ResponseEntity.ok(updatedNotification);
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        boolean success = notificationsService.deleteNotif(id);
        if (success) {
            return ResponseEntity.ok("Notification deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Notification not found with ID: " + id);
        }
    }
}
