package com.metoo.nspm.core.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.metoo.nspm.core.dto.UserDto;
import com.metoo.nspm.core.mapper.UserMapper;
import com.metoo.nspm.core.service.IUserService;
import com.metoo.nspm.entity.nspm.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public SysUser selectByName(String username) {
        return this.userMapper.selectByName(username);
    }

    @Override
    public SysUser selectObjById(Long id) {
        return this.userMapper.selectObjById(id);
    }

    @Override
    public List<SysUser> selectObjByMap(Map params) {

        return this.userMapper.selectObjByMap(params);
    }

    @Override
    public Page<SysUser> selectObjByConditionQuery(UserDto dto) {
        if (dto == null) {
            dto = new UserDto();
        }
        Page<SysUser> page = PageHelper.startPage(dto.getCurrentPage(), dto.getPageSize());
        this.userMapper.selectObjByConditionQuery(dto);
        return page;
    }


}
