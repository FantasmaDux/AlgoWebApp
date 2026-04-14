package io.github.fantasmadux.authmicro.services;

import io.github.fantasmadux.authmicro.store.entities.MailEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    // При удалении FakeMailSender убрать дефолтное значение
    @Value("${mail.username:fake@mail.com}")
    private String username;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void sendEmailForRegistration(MailEntity mail) {
        log.info("Запуск метода отправки сообщений на почту {}", mail.getReceiver());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mail.getReceiver());
        message.setSubject("Ваш код регистрации");
        message.setText(mail.getBody());
        message.setFrom(username);

        try {
            mailSender.send(message);
            log.info("Сообщение отправлено {}", mail.getBody());
        } catch (Exception e) {
            log.error("Ошибка при отправке письма на {}: {}", mail.getReceiver(), e.getMessage(), e);
        }
    }

    @Override
    public void sendEmailForLogin(MailEntity mail) {
        log.info("Запуск метода отправки сообщений на почту {}", mail.getReceiver());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mail.getReceiver());
        message.setSubject("Ваш код для входа");
        message.setText(mail.getBody());
        message.setFrom(username);

        try {
            mailSender.send(message);
            log.info("Сообщение отправлено {}", mail.getBody());
        } catch (Exception e) {
            log.error("Ошибка при отправке письма на {}: {}", mail.getReceiver(), e.getMessage(), e);
        }
    }
}
