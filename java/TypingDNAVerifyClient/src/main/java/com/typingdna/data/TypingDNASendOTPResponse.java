package com.typingdna.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class TypingDNASendOTPResponse extends TypingDNADefaultResponse {
    @Getter
    private final String otp;
}
