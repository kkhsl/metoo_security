package com.metoo.nspm.core.service;

import com.github.pagehelper.Page;
import com.metoo.nspm.core.dto.UserDto;
import com.metoo.nspm.entity.nspm.SysUser;

import java.util.List;
import java.util.Map;

public interface IUserService {

    /**
     * 根据Username 查询一个User 对象
     * @param username
     * @return
     */
    SysUser selectByName(String username);

    SysUser selectObjById(Long id);

    List<SysUser> selectObjByMap(Map params);

    Page<SysUser> selectObjByConditionQuery(UserDto dto);

}
