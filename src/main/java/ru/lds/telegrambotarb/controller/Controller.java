package ru.lds.telegrambotarb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.lds.telegrambotarb.service.TelegramBot;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Controller {
    private final TelegramBot telegramBot;

    @PostMapping("/callback/update")
    public ResponseEntity<?> getUpdate(@RequestBody Update update) {
        var from = update.getMessage().getFrom();
        log.info("Request info: {} {}, id: {}", from.getFirstName(), from.getLastName(), from.getId());
        return ResponseEntity.ok(telegramBot.onWebhookUpdateReceived(update));
    }
}
