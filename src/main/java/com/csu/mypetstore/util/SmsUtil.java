package com.csu.mypetstore.util;

import com.twilio.Twilio;
import com.twilio.converter.Promoter;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.type.PhoneNumber;

import java.net.URI;
import java.math.BigDecimal;

public class SmsUtil {
    // Find your Account Sid and Token at twilio.com/console
    public static final String ACCOUNT_SID = "AC172145598a55d4076fa6b094477d19a8";
    public static final String AUTH_TOKEN = "9abff88d1b9fec170a093398c5326972";

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }
//    public void send(String num,String msg) {
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//        Message message = Message.creator(
//                new PhoneNumber(num),
//                new PhoneNumber("+15155828701"),
//                msg
//                        .create());
//
//        System.out.println(message.getSid());
//    }

    public static void sendSmsVerification(String phoneNumber, String countryCode) throws TwilioException {
        Verification verification = Verification.creator(
                ACCOUNT_SID,
                phoneNumber,
                countryCode
        ).create();

        System.out.println(verification.getStatus());
    }

    public static void main(String[] args) {
        SmsUtil.sendSmsVerification("+8619573137241","你好");
    }
}
