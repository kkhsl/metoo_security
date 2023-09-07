package com.metoo.nspm.core.config.security.config;

import com.metoo.nspm.core.config.security.UserDetailServiceImpl;
import com.metoo.nspm.core.config.security.custom.CustomMd5Password;
import com.metoo.nspm.core.config.security.filter.TokenAuthenticationFilter;
import com.metoo.nspm.core.config.security.filter.TokenLoginFilter;
import com.metoo.nspm.core.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 前后端分离项目，使用JWT生成Token，即用户状态保存在客户端中，前后端交互通过API接口，无Session，所以不需要配置formLogin，session禁用
 */
// 认证解析过滤器
@Configuration
// 开启SpringSecurity的默认行为
@EnableWebSecurity
// 开启注解功能，默认禁用注解
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailServiceImpl userService;
    @Autowired
    private CustomMd5Password customMd5Password;
    @Autowired
    private RedisTemplate redisTemplate;


    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 这里是关键配置，决定哪些接口开启防护，哪些接口绕过防护
        http.csrf().disable()// 关闭csrf
            .cors().and() // 开启跨域以便前端调用接口
            .authorizeRequests()
            .antMatchers("/admin/login").permitAll()// 指定某些接口不需要通过验证即可访问。登录接口肯定是不需要认证的
            .anyRequest().authenticated()// 其他所有接口都要认证才能访问
            .and()
                // 这样做就是为了除了登录的时候去查询数据库外，其他时候都用Token进行认证
            .addFilterBefore(new TokenAuthenticationFilter(redisTemplate), UsernamePasswordAuthenticationFilter.class)

            .addFilter(new TokenLoginFilter(authenticationManager(), redisTemplate));

        // 禁用Session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        // 设置默认的加密方式
//        return new BCryptPasswordEncoder();
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        // 指定UserDetailService  和加密器
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(customMd5Password);
    }

    /**
     * 配置哪些请求不拦截
     * 排除swagger相关请求
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web)throws Exception{
        web.ignoring().antMatchers("/favicon.ico", "swagger-resources/**", "/webjars/**",
                "/v2/**", "swagger-ui.html/**", "/doc.html");
    }
}
