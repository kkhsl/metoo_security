package com.metoo.nspm.core.mapper;

import com.metoo.nspm.core.dto.ClientDTO;
import com.metoo.nspm.entity.nspm.Client;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClientMapper {

    List<Client> selectObjByConditionQuery(ClientDTO dto);
}
