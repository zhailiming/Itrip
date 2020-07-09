package cn.itrip.itripauth.service;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.common.MD5;
import cn.itrip.common.RedisAPI;
import cn.itrip.common.UserAgentUtil;
import com.alibaba.fastjson.JSON;
import cz.mallat.uasparser.UserAgentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.SimpleFormatter;
@Service
public class TokenServiceImpl implements TokenService {

    //会话超时时间
    public  static final int SESSION_TIMEOUT = 2*60*60;//两个小时

    //置换保护时间（设置一个小时不可以置换token）
    public static final int REPLACEMENT_PROTECTION_TIMEOUT = 60*60;

    //就得token延迟过期时间
    public static final int REPLACEMENT_DELAY = 2*60;//默认两分钟

    //加入统一token前缀标识
    private String tokenPreFix = "token";

    private  int expire = SESSION_TIMEOUT;

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    //调用redisAPI
    @Autowired
    private RedisAPI redisAPI;

    /**
     * 生成Token的规范
     *  * PC：“前缀PC-USERCODE-USERID-CREATIONDATE-RONDEM[6位]”
     *  * MOBILE：“前缀 ANDROID-USERCODE-USERID-CREATIONDATE-RONDEM[6位]”
     * @param agent  Http头中的user-agent信息
     * @param user   用户信息
     * @return
     */
    @Override
    public String generateToken(String agent, ItripUser user) {
        try {
            //获取页面传过来的头部信息
            /**
             * 获取系统信息
             * 设备信息
             * 版本信息
             */
            UserAgentInfo userAgentInfo = UserAgentUtil.getUasParser().parse(agent);
            //定义token的字符串
            StringBuffer stringToken  = new StringBuffer();
            stringToken.append(tokenPreFix);//追加头部信息
            //判断设备类型     无法识别的类型
            if(userAgentInfo.getDeviceType().equals(UserAgentInfo.UNKNOWN)){
                //排除win桌面系统   iOS桌面系统
                if(UserAgentUtil.CheckAgent(agent)){
                    stringToken.append("PC-");
                }else{
                    stringToken.append("MOBILE-");
                }
            }else if(userAgentInfo.getDeviceType().equals("Personal computer")){
                stringToken.append("PC-");
            }else {
                stringToken.append("MOBILE-");
            }
            //加密用户code
            stringToken.append(MD5.getMd5(user.getUserCode(),32)+"-");
            stringToken.append(user.getId());//加入用户id
            //加入当前时间
            stringToken.append(new SimpleDateFormat("yyyyMMdd").format(new Date())+"-");
            stringToken.append(MD5.getMd5(agent,6));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存到Redis
     * @param token
     * @param user
     */
    @Override
    public void save(String token, ItripUser user) {
        System.out.println(token+"<===>");

        if(token.startsWith(tokenPreFix+"PC-")){
            redisAPI.set(token,expire, JSON.toJSONString(user));
        }else{
            redisAPI.set(token, JSON.toJSONString(user));//手机token设置永不过期
        }
    }

    /**
     * 获取redis中的用户信息
     * @param token
     * @return
     */
    @Override
    public ItripUser load(String token) {
        return JSON.parseObject(redisAPI.get("token"),ItripUser.class);
    }

    /**
     * 删除token
     * @param token
     */
    @Override
    public void delete(String token) {

        if(redisAPI.exist(token)){
            redisAPI.delete(token);
        }
    }

    private boolean exists(String token) {
        return redisAPI.exist(token);
    }


    /**
     * 置换token
     * @param agent	User-Agent
     * @param token	旧的token
     * @return
     */
    @Override
    public String replaceToken(String agent, String token) {
        return null;
    }

    /**
     * token验证
     * @param agent
     * @param token
     * @return
     */
    @Override
    public boolean validate(String agent, String token) {
        if (!exists(token)){
            return false;
        }
        Date TokenGenTime;//生成token时间
        String agentMD5;
        String[] tokenDetails = token.split("-");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        try {
            TokenGenTime = simpleDateFormat.parse(tokenDetails[3]);
            long passed = Calendar.getInstance().getTimeInMillis()-TokenGenTime.getTime();
            if (passed>this.SESSION_TIMEOUT*1000){
                return  false;
            }
            agentMD5 =tokenDetails[4];
            if (MD5.getMd5(agent,6).equals(agentMD5)){
                return true;
            }
        } catch (ParseException e) {
            return false;
        }
        return false;
    }
}
