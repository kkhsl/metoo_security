package com.metoo.nspm.core.manager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RestController
public class TestController {

    @GetMapping("/test")
    public Object test(){
        return "test";
    }


}
