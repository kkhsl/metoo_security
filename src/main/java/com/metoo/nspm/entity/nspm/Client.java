package com.metoo.nspm.entity.nspm;

import com.metoo.nspm.core.domain.IdEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Client extends IdEntity {

    @ApiModelProperty("应用Id")
    private String clientId;
    @ApiModelProperty("密钥")
    private String clientSecret;
    @ApiModelProperty("应用重定向地址")
    private String redirectUrl;
    @ApiModelProperty("应用名称")
    private String name;
    @ApiModelProperty("应用Logo")
    private String logo;

    private String scope;
}
