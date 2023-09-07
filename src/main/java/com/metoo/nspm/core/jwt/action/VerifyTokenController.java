package com.metoo.nspm.core.jwt.action;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metoo.nspm.core.config.utils.ResponseUtil;
import com.metoo.nspm.core.jwt.util.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api")
@RestController
public class VerifyTokenController {

    // 通过code码，查询token记录；通过token访问api
    @GetMapping("/verify")
    public Object veriry(String token){
        //token头不等于空 并且以Bearer 开头进行token验证登录处理
        int code = 200;
        Map map = new HashMap();
        if(token == null || !token.startsWith("Bearer ")){
            code = 501;
            map.put("msg","无效令牌" );
        }else{
            token = token.replace("Bearer ", "");
            try {
                JwtUtil.verifyJwt(token);// 验证令牌
                return true;
            } catch (SignatureVerificationException e) {
                e.printStackTrace();
                map.put("msg","无效签名" );
                code = 501;
                System.out.println("无效签名");
            }catch (TokenExpiredException e){
                e.printStackTrace();
                code = 502;
                map.put("msg","token 过期" );
                System.out.println("token 过期");
            }catch (AlgorithmMismatchException e){
                e.printStackTrace();
                code = 503;
                map.put("msg","算法不一致" );
                System.out.println("算法不一致");
            }catch (Exception e){
                e.printStackTrace();
                code = 504;
                map.put("msg","token 无效" );
                System.out.println("token 无效");
            }
        }
        map.put("code", code );
        try {
            String json  = new ObjectMapper().writeValueAsString(map);
            return ResponseUtil.ok(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseUtil.error();
        }
    }
}
