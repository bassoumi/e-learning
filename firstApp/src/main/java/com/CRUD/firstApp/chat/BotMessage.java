package com.CRUD.firstApp.chat;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BotMessage {

    private String recipient_id;
    private String text;
}
