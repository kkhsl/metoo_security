package com.metoo.nspm.core.config.security.filter;

import com.alibaba.fastjson.JSON;
import com.metoo.nspm.core.config.security.ResponseUtil;
import com.metoo.nspm.core.config.security.custom.CustomUser;
import com.metoo.nspm.core.jwt.util.JwtUtil;
import com.metoo.nspm.core.utils.CommUtil;
import com.metoo.nspm.core.vo.Result;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// 自定义认证过滤器
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private RedisTemplate redisTemplate;

    public TokenLoginFilter(AuthenticationManager authenticationManager, RedisTemplate redisTemplate)
    {
        this.setAuthenticationManager(authenticationManager);
        this.setPostOnly(false);
        // 指定登录接口以及提交方式，可以指定任意路径
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/login", "POST"));
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String username = this.obtainUsername(request);
        username = username != null ? username : "";
        username = username.trim();
        String password = this.obtainPassword(request);
        password = password != null ? password : "";
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        this.setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    // 认证成功
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        request.getSession(false).removeAttribute("verify_code");

//        super.successfulAuthentication(request, response, chain, authResult);

        CustomUser customUser = (CustomUser) authResult.getPrincipal();
        // 生成Token
        Map payload = new HashMap();

        payload.put("userId", String.valueOf(customUser.getSysUser().getId()));

        payload.put("userName", String.valueOf(customUser.getSysUser().getUsername()));

        String code = CommUtil.randomString(8);
        payload.put("code", code);

        String token = JwtUtil.getToken(payload);

        Map<String, Object> result = new HashMap();
        result.put("token", token);

        // 认证成功，存储权限数据到redis
        redisTemplate.opsForValue().set(customUser.getUsername(), JSON.toJSONString(customUser.getAuthorities()));

        ResponseUtil.out(response, Result.ok(result));

    }

    // 认证失败
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException e) throws IOException, ServletException {
//        super.unsuccessfulAuthentication(request, response, failed);
        if(e.getCause() instanceof RuntimeException){
            ResponseUtil.out(response, Result.build(204, e.getMessage()));
        }else{
            ResponseUtil.out(response, Result.build("Login moble error"));
        }
    }
}
