package io.github.fantasmadux.authmicro.services;


import io.github.fantasmadux.authmicro.store.entities.MailEntity;
import org.springframework.stereotype.Service;

public interface EmailService {
    void sendEmailForRegistration(MailEntity mail);

    void sendEmailForLogin(MailEntity mail);
}
