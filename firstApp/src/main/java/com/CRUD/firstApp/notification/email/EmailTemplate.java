package com.CRUD.firstApp.notification.email;

import lombok.Getter;

public enum EmailTemplate {

    REGISTER_CONFIRMATION("register-confirmation.html","register successfully processed");

    @Getter
    private final String template;

    @Getter
    private final String subject;

    EmailTemplate(String template, String subject) {
        this.template = template;
        this.subject = subject;
    }
}
