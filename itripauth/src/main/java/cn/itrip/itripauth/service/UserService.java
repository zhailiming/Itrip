package cn.itrip.itripauth.service;

import cn.itrip.beans.pojo.ItripUser;
import org.springframework.stereotype.Service;


public interface UserService {
    //用户登录
    public ItripUser login(String name, String password) throws Exception;
    //根据用户名字查询
    public ItripUser findByUsername(String name);
    //获取短信-手机
    public void getSMSPhone(String userCode);
    //短信验证-手机
    public boolean vailSMSPhone(String phoneNumber,String code);
    //获取短信-邮箱
    public void getSMSMail(String userCode);
    //短信验证-邮箱
    public boolean vailSMSMail(String Email,String code);
    //用户注册
    public void itripUserInsert(ItripUser itripUser) throws Exception;



}
