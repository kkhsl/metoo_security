package com.metoo.nspm.core.service;

import com.metoo.nspm.entity.nspm.AuthCode;

public interface AuthCodeService {

    AuthCode selectObjByCode(String code);

    AuthCode selectObjByToken(String token);

    int save(AuthCode instance);

    int update(AuthCode instance);

    int delete(Long id);

}
