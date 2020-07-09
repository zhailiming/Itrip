package cn.itrip.itripauth.service;

import ch.qos.logback.core.net.SyslogOutputStream;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.userinfo.ItripUserVO;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.MD5;
import cn.itrip.common.RedisAPI;

import cn.itrip.dao.user.ItripUserMapper;
import cn.itrip.itripauth.exception.UserLoginFailedException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service(value = "userServiceImpl")
public class UserServiceImpl implements UserService{
    @Autowired
    private SmsServiceImpl smsService;

    @Autowired
    private RedisAPI redisAPI;

    @Autowired
    private MailService mailService;
    private int expire=1;//过期时间（分钟）


    @Autowired
    private ItripUserMapper itripUserMapper;

    /**
     * 登录
     * @param name 用户名
     * @param password 密码
     * @return
     * @throws Exception
     */
    @Override
    public ItripUser login(String name, String password) throws Exception{

        ItripUser itripUser = this.findByUsername(name);

        if (null!=itripUser && itripUser.getUserPassword().equals(password)){
            System.out.println("用户存在");
            if (itripUser.getActivated()==1){
                throw new UserLoginFailedException("用户未激活");
            }else{
                return itripUser;
            }
        }
        return null;
    }

    /**
     * 根据名字查询
     * @param name 用户名
     * @return
     */
    @Override
    public ItripUser findByUsername(String name) {
        Map<String, Object> param = new HashMap();
        param.put("userCode", name);
        System.out.println(param.get("userCode"));
        try {
            //调用dao方法
            List<ItripUser> itripUserList = itripUserMapper.getItripUserListByMap(param);
            System.out.println(itripUserList.size());
            if(itripUserList.size()>0){
                return itripUserList.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取短信-手机
     * @param userCode 手机号
     */
    @Override
    public void getSMSPhone(String userCode) {
        //发送短信验证码
        String code=String.valueOf(MD5.getRandomCode());
        try {
            smsService.send(userCode, "1", new String[]{code,String.valueOf(expire)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        //缓存手机号及验证码
        String keycode="activation:"+userCode;
        String keyphone="activation:phone";
        redisAPI.set(keyphone,expire*60,userCode);
        redisAPI.set(keycode, expire*60, code);
    }

    /**
     * 短信验证-手机
     * @param phoneNumber 手机号
     * @param code 验证码
     * @return
     */
    @Override
    public boolean vailSMSPhone(String phoneNumber,String code) {
        String keycode="activation:"+phoneNumber;
        String keyphone="activation:phone";
        if(redisAPI.exist(keycode) && redisAPI.exist(keyphone))
            if(redisAPI.get(keycode).equals(code) && redisAPI.get(keyphone).equals(phoneNumber))
                return true;
        return false;
    }

    /**
     * 获取短信-邮箱
     * @param userCode 邮箱地址
     */
    @Override
    public void getSMSMail(String userCode) {
        //生成验证码
        String activationCode = MD5.getMd5(new Date().toLocaleString(), 32);
        //发送激活邮件
        try {
            mailService.sendActivationMail(userCode, activationCode);
        }catch (Exception e) {
            e.printStackTrace();
        }
        //缓存邮箱及验证码
        String keycode="activation:"+userCode;
        String keyMail="activation:mail";
        redisAPI.set(keyMail,expire*60,userCode);
        redisAPI.set(keycode, expire*60, activationCode);

    }

    /**
     * 短信验证-邮箱
     * @param Email 邮箱地址
     * @param code 验证码
     * @return
     */
    @Override
    public boolean vailSMSMail(String Email, String code) {
        String keycode="activation:"+Email;
        String keyMail="activation:mail";
        if(redisAPI.exist(keycode) && redisAPI.exist(keyMail))
            if(redisAPI.get(keycode).equals(code) && redisAPI.get(keyMail).equals(Email))
                return true;
        return false;
    }

    /**
     * 用户注册
     * @param itripUser
     * @throws Exception
     */
    @Override
    public void itripUserInsert(ItripUser itripUser) throws Exception {
        itripUserMapper.insertItripUser(itripUser);
    }
}
