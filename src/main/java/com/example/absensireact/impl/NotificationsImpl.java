package com.example.absensireact.impl;

import com.example.absensireact.exception.NotFoundException;
import com.example.absensireact.model.Admin;
import com.example.absensireact.model.Notifications;
import com.example.absensireact.model.User;
import com.example.absensireact.repository.AdminRepository;
import com.example.absensireact.repository.NotificationsRepository;
import com.example.absensireact.repository.UserRepository;
import com.example.absensireact.service.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationsImpl implements NotificationsService {

    @Autowired
    private NotificationsRepository  notificationsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public List<Notifications>getAllNotif(){
        return notificationsRepository.findAll();
    }

    @Override
    public List<Notifications>getNotfiUser(Long userId){
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User id tiak ditemukan dengan id :" + userId);
        }
      return notificationsRepository.findnotifByUserId(userId);
    }

    @Override
    public List<Notifications>getNotfiAllByAdminId(Long adminId){
        Optional<Admin> adminoptional = adminRepository.findById(adminId);
        if (adminoptional.isEmpty()) {
            throw new NotFoundException("admin id tiak ditemukan dengan id :" + adminId);
        }
        return notificationsRepository.findnotifByAdmin(adminId);
    }

    @Override
    public Notifications sendToAllUser(Long adminId, Notifications notifications) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("id admin tidak ditemukan : " + adminId));

        List<User> users = userRepository.findByIdAdmin(adminId);

        if (users.isEmpty()) {
            throw new NotFoundException("Tidak ada user yang terdaftar dengan id admin: " + adminId);
        }
        for (User user : users) {

            notifications.setAdmin(admin);
            notifications.setMessage(notifications.getMessage());
            notifications.setUser(user);
            notifications.setNamaAcara(notifications.getNamaAcara());
            notifications.setTanggalAcara(notifications.getTanggalAcara());
            notifications.setTempatAcara(notifications.getTempatAcara());
            notificationsRepository.save(notifications);
        }

        return notifications;
    }

    @Override
    public Notifications tambahNotif(Long adminId , Long userId, Notifications notifications){
        Optional<Admin> adminOptional = adminRepository.findById(adminId);
        if (adminOptional.isEmpty()) {
            throw new NotFoundException("Admin id tidak ditemukan dengan id :" + adminId);
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User id tidak ditemukan dengan id :" + userId);
        }
        User user = userOptional.get();
        Admin admin = adminOptional.get();
        Date newDate = new Date();
        notifications.setAdmin(admin);
        notifications.setMessage(notifications.getMessage());
        notifications.setUser(user);
        notifications.setNamaAcara(notifications.getNamaAcara());
        notifications.setTanggalAcara(notifications.getTanggalAcara());
        notifications.setTempatAcara(notifications.getTempatAcara());
        notifications.setCreatedAt(newDate);
        return notificationsRepository.save(notifications);
    }

    @Override
    public Notifications editNotifById(Long id, Notifications notificationsUpdate){
        Optional<Notifications> notifcationsOptional = notificationsRepository.findById(id);
        if (notifcationsOptional.isEmpty()) {
            throw new NotFoundException("Id Notif tidak ditemukan dengan id :" + id);
        }
        Notifications notification = notifcationsOptional.get();
        notification.setMessage(notificationsUpdate.getMessage());
        notification.setTempatAcara(notificationsUpdate.getTempatAcara());
        notification.setNamaAcara(notificationsUpdate.getNamaAcara());
        return notificationsRepository.save(notification);
    }

//    gagall
    @Override
    public Notifications editNotifByUserId(Long userId, Notifications notifications){
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Id User tidak ditemukan dengan id :" + userId);
        }
        Date newDate = new Date();
        User user = userOptional.get();
        notifications.setMessage(notifications.getMessage());
        notifications.setCreatedAt(newDate);
        notifications.setUser(user);

        return notificationsRepository.save(notifications);
    }

    @Override
    public boolean deleteNotif(Long id){
        Optional<Notifications> notificationsOptional = notificationsRepository.findById(id);
        if (notificationsOptional.isPresent()) {
            notificationsRepository.deleteById(id);
            return true;
        }
        return false;

    }

}
