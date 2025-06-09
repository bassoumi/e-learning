package com.CRUD.firstApp.chat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final WebClient rasaClient;

    public ChatService(WebClient rasaClient) {
        this.rasaClient = rasaClient;
    }
    private Mono<Void> restartConversation(String userId) {
        // facultatif : réinitialiser l’historique si vous le souhaitez
        return rasaClient
                .post()
                .uri("/conversations/{id}/tracker/events", userId)
                .bodyValue(List.of(Map.of("event", "restart")))
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<List<BotMessage>> sendMessage(String userId, String message) {
        Map<String, String> payload = Map.of(
                "sender",  userId,
                "message", message
        );

        return restartConversation(userId)
                .then(rasaClient
                        .post()
                        .uri("/webhooks/rest/webhook")
                        .bodyValue(payload)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<BotMessage>>() {})
                );
    }
}
