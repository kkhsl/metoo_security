package com.metoo.nspm.core.config.security.filter;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.metoo.nspm.core.config.security.ResponseUtil;
import com.metoo.nspm.core.jwt.util.Globals;
import com.metoo.nspm.core.jwt.util.JwtUtil;
import com.metoo.nspm.core.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 认证解析过滤器
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    Logger log = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    private RedisTemplate redisTemplate;

    public TokenAuthenticationFilter(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        log.info("uri: " + httpServletRequest.getRequestURI());
        log.info("url: " + httpServletRequest.getRequestURL());

        // 登录方法放行
         if("/security/admin/login".equals(httpServletRequest.getRequestURI())){
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        //
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = getAuthentication(httpServletRequest);
        if(usernamePasswordAuthenticationToken != null){
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }else{
            ResponseUtil.out(httpServletResponse, Result.build("权限不足"));
        }
    }


    public UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request){
        // Token置于Header中
        log.info("Token: " + request.getHeader(Globals.AUTHORIZATION).replace("Bearer ", ""));
        String token = request.getHeader(Globals.AUTHORIZATION).replace("Bearer ", "");
        if(!StringUtils.isEmpty(token)){
            String username = JwtUtil.getUsername(token);
            if(!StringUtils.isEmpty(username)){

                String authorization = (String)redisTemplate.opsForValue().get(username);
                List<Map> mapList = JSON.parseArray(authorization, Map.class);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                for (Map map : mapList){
                    authorities.add(new SimpleGrantedAuthority((String) map.get("authority")));
                }

                return new UsernamePasswordAuthenticationToken(username, null, authorities);
//                return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
            }

        }
        return null;
    }
}
