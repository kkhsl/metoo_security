package com.metoo.nspm.core.mapper;

import com.metoo.nspm.core.dto.UserDto;
import com.metoo.nspm.entity.nspm.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据Username 查询一个User 对象
     * @param username
     * @return
     */
    SysUser selectByName(String username);

    SysUser selectObjById(Long id);

    List<SysUser> selectObjByMap(Map params);

    List<SysUser> selectObjByConditionQuery(UserDto dto);
}
