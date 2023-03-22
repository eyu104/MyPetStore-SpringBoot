package com.csu.mypetstore.service;

import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    @Autowired
    private TwilioRestClient twilioRestClient;

    public void sendSms(String toPhoneNumber, String smsBody) {
        Message message = Message.creator(
                new PhoneNumber("+8619573137241"),
                new PhoneNumber("+15155828701"),
                smsBody
        ).create(twilioRestClient);
    }
}