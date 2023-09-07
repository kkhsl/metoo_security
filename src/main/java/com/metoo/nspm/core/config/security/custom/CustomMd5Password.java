package com.metoo.nspm.core.config.security.custom;

import com.metoo.nspm.core.config.security.utils.MD5Utils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// 自定义密码组件
@Component
public class CustomMd5Password implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
//        rawPassword = "123456";

        return MD5Utils.encrypt(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodePassword) {
        // 如果密码为空， 则返回true; 实现免密登录
        System.out.println(MD5Utils.encrypt(rawPassword.toString()));
        System.out.println(encodePassword.equals(MD5Utils.encrypt(rawPassword.toString())));

        return encodePassword.equals(MD5Utils.encrypt(rawPassword.toString()));
    }
}
