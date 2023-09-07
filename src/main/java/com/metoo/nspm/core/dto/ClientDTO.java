package com.metoo.nspm.core.dto;

import com.metoo.nspm.core.dto.page.PageDto;
import com.metoo.nspm.entity.nspm.Client;
import com.metoo.nspm.entity.nspm.Res;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO extends PageDto<Client> {

    private String name;
}
