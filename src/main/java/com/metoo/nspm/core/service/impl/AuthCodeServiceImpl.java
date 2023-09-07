package com.metoo.nspm.core.service.impl;

import com.metoo.nspm.core.mapper.AuthCodeMapper;
import com.metoo.nspm.core.service.AuthCodeService;
import com.metoo.nspm.entity.nspm.AuthCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Transactional
public class AuthCodeServiceImpl implements AuthCodeService {

    @Resource
    private AuthCodeMapper authCodeMapper;

    @Override
    public AuthCode selectObjByCode(String code) {
        return this.authCodeMapper.selectObjByCode(code);
    }

    @Override
    public AuthCode selectObjByToken(String token) {
        return this.authCodeMapper.selectObjByToken(token);
    }

    @Override
    public int save(AuthCode instance) {
        instance.setAddTime(new Date());
        return this.authCodeMapper.save(instance);
    }

    @Override
    public int update(AuthCode instance) {
        return this.authCodeMapper.update(instance);
    }

    @Override
    public int delete(Long id) {
        return this.authCodeMapper.delete(id);
    }

}
