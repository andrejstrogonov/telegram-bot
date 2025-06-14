package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;

    private String notificationText;

    private LocalDateTime sendDatetime;

    // Constructor
    public NotificationTask(Long chatId, String notificationText, LocalDateTime sendDatetime, LocalDateTime dateTimesDepartures) {
        this.chatId = chatId;
        this.notificationText = notificationText;
        this.sendDatetime = sendDatetime;
        // Assuming dateTimesDepartures is used for some purpose, otherwise it can be removed
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public LocalDateTime getSendDatetime() {
        return sendDatetime;
    }

    public void setSendDatetime(LocalDateTime sendDatetime) {
        this.sendDatetime = sendDatetime;
    }
}
