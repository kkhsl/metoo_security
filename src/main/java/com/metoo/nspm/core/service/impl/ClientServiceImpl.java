package com.metoo.nspm.core.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.metoo.nspm.core.dto.ClientDTO;
import com.metoo.nspm.core.mapper.ClientMapper;
import com.metoo.nspm.core.service.IClientService;
import com.metoo.nspm.entity.nspm.Client;
import com.metoo.nspm.entity.nspm.NetworkElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

@Service
@Transactional
public class ClientServiceImpl implements IClientService {

    @Resource
    private ClientMapper clientMapper;

    @Override
    public Page<Client> selectObjByConditionQuery(ClientDTO dto) {
        if(dto == null){
            dto = new ClientDTO();
        }
        Page<Client> page = PageHelper.startPage(dto.getCurrentPage(), dto.getPageSize());
        this.clientMapper.selectObjByConditionQuery(dto);
        return page;
    }
}
