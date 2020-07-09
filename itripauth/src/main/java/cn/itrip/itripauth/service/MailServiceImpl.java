package cn.itrip.itripauth.service;

import cn.itrip.common.RedisAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("mailService")
public class MailServiceImpl implements MailService {
    @Autowired
    private MailSender mailSender;
    @Resource
    private RedisAPI redisAPI;
    @Resource
    private SimpleMailMessage activationMailMessage;

    //发送激活邮件
    @Override
    public void sendActivationMail(String mailTo, String activationCode) {
        System.out.println(mailTo);
        System.out.println(activationCode);
        //发送给
        activationMailMessage.setTo(mailTo);
        //发送的内容
        activationMailMessage.setText("注册邮箱："+mailTo +"  激活码："+activationCode);
        //调用发送方法发送邮件
        mailSender.send(activationMailMessage);
        //保存到redis
        this.saveActivationInfo("activation:"+mailTo, activationCode);
    }
   //保存激活信息
    private void saveActivationInfo(String key, String value) {
        redisAPI.set(key, 30*60, value);
    }
}
