package com.example.absensireact.controller;
import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Notifications;
import com.example.absensireact.service.NotificationsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

 import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
@Controller
public class NotificationsController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationsController.class);

    @Autowired
    private NotificationsService notificationsService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public ResponseEntity<?> getAllNotifications() {
        try {
            logger.info("Mengambil semua notifikasi...");
            List<Notifications> notifications = notificationsService.getAllNotif();
            logger.info("Berhasil mengambil {} notifikasi.", notifications.size());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            logger.error("Gagal mengambil notifikasi", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan saat mengambil notifikasi.");
        }
    }

    @GetMapping("/user/getByUser/{userId}")
    public ResponseEntity<?> getNotificationsByUser(@PathVariable Long userId) {
        try {
            logger.info("Mengambil notifikasi untuk user dengan ID: {}", userId);
            List<Notifications> notifications = notificationsService.getNotfiUser(userId);
            logger.info("Ditemukan {} notifikasi untuk user ID: {}", notifications.size(), userId);
            return ResponseEntity.ok(notifications);
        } catch (NotFoundException e) {
            logger.error("Notifikasi tidak ditemukan untuk user ID: {}", userId, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/getById/{id}")
    public ResponseEntity<?> getNotificationsById(@PathVariable Long id) {
        try {
            logger.info("Mengambil notifikasi dengan ID: {}", id);
            Optional<Notifications> notification = notificationsService.getById(id);
            return ResponseEntity.ok(notification);
        } catch (NotFoundException e) {
            logger.error("Notifikasi tidak ditemukan dengan ID: {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/getByAdmin/{adminId}")
    public ResponseEntity<?> getNotificationsByAdmin(@PathVariable Long adminId) {
        try {
            logger.info("Mengambil notifikasi untuk admin dengan ID: {}", adminId);
            List<Notifications> notifications = notificationsService.getNotfiAllByAdminId(adminId);
            logger.info("Ditemukan {} notifikasi untuk admin ID: {}", notifications.size(), adminId);
            return ResponseEntity.ok(notifications);
        } catch (NotFoundException e) {
            logger.error("Notifikasi tidak ditemukan untuk admin ID: {}", adminId, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/user/sendByadmin/{adminId}")
    public ResponseEntity<?> createNotification(@PathVariable Long adminId, @RequestParam Long userId, @RequestBody Notifications message) {
        try {
            logger.info("Mengirim notifikasi dari admin ID: {} ke user ID: {}", adminId, userId);
            Notifications notification = notificationsService.tambahNotif(adminId, userId, message);
            logger.info("Notifikasi berhasil dikirim dengan ID: {}", notification.getId());
            return ResponseEntity.ok(notification);
        } catch (NotFoundException e) {
            logger.error("Gagal mengirim notifikasi dari admin ID: {} ke user ID: {}", adminId, userId, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/user/send/{idAdmin}")
    public ResponseEntity<?> sendNotificationToAllUsers(@PathVariable Long idAdmin, @RequestBody Notifications notifications) {
        try {
            logger.info("Mengirim notifikasi ke semua user dari admin ID: {}", idAdmin);
            Notifications sentNotification = notificationsService.sendToAllUser(idAdmin, notifications);
            messagingTemplate.convertAndSend("/topic/notifications", sentNotification);
            logger.info("Notifikasi berhasil dikirim ke semua user.");
            return ResponseEntity.ok(sentNotification);
        } catch (Exception e) {
            logger.error("Gagal mengirim notifikasi ke semua user dari admin ID: {}", idAdmin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan saat mengirim notifikasi.");
        }
    }

    @PutMapping("/editbyId/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id, @RequestBody Notifications notifications) {
        try {
            logger.info("Mengupdate notifikasi dengan ID: {}", id);
            Notifications updatedNotification = notificationsService.editNotifById(id, notifications);
            logger.info("Notifikasi dengan ID {} berhasil diperbarui.", id);
            return ResponseEntity.ok(updatedNotification);
        } catch (NotFoundException e) {
            logger.error("Gagal mengupdate notifikasi dengan ID: {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/editby/user/{userId}")
    public ResponseEntity<?> updateNotificationByUserId(@PathVariable Long userId, @RequestBody Notifications notifications) {
        try {
            logger.info("Mengupdate notifikasi untuk user ID: {}", userId);
            Notifications updatedNotification = notificationsService.editNotifByUserId(userId, notifications);
            logger.info("Notifikasi untuk user ID {} berhasil diperbarui.", userId);
            return ResponseEntity.ok(updatedNotification);
        } catch (NotFoundException e) {
            logger.error("Gagal mengupdate notifikasi untuk user ID: {}", userId, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        try {
            logger.info("Menghapus notifikasi dengan ID: {}", id);
            boolean success = notificationsService.deleteNotif(id);
            if (success) {
                logger.info("Notifikasi dengan ID {} berhasil dihapus.", id);
                return ResponseEntity.ok("Notification deleted successfully.");
            } else {
                logger.warn("Notifikasi tidak ditemukan dengan ID: {}", id);
                return ResponseEntity.badRequest().body("Notification not found with ID: " + id);
            }
        } catch (Exception e) {
            logger.error("Gagal menghapus notifikasi dengan ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan saat menghapus notifikasi.");
        }
    }

    @DeleteMapping("/delete-sementara/{id}")
    public ResponseEntity<?> deleteSemenetara(@PathVariable Long id) {
        try {
            logger.info("Memindahkan notifikasi ID {} ke sampah.", id);
            notificationsService.DeleteNotifSementara(id);
            return ResponseEntity.ok("Notif berhasil dipindahkan ke sampah");
        } catch (NotFoundException e) {
            logger.error("Notif tidak ditemukan dengan ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notif tidak ditemukan dengan id: " + id);
        }
    }

    @PutMapping("/pemulihan-kelas/{id}")
    public ResponseEntity<?> PemulihanNotif(@PathVariable Long id) {
        try {
            logger.info("Memulihkan notifikasi dengan ID: {}", id);
            notificationsService.PemulihanDataNotif(id);
            return ResponseEntity.ok("Notif berhasil Dipulihkan");
        } catch (NotFoundException e) {
            logger.error("Gagal memulihkan notifikasi dengan ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notif tidak ditemukan dengan id: " + id);
        }
    }
}