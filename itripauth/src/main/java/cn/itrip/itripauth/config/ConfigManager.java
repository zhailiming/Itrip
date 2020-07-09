package cn.itrip.itripauth.config;

import cn.itrip.common.SystemConfig;
import com.sun.tracing.ProbeName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.*;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class ConfigManager {

    @Autowired
   private JedispoolProperty jedispoolProperty ;
    //云通讯配置信息
    @Bean
    public SystemConfig systemConfig(){
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setSmsAccountSid("8a216da873222571017326db71ca0261");
        systemConfig.setSmsAppID("8a216da873222571017326db72a10268");
        systemConfig.setSmsAuthToken("b6edd09da7d943e78b8d555e22dbf1ed");
        systemConfig.setSmsServerPort("8883");
        systemConfig.setSmsServerIP("app.cloopen.com");
        return systemConfig;
    }
    //获取连接池
    @Bean
    public JedisPool jedisPool(){
        JedisPool jedisPool = null;
        try {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxIdle(jedispoolProperty.getMaxIdle());
            jedisPoolConfig.setMaxWait(jedispoolProperty.getMaxWait());
            jedisPoolConfig.setMaxActive(jedispoolProperty.getMaxActive());
            jedisPoolConfig.setTestOnBorrow(true);
            jedisPool = new JedisPool(jedisPoolConfig,jedispoolProperty.getHost(),jedispoolProperty.getPort());

        }catch (Exception e){
            e.printStackTrace();
        }
        return jedisPool;
    }

    /**
     * 邮箱控制对象
     * @return
     */
    @Bean
    public JavaMailSenderImpl javaMailSender(){
        JavaMailSenderImpl javaMailSender=new JavaMailSenderImpl();
        //发送的默认编码
        javaMailSender.setDefaultEncoding("UTF-8");
        //发送邮箱的服务器地址
        javaMailSender.setHost("smtp.163.com");
        //端口号
        javaMailSender.setPort(25);
        //登录名
        javaMailSender.setUsername("zlm511969046@163.com");
        //密码
        javaMailSender.setPassword("RCHNMWKGWDSZLEXB");//
       /* Properties properties=new Properties();
        properties.setProperty("mail.smtp.auth","true");
        javaMailSender.setJavaMailProperties(properties);*/
        return javaMailSender;
    }
    /**
     *
     * 封装邮件信息对象
     * @return
     */
    @Bean
    @Scope(scopeName = "prototype")
    public SimpleMailMessage simpleMailMessage(){
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        //发送者
        simpleMailMessage.setFrom("zlm511969046@163.com");
        //邮件标题
        simpleMailMessage.setSubject("【i旅行】请激活您的账户");
        return simpleMailMessage;
    }
}
