package com.metoo.nspm.core.config.security;

import com.metoo.nspm.core.config.security.custom.CustomMd5Password;
import com.metoo.nspm.core.config.security.custom.CustomUser;
import com.metoo.nspm.core.service.IResService;
import com.metoo.nspm.core.service.IRoleService;
import com.metoo.nspm.core.service.IUserService;
import com.metoo.nspm.entity.nspm.Res;
import com.metoo.nspm.entity.nspm.Role;
import com.metoo.nspm.entity.nspm.SysUser;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;
    @Autowired
    private IResService resService;

    @Autowired
    private CustomMd5Password customMd5Password;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = this.userService.selectByName(username);
        if(sysUser == null){
            throw new UsernameNotFoundException("用户不存在异常");
        }


        /*
        * 增加用户状态判断 例如：密码锁定，密码重试次数过多
        * throw new RuntimeException("用户被禁用");
        */

        // 增加权限数据
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        List<Role> roles = this.roleService.findRoleByUserId(sysUser.getId());//user.getRoles();
        if(!CollectionUtils.isEmpty(roles)){
            if(sysUser != null){

                for(Role role : roles){
                    List<Res> permissions = resService.findResByRoleId(role.getId());
                    if(!CollectionUtils.isEmpty(permissions)){
                        permissions.forEach(permission -> {
                            SimpleGrantedAuthority simpleAuthorizationInfo = new SimpleGrantedAuthority(permission.getValue().trim());
                            authorities.add(simpleAuthorizationInfo);
                        });
                    }
                }
            }
        }
        return new CustomUser(sysUser, authorities);
//        return new CustomUser(sysUser, Collections.emptyList());
    }
}
