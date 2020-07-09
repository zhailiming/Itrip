package cn.itrip.itripauth.service;

import cn.itrip.beans.pojo.ItripUser;

public interface TokenService {



    /**
     * 生成token
     * @param agent  Http头中的user-agent信息
     * @param user   用户信息
     * @return Token格式<br />
     * 	 	PC：“前缀PC-USERCODE-USERID-CREATIONDATE-RONDEM[6位]”
     * 	<BR/>
     * 		MOBILE：“前缀ANDROID-USERCODE-USERID-CREATIONDATE-RONDEM[6位]”
     *
     */
    public String generateToken(String agent, ItripUser user);

    //保存用户信息到redis中
    public void save(String token,ItripUser user);

    //从redis中获取用户信息
    public ItripUser load(String token);

    //删除token
    public void delete(String token);

    /**
     * 置换Token <BR/>
     *  1、首先要判断token是否有效 	<BR/>
     *  2、生成token后的1个小时内不允许置换 	<BR/>
     *  3、置换token时，需要生成新token，并且旧token不能立即失效，应设置为置换后的时间延长2分钟 <BR/>
     *  4、兼容手机端和PC端 <BR/>
     * @param agent	User-Agent
     * @param token	旧的token
     * @return 新的token
     *
     */
    public String replaceToken(String agent,String token) ;

    /**
     * 验证token是否有效
     */
    public boolean validate(String agent,String token);
}
