package pro.sky.telegrambot.repository;

import javax.management.Notification;
import pro.sky.telegrambot.model.NotificationTask;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository {
    List<NotificationTask> findNotificationsByTime(LocalDateTime time);
}
