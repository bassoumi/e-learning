package com.CRUD.firstApp.chat;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatbotService;

    public ChatController(ChatService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/message")
    public Mono<List<BotMessage>> postMessage(@RequestBody UserMessage userMessage) {
        return chatbotService.sendMessage(userMessage.getUserId(), userMessage.getText());
    }
}
