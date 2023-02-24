package com.dev.vlpr.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailParamsDTO {
    private String id;
    private String emailTo;
}