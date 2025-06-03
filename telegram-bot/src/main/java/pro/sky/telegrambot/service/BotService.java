package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.TelegramRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
import org.springframework.stereotype.Service;

@Service
public class BotService {

    public void sendMessage(Long chatId, String message) {
        // Logic to send a message to the chat
        System.out.println("Sending message to chat " + chatId + ": " + message);
        // Here you would integrate with the actual Telegram Bot API
    }
    private final TelegramRepository telegramRepository;
    private final TelegramBot telegramBot;

    @Autowired
    public BotService(TelegramRepository telegramRepository, TelegramBot telegramBot) {
        this.telegramRepository = telegramRepository;
        this.telegramBot = telegramBot;
    }

    Set<NotificationTask> basesTextDateTime = new HashSet<>();

    private Logger logger = LoggerFactory.getLogger(BotService.class);

    //Отправляем сообщение
    public void sendingMessage(Long chatId, String string) {
        SendMessage message = new SendMessage(String.valueOf(chatId), string);
        controlSendingControl(telegramBot.execute(message));
    }

    //заносим в Set первоначальные исходные данные объектов при запуске приложения
    public void initialization() {
        basesTextDateTime = telegramRepository.listNotification(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }

    public void processingMessages(Long chatId, String messageText) {
        Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        Matcher matcher = pattern.matcher(messageText);
        // сортируем сообщение + дату
        if (matcher.matches()) {
            LocalDateTime data = LocalDateTime.parse(matcher.group(1), formatter);
            String item = matcher.group(3);
            setSql(chatId, item, data, LocalDateTime.now());
            //очищаем коллекцию после ввода нового сообщения
            basesTextDateTime.clear();
            //заносим в Set данные объектов у которых время равно или превышает текущее
            initialization();
            logger.info("Сообщение \" {}  \" отправлено с датой {} ", item, data);
        } else {
            setSql(chatId, messageText, null, LocalDateTime.now());
            logger.info("Сообщение \" {} \" отправлено без даты", messageText);
        }
    }

    @Scheduled(cron = "0/59 * * * * ?")
    public void executeTask() {
        //проводим поиск элементов с текущим временем
        List<NotificationTask> listObjectEqualsDate = basesTextDateTime.stream().filter(object -> object.getDateTimeNotification()
                .equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))).collect(Collectors.toList());
        if (listObjectEqualsDate != null) {
            //выводим сообщение с текущей датой в телеграмм
            listObjectEqualsDate.forEach(variable -> sendingMessage(variable.getChatId(), variable.getTextNotification()));
            //удаляем данные отработанные из basesTextDateTime
            listObjectEqualsDate.stream().map(object -> basesTextDateTime.remove(object));
        }
    }

    //записываем данные сообщения в базу данных
    private void setSql(Long chatId, String textMessage, LocalDateTime dateTimeNotatification, LocalDateTime dateTimesDepartures) {
        NotificationTask sql = new NotificationTask(chatId, textMessage, dateTimeNotatification, dateTimesDepartures);
        telegramRepository.save(sql);
    }

    private void controlSendingControl(SendResponse sendResponse) {
        if (sendResponse.isOk()) {
            logger.info("Сообщение отправлено");
            return;
        }
        logger.info("Ошибка, сообщение не отправлено");
    }
}
