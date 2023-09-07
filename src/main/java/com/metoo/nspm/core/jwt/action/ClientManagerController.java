package com.metoo.nspm.core.jwt.action;

import com.github.pagehelper.Page;
import com.metoo.nspm.core.config.utils.ResponseUtil;
import com.metoo.nspm.core.dto.ClientDTO;
import com.metoo.nspm.core.service.IClientService;
import com.metoo.nspm.core.utils.query.PageInfo;
import com.metoo.nspm.entity.nspm.Client;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RestController
public class ClientManagerController {

    @Autowired
    private IClientService clientService;

    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("客户端列表")
    @PostMapping("/client")
    public Object clientList(@RequestBody ClientDTO dto){
        Page<Client> page = this.clientService.selectObjByConditionQuery(dto);
        if(page.getResult().size() > 0){
            return ResponseUtil.ok(new PageInfo<Client>(page));
        }
        return ResponseUtil.ok();
    }
}
