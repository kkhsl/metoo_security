package com.metoo.nspm.core.config.utils;

import com.metoo.nspm.core.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShiroUserHolder {

    @Autowired
    private IUserService userService;

//    public static SysUser currentUser() {
//        if (SecurityUtils.getSubject() != null){
//            Subject subject = SecurityUtils.getSubject();
//            if(subject.getPrincipal() != null && subject.isAuthenticated()){
//                String userName = SecurityUtils.getSubject().getPrincipal().toString();
//                IUserService userService = (IUserService) ApplicationContextUtils.getBean("userServiceImpl");
//                 SysUser user = userService.selectByName(userName);
//                if(user != null){
//                    return user;
//                }
//            }
//        }
//
//        return null;
//    }

//    public static SysUser currentUser(HttpServletRequest request) {
//
//    }

}
