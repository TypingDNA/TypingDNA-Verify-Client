package com.typingdna.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
class TypingDNADefaultResponse {
    private int success;
    @Getter
    private int code;
    @Getter
    private String message;
    @Getter
    private int status;

    public boolean isSuccess() {
        return success == 1;
    }
}
