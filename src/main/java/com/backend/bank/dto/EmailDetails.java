package com.backend.bank.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {
    private String receiver;
    private String subject;
    private String body;
    private String attachment;
}
