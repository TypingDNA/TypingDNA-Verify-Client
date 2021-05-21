package com.typingdna;

import com.google.gson.Gson;
import com.typingdna.data.TypingDNASendOTPResponse;
import com.typingdna.data.TypingDNAVerifyDataAttributes;
import com.typingdna.data.TypingDNAValidateOTPResponse;
import com.typingdna.data.TypingDNAVerifyPayload;
import com.typingdna.exception.TypingDNAVerifyException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;

import java.util.HashMap;

@AllArgsConstructor
public class TypingDNAVerifyClient {
    @NonNull private final String applicationId;
    @NonNull private final String clientId;
    @NonNull private final String secret;
    private final TypingDNAVerifyAPI api = TypingDNAVerifyAPI.getInstance();
    private final float VERSION = 1.1f;

    public TypingDNAVerifyDataAttributes getDataAttributes(TypingDNAVerifyPayload payload) throws TypingDNAVerifyException {
        return new TypingDNAVerifyDataAttributes(clientId, applicationId, encryptPayload(payload));
    }

    public TypingDNASendOTPResponse sendOTP(TypingDNAVerifyPayload payload) throws TypingDNAVerifyException {
        val data = getRequestParameters(payload);

        val gson = new Gson();
        val response = api.request("/otp/send", gson.toJson(data));
        return gson.fromJson(response, TypingDNASendOTPResponse.class);
    }

    public TypingDNAValidateOTPResponse validateOTP(TypingDNAVerifyPayload payload, String code) throws TypingDNAVerifyException {
        val data = getRequestParameters(payload);
        data.put("code", code);

        val gson = new Gson();
        val response = api.request("/otp/validate", gson.toJson(data));
        return gson.fromJson(response, TypingDNAValidateOTPResponse.class);
    }

    private String encryptPayload(TypingDNAVerifyPayload payload) throws TypingDNAVerifyException {
        val gson = new Gson();
        val payloadJSON = gson.toJson(payload);
        return TypingDNAEncryption.encrypt(payloadJSON, secret, applicationId);
    }

    private HashMap<String, String> getRequestParameters(TypingDNAVerifyPayload payload) throws TypingDNAVerifyException {
        val data = new HashMap<String, String>();
        data.put("clientId", clientId);
        data.put("applicationId", applicationId);
        data.put("payload", encryptPayload(payload));
        data.put("version", String.valueOf(VERSION));

        return data;
    }
}
