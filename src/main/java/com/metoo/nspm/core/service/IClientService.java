package com.metoo.nspm.core.service;

import com.github.pagehelper.Page;
import com.metoo.nspm.core.dto.ClientDTO;
import com.metoo.nspm.entity.nspm.Client;

import java.util.List;
import java.util.Map;

public interface IClientService {

    Page<Client> selectObjByConditionQuery(ClientDTO dto);
}
