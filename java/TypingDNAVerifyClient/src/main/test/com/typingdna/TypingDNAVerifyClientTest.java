package com.typingdna;

import com.typingdna.data.TypingDNAVerifyPayload;
import com.typingdna.exception.TypingDNAVerifyException;
import lombok.val;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TypingDNAVerifyClientTest {
    private static final String APPLICATION_ID = "";
    private static final String CLIENT_ID = "";
    private static final String SECRET = "";

    private static final String CLIENT_EMAIL = "";
    private static final String CLIENT_PHONE = "";
    private static final String CLIENT_COUNTRY_CODE = "";
    private static final String CLIENT_LANG = "en";

    private TypingDNAVerifyClient client;

    @Before
    public void setUp() {
        client = new TypingDNAVerifyClient(APPLICATION_ID, CLIENT_ID, SECRET);
    }

    @Test
    public void testSendOTP() throws TypingDNAVerifyException {
        val payload = new TypingDNAVerifyPayload(CLIENT_EMAIL, CLIENT_PHONE, CLIENT_COUNTRY_CODE, CLIENT_LANG);
        val response = client.sendOTP(payload);
        Assert.assertTrue(response.isSuccess());
        Assert.assertNotNull(response.getOtp());
    }
}
