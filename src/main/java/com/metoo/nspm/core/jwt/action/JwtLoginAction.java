package com.metoo.nspm.core.jwt.action;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.metoo.nspm.core.config.utils.ResponseUtil;
import com.metoo.nspm.core.jwt.util.Globals;
import com.metoo.nspm.core.jwt.util.JwtUtil;
import com.metoo.nspm.core.service.AuthCodeService;
import com.metoo.nspm.core.service.IUserService;
import com.metoo.nspm.core.utils.CommUtil;
import com.metoo.nspm.core.utils.crypto.AesUtils;
import com.metoo.nspm.core.vo.Result;
import com.metoo.nspm.entity.nspm.*;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *     Title: JwtLoginAction
 * </p>
 *
 * <p>
 *     Description: Spring Boot 集成 JWT
 *     SpringWeb项目：在拦截器中拦截登录请求；
 *     SpeingCloud项目：在网关中拦截登录请求；
 * </p>
 *
 * <author>
 *     HKK
 * </author>
 */
@RestController
@RequestMapping("/admin")
public class JwtLoginAction {

    @Autowired
    private IUserService userService;
    @Autowired
    private AuthCodeService authCodeService;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("login")
    @ResponseBody
    public Object login(HttpServletResponse response, SysUser user)  {

        if(user.getUsername() == null || user.getUsername().equals("")){
            return ResponseUtil.badArgument("用户名不能为空");
        }
        if(user.getPassword() == null || user.getPassword().equals("")){
            return ResponseUtil.badArgument("密码不能为空");
        }

        SysUser obj = this.userService.selectByName(user.getUsername());

        if(obj == null){
            return new Result(410, "用户名错误");
        }

        Md5Hash md5Hash = new Md5Hash(user.getPassword(), obj.getSalt(), 1024);

        if(!obj.getPassword().equals(md5Hash.toHex())){
            return new Result(420, "密码错误");
        }

        Map payload = new HashMap();

        payload.put("userId", String.valueOf(obj.getId()));

        payload.put("userName", String.valueOf(obj.getUsername()));

        String code = CommUtil.randomString(8);
//
//        payload.put("code", code);

        Map param = new HashMap();
        param.put("username", obj.getUsername());
        param.put("code", code);
        String content = JSONObject.toJSONString(param);
        String ticket = "";
        try {
            ticket = AesUtils.encrypt(content);
            payload.put("ticket", ticket);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String token = JwtUtil.getToken(payload);

        //获取当前时间的毫秒值

        String currentTime = String.valueOf(System.currentTimeMillis() + Globals.EXPIRE);

        //拼接存到redis中的Key中
//        String accessKey = ACCESS_TOKEN + user.getId() + ":" + token;
        String accessKey = Globals.BEARER + obj.getId() + ":" + ticket; // 增加code，避免多客户端登录

        //往拼接存到redis中的Key存value值
        redisTemplate.opsForValue().set(accessKey, currentTime);

        //设置redis key的过期时间 2分钟  可自定义
        redisTemplate.expire(accessKey, Globals.EXPIRE, TimeUnit.MILLISECONDS);

        response.setHeader(Globals.AUTHORIZATION, Globals.BEARER + token);

        // 清空其余token

        // 保存token到数据库

        AuthCode authCode = new AuthCode();
        authCode.setCode(code);
        authCode.setToken(token);
        authCode.setUserId(obj.getId());
        int i = this.authCodeService.save(authCode);
        if(i > 0){
            // 记录到数据库
            Map result = new HashMap();
            result.put("token", token);
            result.put("ticket", ticket);
            return ResponseUtil.ok(result);
        }
        return ResponseUtil.badArgument("认证失败，系统错误");
    }

    @GetMapping("/refreshTokenExpired")
    public String refreshTokenExpired(String token){
        return JwtUtil.refreshTokenExpired(token);
    }


    @GetMapping("/getUserIdByToken")
    public Long getUserIdByToken(HttpServletRequest request){
        return JwtUtil.getUserIdByToken(request);
    }

    // 获取用户信息
    @GetMapping("/getUser")
    public Object getUser(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        token = token.replace("Bearer ", "");
        DecodedJWT decodedJWT = JwtUtil.getDecodedJWT(token);
        return decodedJWT.getClaim("username").asString();

    }

//    // 退出
//    @RequestMapping("/logout")
//    public Object logout(@RequestParam String code){
//
//        AuthCode authCode = this.authCodeService.selectObjByCode(code);
//        if(authCode != null){
//            // 删除
//            this.authCodeService.delete(authCode.getId());
//        }
//        return ResponseUtil.ok();
//    }

    // 退出
    @RequestMapping("/logout")
    public Object logout(HttpServletRequest request){
        String token = request.getHeader("Authorization");

        if(token == null || token.equals("")){
            return ResponseUtil.ok();
        }
        if(!token.startsWith("Bearer ")){
            return ResponseUtil.ok();
        }
        token = token.replace("Bearer ", "");
        AuthCode authCode = this.authCodeService.selectObjByToken(token);
        if(authCode != null){
            // 删除
            this.authCodeService.delete(authCode.getId());
        }
        return ResponseUtil.ok();
    }

}
