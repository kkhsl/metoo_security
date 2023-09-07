package com.metoo.nspm.core.mapper;

import com.metoo.nspm.entity.nspm.AuthCode;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.map.repository.config.EnableMapRepositories;

//@EnableMapRepositories
@Mapper
public interface AuthCodeMapper {

    AuthCode selectObjByCode(String code);

    AuthCode selectObjByToken(String token);

    int save(AuthCode instance);

    int update(AuthCode instance);

    int delete(Long id);
}
