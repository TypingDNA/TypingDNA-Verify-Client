package com.typingdna.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class TypingDNAVerifyPayload {
    private final String email;
    private final String phoneNumber;
    private String language = "EN";
    private String mode = "standard";
}
