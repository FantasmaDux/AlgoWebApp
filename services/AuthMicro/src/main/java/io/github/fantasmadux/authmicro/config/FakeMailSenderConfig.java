package io.github.fantasmadux.authmicro.config;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.InputStream;

/**
 * Фейковый mail sender для разработки.
 * Подменяет настоящий, чтобы приложение билдилось без реальных почты и пароля.
 */
@Configuration
@Primary
public class FakeMailSenderConfig {
    private static final Logger log = LoggerFactory.getLogger(FakeMailSenderConfig.class);

    @Bean
    public JavaMailSender mailSender() {
        return new JavaMailSender() {
            @Override
            public void send(SimpleMailMessage... simpleMessages) throws MailException {
                for (SimpleMailMessage message : simpleMessages) {
                    log.info("Отправлено фальшивое сообщение на почту {}", message.getTo());
                }
            }

            @Override
            public MimeMessage createMimeMessage() {
                return null;
            }

            @Override
            public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
                return null;
            }

            @Override
            public void send(MimeMessage... mimeMessages) throws MailException {

            }
        };
    }
}
