package ru.lds.telegram.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.lds.telegram.bot.UpdateProcessor;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Controller {
    private final UpdateProcessor updateProcessor;

    @PostMapping("/callback/update")
    public ResponseEntity<?> getUpdate(@RequestBody Update update) {
        updateProcessor.processUpdate(update);
        return ResponseEntity.ok().build();
    }
}
