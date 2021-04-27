package com.typingdna.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TypingDNAVerifyDataAttributes {
    private final String clientId;
    private final String applicationId;
    private final String payload;
}
