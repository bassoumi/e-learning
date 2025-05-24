package com.CRUD.firstApp.notification.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;



@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendRegistrationConfirmationEmail(String to, String firstName, String lastName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            // config of our html type and ecriture :  garantit que les caractères accentués (é, è, à, etc.)
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            //expediteur
            helper.setFrom("noreply@tondomaine.com");
            //destinataire
            helper.setTo(to);
            //add the object "email object"
            helper.setSubject(EmailTemplate.REGISTER_CONFIRMATION.getSubject());

            // Prépare le contexte Thymeleaf
            Context ctx = new Context();
            ctx.setVariable("firstName", firstName);
            ctx.setVariable("lastName", lastName);
            ctx.setVariable("email", to);

            // Génère le HTML
            String html = templateEngine.process(
                    EmailTemplate.REGISTER_CONFIRMATION.getTemplate(),
                    ctx
            );
            helper.setText(html, true);

            mailSender.send(mimeMessage);
            log.info("E‑mail d'inscription envoyé à {}", to);
        } catch (MessagingException ex) {
            log.warn("Impossible d’envoyer l’e‑mail d’inscription à {}: {}", to, ex.getMessage());
        }
    }
}


