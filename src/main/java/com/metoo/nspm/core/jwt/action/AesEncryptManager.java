package com.metoo.nspm.core.jwt.action;

import com.alibaba.fastjson.JSONObject;
import com.metoo.nspm.core.utils.crypto.AesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户名加解密
 *
 * @author hkk
 * @date 2023/09/04
 */

@RequestMapping("/admin/aes")
@Controller
public class AesEncryptManager {

    @RequestMapping("/encrypt")
    @ResponseBody
    private String encrypt(@RequestParam(value = "username") String username) throws Exception{
        Map map = new HashMap();
        map.put("username", username);
        String content = JSONObject.toJSONString(map);
        return AesUtils.encrypt(content);
    }

    @RequestMapping("/decrypt")
    @ResponseBody
    private String decrypt(@RequestParam String param) throws Exception{
        return AesUtils.decrypt(param);
    }
}
