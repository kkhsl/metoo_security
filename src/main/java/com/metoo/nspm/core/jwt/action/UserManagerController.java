package com.metoo.nspm.core.jwt.action;

import com.github.pagehelper.Page;
import com.metoo.nspm.core.config.utils.ResponseUtil;
import com.metoo.nspm.core.dto.UserDto;
import com.metoo.nspm.core.service.IUserService;
import com.metoo.nspm.core.utils.query.PageInfo;
import com.metoo.nspm.entity.nspm.SysUser;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RestController
public class UserManagerController {

    @Autowired
    private IUserService userService;

    @ApiOperation("用户列表")
    @RequestMapping("/user")
    public Object list(@RequestBody(required = false) UserDto dto) {
        if (dto == null) {
            dto = new UserDto();
        }
        Page<SysUser> page = this.userService.selectObjByConditionQuery(dto);
        if (page.getResult().size() > 0) {
            return ResponseUtil.ok(new PageInfo<SysUser>(page));
        }
        return ResponseUtil.ok();
    }
}
