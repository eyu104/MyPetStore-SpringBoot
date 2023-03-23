package com.csu.mypetstore.config;

import com.twilio.http.TwilioRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {


    /**
     * twilio平台发送短信验证码
     * 因为是国外平台
     * 被拦截了
     * @return
     */
    @Bean
    public TwilioRestClient twilioRestClient() {
        return new TwilioRestClient.Builder("AC172145598a55d4076fa6b094477d19a8",
                "9abff88d1b9fec170a093398c5326972").build();
    }
}