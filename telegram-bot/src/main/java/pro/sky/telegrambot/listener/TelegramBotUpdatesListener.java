package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    private static final Pattern MESSAGE_PATTERN = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (update.message() != null) {
                String messageText = update.message().text();
                Matcher matcher = MESSAGE_PATTERN.matcher(messageText);
                if (matcher.matches()) {
                    String dateTimeString = matcher.group(1);
                    String reminderText = matcher.group(3);

                    LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

                    NotificationTask notificationTask = new NotificationTask(
                        update.message().chat().id(),
                        reminderText,
                        dateTime,
                        LocalDateTime.now() // Assuming this is the intended fourth parameter
                    );

                    notificationTaskRepository.save(notificationTask);

                    telegramBot.execute(new SendMessage(update.message().chat().id(), "Reminder saved successfully!"));
                } else if ("/start".equals(messageText)) {
                    long chatId = update.message().chat().id();
                    telegramBot.execute(new SendMessage(chatId, "Welcome to our Telegram Bot!"));
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}