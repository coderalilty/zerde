package kidd.house.zerde.service;

import com.vdurmont.emoji.EmojiParser;
import kidd.house.zerde.config.TelegramBotConfig;
import kidd.house.zerde.model.entity.Information;
import kidd.house.zerde.model.entity.Parent;
import kidd.house.zerde.repo.InformationRepo;
import kidd.house.zerde.repo.ParentRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TelegramService extends TelegramLongPollingBot {
    @Autowired
    private ParentRepo parentRepo;
    @Autowired
    private InformationRepo informationRepo;
    final TelegramBotConfig config;
    static final String HELP_TEXT = """
            This bot is created to demonstration spring boot

            Type /start message end starting

            Type /help Telegram Bot commands""";
    static final String ERROR_TEXT = "Error occurred";
    public TelegramService(TelegramBotConfig config) {
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start","get a welcome message"));
        listofCommands.add(new BotCommand("/mydata","get your fata stored"));
        listofCommands.add(new BotCommand("/deletedata","delete my data"));
        listofCommands.add(new BotCommand("/help","info how to use this bot"));
        listofCommands.add(new BotCommand("/settings","set your preferences"));
        try{
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(),null));
        } catch (TelegramApiException e){
            log.error("Error setting bots command list: " + e.getMessage());
        }
    }
    @Override
    public String getBotToken() {
        return config.getBotToken();
    }
    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            System.out.println("Получен chatId: " + chatId);
            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId,update.getMessage().getChat().getFirstName());
                    registerUser(update.getMessage());
                    break;
                case "/help":
                    sendMessageToChat(chatId,HELP_TEXT);
                    break;
                default: sendMessageToChat(chatId, "Sorry, command was not recognized");
            }
        }
    }

    private void registerUser(Message message) {
        if (parentRepo.findByChatId(message.getChatId()).isEmpty()){

            var chatId = message.getChatId();
            var chat = message.getChat();
            Parent parent = new Parent();
            parent.setChatId(chatId);
            parent.setParentName(chat.getFirstName());
            parent.setLastName(chat.getLastName());
            parent.setMiddleName(chat.getUserName());
            parent.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            parentRepo.save(parent);
            log.info("user saved " + parent);
        }
    }
    private void startCommandReceived(long chatId,String name){
        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + " :blush:");
        log.info("Replied to user " + name);
        findButtonsOnStart(chatId, answer);
    }
    public void findButtonsOnStart(Long chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messageText);
        // Настройка кнопок
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("weather");
        row.add("get random joke");

        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("register");
        row.add("check my data");
        row.add("delete my data");
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        System.out.println("Отправка сообщения в Telegram: " + messageText);
        executeMessage(message);

    }
    //метод для отправки уведомлений в Telegram
    public void sendMessageToChat(Long chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messageText);
        executeMessage(message);
    }
    private void executeMessage(SendMessage message){
        try {
            execute(message);
            System.out.println("Сообщение отправлено успешно.");
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }
    @Scheduled(cron = "${cron.scheduler}")
    private void sendMessage(){
        var information = informationRepo.findAll();
        var parents = parentRepo.findAll();
        for (Information information1: information){
            for (Parent parent: parents){
                sendMessageToChat(parent.getChatId(),information1.getText());
            }
        }
    }
}
