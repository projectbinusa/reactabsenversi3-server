package com.example.absensireact.service;

import com.example.absensireact.model.Notifications;

import java.util.List;
import java.util.Optional;

public interface NotificationsService {
    List<Notifications> getAllNotif();

    List<Notifications>getNotfiUser(Long userId);

    Optional<Notifications> getById(Long id);

    List<Notifications>getNotfiAllByAdminId(Long adminId);

    Notifications sendToAllUser(Long idAdmin, Notifications notifications);



    Notifications tambahNotif(Long idAdmin, Long userId, Notifications notifications);

    Notifications editNotifById(Long id, Notifications notifications);

    Notifications editNotifByUserId(Long userId, Notifications notifications);

    boolean deleteNotif(Long id);

    void DeleteNotifSementara(Long id);

    void PemulihanDataNotif(Long id);
}
