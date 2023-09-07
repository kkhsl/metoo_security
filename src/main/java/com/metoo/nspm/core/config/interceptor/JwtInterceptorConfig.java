package com.metoo.nspm.core.config.interceptor;

import com.metoo.nspm.core.jwt.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class JwtInterceptorConfig implements WebMvcConfigurer {

    @Bean
    public JwtInterceptor requestHandlerInterceptor(){
        return new JwtInterceptor();
    }

    //要排除的路径,排除的路径说明不需要用户登录也可访问
    String [] excludePathPatterns = {
            "/admin/login",
            "/admin/logout",
            "/admin/captcha",
            "/api/getTokenByTicket",
            "/jwt/refreshTokenExpired",
            "/api/renew"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(requestHandlerInterceptor())
                .addPathPatterns("/**")// 拦截所有路径
                .excludePathPatterns(
                        "/admin/login",
                        "/admin/logout",
                        "/admin/captcha",
                        "/api/getTokenByTicket",
                        "/jwt/refreshTokenExpired",
                        "/api/renew");// 配置不需要拦截的路径
    }
}
