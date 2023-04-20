package com.csu.mypetstore.controller;

import com.csu.mypetstore.config.Result;
import com.csu.mypetstore.domain.DTO.EmailDTO;
import com.csu.mypetstore.domain.ToEmail;
import com.csu.mypetstore.util.VerCodeGenerateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * TODO 邮箱验证码
 *
 * @author DB
 * <br>CreateDate 2021/9/13 0:35
 */
@RestController
@RequestMapping("email")

public class EmailController {
    //	引入邮件接口
    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    RedisTemplate<String,String> redisTemplate;



    //	获得发件人信息
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 懒得写service，凑合用
     * 使用：http://localhost:9099/email/sendEmail?tos=邮箱地址
     * @param toEmail
     * @return
     */
    @PostMapping("sendEmail")
    public Result<?> sendEmail(ToEmail toEmail) {
//        创建邮件消息
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);

        message.setTo(toEmail.getTos());

        message.setSubject("您本次的验证码是");

        String verCode = VerCodeGenerateUtil.generateVerCode();

        message.setText("尊敬的用户,您好:\n"
                + "\n本次请求的邮件验证码为:" + verCode + ",本验证码 5 分钟内效，请及时输入。（请勿泄露此验证码）\n"
                + "\n如非本人操作，请忽略该邮件。\n(这是一封通过自动发送的邮件，请不要直接回复）");


        javaMailSender.send(message);
//        User user = QcbyContext.getUser(request.getHeader("token"));
//        user.setVerCode(verCode);

        //过期时间5分钟
        redisTemplate.opsForValue().set(toEmail.getTos()[0],verCode,5,TimeUnit.MINUTES);
        return Result.success("发送成功" + toEmail.getTos()[0]);
    }

    @GetMapping("/checkEmail")
    public Result<?> checkEmail(@RequestParam("tos") String tos1,@RequestParam String verCode) {
        ToEmail toEmail = new ToEmail();
        String[] tos = new String[1];
        tos[0] = tos1;
        toEmail.setTos(tos);

        String getCode = redisTemplate.opsForValue().get(toEmail.getTos()[0]);
        if (getCode != null && getCode.toLowerCase().equals(verCode.toLowerCase())){
            //验证后删除对应key
            redisTemplate.delete(toEmail.getTos()[0]);
            return Result.success(verCode);
        }else {
            System.out.println(redisTemplate.opsForValue().get(toEmail.getTos()[0]));
            return Result.error("50","验证码错误");
        }
//        System.out.println(redisTemplate.opsForValue().get(toEmail.getTos()[0]));
//        return Result.success(redisTemplate.opsForValue().get(toEmail.getTos()[0]));

    }
}
