package com.csu.mypetstore.controller;

import com.aliyuncs.utils.StringUtils;
import com.csu.mypetstore.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 因不能申请阿里云企业认证
 * 所以只能给特定手机号码发送短信
 * 取消
 */
@RestController
@RequestMapping("/sms")
@CrossOrigin
public class SmsController {
    @Autowired
    SmsService smsService;

    @Resource
    RedisTemplate<String,String> redisTemplate;

    @GetMapping("/{phone}")
    public String sendSms(@PathVariable("phone") String phone){
        System.out.println(phone);
        //得到电话，先看查一下redis中有无存放验证码
        String code = redisTemplate.opsForValue().get(phone);
        //有则返回已存在
        if (!StringUtils.isEmpty(code)){
            return phone+":"+code+"已存在,还没有过期!";
        }else {
            //没有则生成验证码，uuid随机生成四位数验证码
            code = UUID.randomUUID().toString().substring(0,4);   //随机生成四个数形成验证码
            HashMap<String, Object> map = new HashMap<>();
            map.put("code","1145");
            //调用方法发送信息 传入电话，模板，验证码
            boolean send = smsService.addSendSms(phone, "SMS_274560816", map);


            //返回ture则发送成功
            if (send){
                //存入redis中并设置过期时间，这里设置5分钟过期
                redisTemplate.opsForValue().set("phone",code,5, TimeUnit.SECONDS);
                return phone+":"+code+"发送成功!";
            }else {
                //返回false则发送失败
                return "发送失败";
            }
        }
    }
}
