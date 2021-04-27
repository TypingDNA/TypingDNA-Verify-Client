package com.typingdna;

import com.typingdna.exception.TypingDNAVerifyException;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

class TypingDNAVerifyAPI {
    private static TypingDNAVerifyAPI instance = null;
    @Getter @Setter
    private String api = "https://verify.typingdna.com";

    private TypingDNAVerifyAPI() {}

    public static TypingDNAVerifyAPI getInstance() {
        if (instance == null) {
            instance = new TypingDNAVerifyAPI();
        }

        return instance;
    }

    public String request(String path, String json) throws TypingDNAVerifyException {
        val httpClient = HttpClients.createDefault();
        val httpPost = new HttpPost(String.format("%s%s", api, path));

        try {
            val entity = new StringEntity(json);
            httpPost.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            throw new TypingDNAVerifyException("The request encoding is unsupported");
        }

        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        try {
            val response = httpClient.execute(httpPost);
            val body = EntityUtils.toString(response.getEntity());
            httpClient.close();

            return body;
        } catch (IOException e) {
            throw new TypingDNAVerifyException(String.format("Request to %s%s failed", api, path));
        }
    }
}
