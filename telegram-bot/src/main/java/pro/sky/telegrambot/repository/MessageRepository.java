package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}