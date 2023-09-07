package com.metoo.nspm.core.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metoo.nspm.core.vo.Result;
import org.apache.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {

    public static void out(HttpServletResponse response, Result r){
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(HttpStatus.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            mapper.writeValue(response.getWriter(), r);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
