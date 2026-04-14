package io.github.fantasmadux.authmicro.store.entities;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailEntity {
    private String receiver;
    private String body;
}
