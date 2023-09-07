package com.metoo.nspm.entity.nspm;

import com.metoo.nspm.core.domain.IdEntity;
import lombok.Data;

@Data
public class AuthCode extends IdEntity {

    private Long userId;
    private String code;
    private String token;

    private String refresh_token;

    private String username;
}
