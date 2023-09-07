package com.metoo.nspm.core.jwt.action;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.metoo.nspm.core.config.utils.ResponseUtil;
import com.metoo.nspm.core.jwt.util.JwtUtil;
import com.metoo.nspm.core.service.AuthCodeService;
import com.metoo.nspm.core.service.IUserService;
import com.metoo.nspm.core.utils.crypto.AesUtils;
import com.metoo.nspm.entity.nspm.AuthCode;
import com.metoo.nspm.entity.nspm.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequestMapping("/api")
@RestController
public class TokenManagerController {

    @Autowired
    private AuthCodeService authCodeService;
    @Autowired
    private IUserService userService;

    // 根据Code码，获取token
    @GetMapping("/getTokenByTicket")
    public Object getToken(@RequestParam(value = "ticket") String ticket) throws Exception {
        if(ticket == null || ticket.equals("")){
            return ResponseUtil.badArgument("参数无效");
        }
        String result = AesUtils.decrypt(ticket);
        Map map = JSONObject.parseObject(result, Map.class);
        if(map.get("code") != null && map.get("username") != null){
            AuthCode authCode = this.authCodeService.selectObjByCode(map.get("code").toString());
            // 验证token是否过期
            // expired：true:过期 false:未过期
            if(authCode != null){
                SysUser user = this.userService.selectObjById(authCode.getUserId());
                if(user != null){
                    authCode.setUsername(user.getUsername());
                }
            }
            return ResponseUtil.ok(authCode);
        }
        return ResponseUtil.badArgument();
    }


    @GetMapping("/renew")
    public Object renew(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        token = token.replace("Bearer ", "");
        return this.verifyToken(token);
        // 验证token，是否存在

        // 如果已过期（失效）

        // 过期时间超过指定时间，直接退出登录，删除指定token

        // 过期时间未超过指定过期时间，刷新token

        // 未超过

        // 否则属性token值

        // 存入数据库，并记录刷新token时间，token更新时间没有好过有效期，则不刷新token
    }

    public Object verifyToken(String token){
        // 验证token，是否存在
        AuthCode authCode = this.authCodeService.selectObjByToken(token);
        if(authCode == null || authCode.equals("")){
            // token无效
        }
        // 如果已过期（失效）
        try {
            JwtUtil.verifyJwt(token);// 验证令牌
            // 有效
            return ResponseUtil.ok();
        } catch(TokenExpiredException e){
            e.printStackTrace();
            // 过期
           return ResponseUtil.badArgument(1002, "Token 过期");
        }

        // 过期时间超过指定时间，直接退出登录，删除指定token

        // 过期时间未超过指定过期时间，刷新token

        // 未超过

        // 否则属性token值

        // 存入数据库，并记录刷新token时间，token更新时间没有好过有效期，则不刷新token
    }

}
