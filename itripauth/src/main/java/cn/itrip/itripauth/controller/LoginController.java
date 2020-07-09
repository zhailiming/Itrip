package cn.itrip.itripauth.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.ErrorCode;
import cn.itrip.common.MD5;
import cn.itrip.itripauth.exception.UserLoginFailedException;
import cn.itrip.itripauth.service.TokenService;
import cn.itrip.itripauth.service.TokenServiceImpl;
import cn.itrip.itripauth.service.UserService;
import cn.itrip.itripauth.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;

/**
 *用户登录Controller
 *控制用户登录及注销
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class LoginController {

    @Autowired
    @Qualifier(value = "userServiceImpl")
    private UserServiceImpl userServiceImpl;
    @Autowired
    private TokenServiceImpl tokenService;
    /**
     * 登录
     */
    @RequestMapping(value = "/doLogin",method = RequestMethod.GET)
    public Dto dologin(@RequestParam String name, @RequestParam String password, HttpServletRequest request) {
        if (!EmptyUtils.isEmpty(name) && !EmptyUtils.isEmpty(password)) {
            ItripUser itripUser = null;
            try {
                itripUser = userServiceImpl.login(name.trim(),MD5.getMd5(password.trim(),32));
            } catch (UserLoginFailedException e) {
               //e.printStackTrace();
                return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_AUTHENTICATION_FAILED);
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
            }
            //判断user对象是否为空
            if (EmptyUtils.isNotEmpty(itripUser)){
                //获取token
                String token = tokenService.generateToken(request.getHeader("user-agent"),itripUser);
                System.out.println("判断token====>"+token);
                //保存token到redis中
                tokenService.save(token,itripUser);
                //返回ItripTokenVO
                ItripTokenVO itripTokenVO = new ItripTokenVO(token,
                        Calendar.getInstance().getTimeInMillis()+ TokenServiceImpl.SESSION_TIMEOUT*1000,//有效期2h
                        Calendar.getInstance().getTimeInMillis());
                return DtoUtil.returnDataSuccess(itripTokenVO);
            }else{
                return DtoUtil.returnFail("用户名或密码错误",ErrorCode.AUTH_AUTHENTICATION_FAILED);
            }
        }else {
            return DtoUtil.returnFail("参数错误！检查提交的参数名称是否正确。",ErrorCode.AUTH_PARAMETER_ERROR);
        }
    }

    /**
     * 注销
     */
    @RequestMapping("/logout")
    public Dto logout(HttpServletRequest request){

        //验证token
        String token = request.getHeader("token");
        if(!tokenService.validate(request.getHeader("user-agent"),token)){
            return DtoUtil.returnFail("token无效",ErrorCode.AUTH_TOKEN_INVALID);
        }
        //删除token信息
        try {
            tokenService.delete(token);
            return DtoUtil.returnSuccess("注销成功");
        }catch (Exception e){
            return  DtoUtil.returnFail("注销失败",ErrorCode.AUTH_UNKNOWN);
        }

    }

}
