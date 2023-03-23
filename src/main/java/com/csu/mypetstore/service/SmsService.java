package com.csu.mypetstore.service;


import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface SmsService {
    boolean addSendSms(String PhoneNumbers, String TemplateCode, Map code);
}