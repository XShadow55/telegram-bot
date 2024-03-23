package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.notificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener  {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final notificationTaskRepository taskRepository;
    String input = "01.01.2022 20:00 Сделать домашнюю работу";
    String patterns = "([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)";

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    public TelegramBotUpdatesListener(notificationTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public int process(List<Update> updates) {
       for(Update update:updates){
           String messageText = update.message().text();
           if (messageText.equals("/start")) {
               String welcomeMessage = "Привет! Я рад приветствовать тебя в нашем боте. ";
               SendMessage message = new SendMessage(update.message().chat().id(),welcomeMessage);
               telegramBot.execute(message);
           }
           Pattern pattern = Pattern.compile(patterns);
           logger.info(update.message().text());
           Matcher matcher = pattern.matcher(input);
           if (matcher.matches()) {
               String date = matcher.group(1);
               String item = matcher.group(3);
               logger.info(matcher.group(3));
               String welcomeMessage = "Введи сообщение для напоминания: ";
               SendMessage message = new SendMessage(update.message().chat().id(),welcomeMessage);
               telegramBot.execute(message);
               LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
               NotificationTask notificationTask = new NotificationTask(update.message().chat().id(),item,dateTime);
               taskRepository.save(notificationTask);
           }

        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
    @Scheduled(cron = "0 0/1 * * * *")
    public void  run() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> entities = taskRepository.findByExecutionTime(now);
        for (int i=0;i < entities.size();i++) {
            SendMessage message = new SendMessage(entities.get(i).getChat_id(), entities.get(i).getMessage());
            telegramBot.execute(message);
        }
    }


}
