package com.metoo.nspm.core.config.security.custom;

import com.metoo.nspm.entity.nspm.SysUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

// 自定义用户信息类

public class CustomUser extends User {

    private SysUser sysUser;

    public CustomUser(SysUser sysUser, Collection<? extends GrantedAuthority> authorities){
        super(sysUser.getUsername(), sysUser.getPassword(), authorities);
        this.sysUser = sysUser;
    }

    public SysUser getSysUser(){
        return this.sysUser;
    }

    public void setSysUser(SysUser sysUser){
        this.sysUser = sysUser;
    }


}
