package com.metoo.nspm.core.jwt.interceptor;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metoo.nspm.core.jwt.util.Globals;
import com.metoo.nspm.core.jwt.util.JwtUtil;
import com.metoo.nspm.core.service.AuthCodeService;
import com.metoo.nspm.entity.nspm.AuthCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *     Title: JwtInterceptor.java
 * </p>
 *
 * <p>
 *     Description: JWT 登录拦截器
 * </p>
 *
 * <author>
 *     HKK
 * </author>
 */

public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthCodeService authCodeService;
    @Autowired
    private RedisTemplate redisTemplate;

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
////        String token = request.getHeader("token");
//        String tokenHead = request.getHeader("Authorization");
//
//        String verify_code = request.getHeader("code");
//
//        //token头不等于空 并且以Bearer 开头进行token验证登录处理
//        int code = 200;
//        Map map = new HashMap();
//        if(tokenHead == null || !tokenHead.startsWith("Bearer ")){
//            code = 501;
//            map.put("msg", "无效签名");
//        }else{
//            String token = tokenHead.replace("Bearer ", "");
//            try {
//                JwtUtil.verifyJwt(token);// 验证令牌
//                return true;
//            } catch (SignatureVerificationException e) {
//                e.printStackTrace();
//                map.put("msg","无效签名");
//                code = 1001;
//                System.out.println("无效签名");
//            }catch (TokenExpiredException e){
//                e.printStackTrace();
//                code = 1002;
//                map.put("msg","token 过期");
//            }catch (AlgorithmMismatchException e){
//                e.printStackTrace();
//                code = 1003;
//                map.put("msg","算法不一致");
//                System.out.println("算法不一致");
//            }catch (Exception e){
//                e.printStackTrace();
//                code = 1004;
//                map.put("msg","token 无效");
//                System.out.println("token 无效");
//            }
//        }
//        map.put("code", code);
//        String json  = new ObjectMapper().writeValueAsString(map);
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().print(json);
//        return false;
//    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String token = request.getHeader("token");
        String tokenHead = request.getHeader("Authorization");

        String verify_code = request.getHeader("code");

        //token头不等于空 并且以Bearer 开头进行token验证登录处理
        int code = 200;
        Map map = new HashMap();
        if(tokenHead == null || !tokenHead.startsWith("Bearer ")){
            code = 401;
            map.put("msg", "无效签名");
        }else{
            String token = tokenHead.replace("Bearer ", "");
            boolean flag = true;
            try {
                JwtUtil.verifyJwt(token);// 验证令牌

                // 1，判断是否需要续签
                // 1-1，判断redis中是否存在该值
                // 优化，使用token直接取出所需数据
                DecodedJWT decodedJWT = JwtUtil.getDecodedJWT(token);
                String userId = decodedJWT.getClaim("userId").asString();
                String ticket = decodedJWT.getClaim("ticket").asString();
                String accessKey = Globals.BEARER + userId + ":" + ticket; // 增加code，避免多客户端登录

                if(!redisTemplate.hasKey(accessKey)){
                    //登录失败
                    map.put("msg","无效签名");
                    code = 401;
                    flag = false;
                }else{
                    // 时间换算

                    // 1-2，判断时间是否在过期时间范围内，未超出则续签

                    String token_expire_str = (String) redisTemplate.opsForValue().get(accessKey);

                    Long token_expire = Long.parseLong(token_expire_str);

                    Date time = new Date(token_expire);

                    Date time2 = new Date();

                    if(time.after(time2)){
                        // token有限，判断有效时间，是否需要续期
                        // 剩余时间，小于过期时间一半则过期时间，增加过期时间
                        long time3 = time2.getTime();
                        System.out.println(token_expire - time3);
                        if((token_expire - time3) < Globals.EXPIRE / 2){

                            String currentTime = String.valueOf(System.currentTimeMillis() + Globals.EXPIRE);

                            redisTemplate.opsForValue().set(accessKey, currentTime);

                            redisTemplate.expire(accessKey, Globals.EXPIRE, TimeUnit.MILLISECONDS);
                        }
                    }else{
                        // 过期 退出
                        code = 401;
                        map.put("msg","token 过期");
                        flag = false;
                    }
                }



                if(flag){
                    return true;
                }
            } catch (SignatureVerificationException e) {
                e.printStackTrace();
                map.put("msg","无效签名");
                code = 401;
                System.out.println("无效签名");
            }catch (TokenExpiredException e){
                e.printStackTrace();
                code = 401;
                map.put("msg","token 过期");
            }catch (AlgorithmMismatchException e){
                e.printStackTrace();
                code = 401;
                map.put("msg","算法不一致");
                System.out.println("算法不一致");
            }catch (Exception e){
                e.printStackTrace();
                code = 401;
                map.put("msg","token 无效");
                System.out.println("token 无效");
            }
        }
        map.put("code", code);
        String json  = new ObjectMapper().writeValueAsString(map);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
        return false;
    }

    // 预处理回调方法
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
////        String token = request.getHeader("token");
//        String tokenHead = request.getHeader("Authorization");
//
//        String verify_code = request.getHeader("code");
//
//       //token头不等于空 并且以Bearer 开头进行token验证登录处理
//        int code = 200;
//        Map map = new HashMap();
//        if(tokenHead == null || !tokenHead.startsWith("Bearer ")){
//            code = 501;
//            map.put("msg", "无效签名");
//        }else{
//            String token = tokenHead.replace("Bearer ", "");
//            try {
//                JwtUtil.verifyJwt(token);// 验证令牌
//
//                DecodedJWT dj = JwtUtil.getDecodedJWT(token);
//
//                // 判断用户是否退出
//                String djCode = dj.getClaim("code").asString();
//                AuthCode authCode = this.authCodeService.selectObjByCode(djCode);
//                if(authCode == null){
//                    code = 1004;
//                    map.put("msg","token 无效");
//                }else{
//                    // 计算当前时间是否超过过期时间的一半，如果是就帮用户续签 --------------------------
//                    // 此处并不是永久续签，只是为 大于过期时间的一半 且 小于过期时间 的 token 续签
//                    Long expTime = dj.getExpiresAt().getTime();
//                    Long iatTime = dj.getIssuedAt().getTime();
//                    Long nowTime = System.currentTimeMillis();
//                    if((nowTime-iatTime) > (expTime-iatTime) / 2) {
//                        // 生成新的jwt
//                        Map<String, String> payload = new HashMap<>();
//                        payload.put("code", verify_code);
//                        String newJwt = JwtUtil.getToken(payload);
//                        // 加入返回头
//                        response.addHeader("Authorization", "Bearer " + newJwt);
//                        if(verify_code == null && verify_code != ""){
//                            // 根据token查询 refresh_token
//                            AuthCode refresh = this.authCodeService.selectObjByToken(token);
//                            if(refresh != null){
//                                // 更新token
//                                refresh.setRefresh_token(newJwt);
//                                this.authCodeService.update(refresh);
//                            }
//                        }
//                    }
//                    return true;
//                }
//            } catch (SignatureVerificationException e) {
//                e.printStackTrace();
//                map.put("msg","无效签名");
//                code = 1001;
//                System.out.println("无效签名");
//            }catch (TokenExpiredException e){
//                e.printStackTrace();
//                if(verify_code == null && verify_code.equals("")){
//                    // 根据token查询refresh_token
//                    AuthCode refresh = this.authCodeService.selectObjByToken(token);
//                    if(refresh != null){
//                        if(refresh.getRefresh_token() != null && !refresh.getRefresh_token().equals("")){
//                            // 校验刷新token是否失效
//                            try {
//                                JwtUtil.verifyJwt(refresh.getRefresh_token());// 验证令牌
//                                response.addHeader("Authorization", "Bearer " + refresh.getRefresh_token());
//                                return true;
//                            } catch (TokenExpiredException e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                    }
//                }
//                code = 1002;
//                map.put("msg","token 过期");
//            }catch (AlgorithmMismatchException e){
//                e.printStackTrace();
//                code = 1003;
//                map.put("msg","算法不一致");
//                System.out.println("算法不一致");
//            }catch (Exception e){
//                e.printStackTrace();
//                code = 1004;
//                map.put("msg","token 无效");
//                System.out.println("token 无效");
//            }
//        }
//        map.put("code", code);
//        String json  = new ObjectMapper().writeValueAsString(map);
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().print(json);
//        return false;
//    }

    // 目标方法执行后调用
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
        System.out.println("");
    }

}
