package com.metoo.nspm.core.jwt.interceptor;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.github.pagehelper.util.StringUtil;
import com.metoo.nspm.core.jwt.util.JwtUtil;
import com.metoo.nspm.core.service.IUserService;
import com.metoo.nspm.entity.nspm.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IUserService userService;
    @Resource
    private HttpServletRequest request;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        logger.info("进入拦截器 uri:" + request.getRequestURI());
        // 不是controller的方法不拦截
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        //检查方法上是否有@PreAuth注解，没有则不拦截
//        HandlerMethod handlerMethod = (HandlerMethod) handler;
//        PreAuth preAuth = handlerMethod.getMethodAnnotation(PreAuth.class);
//        if (preAuth == null) {
//            return true;
//        }

        Long userId = JwtUtil.getUserIdByToken(request);
        SysUser user = userService.selectObjById(userId);

        //用户不存在，进行拦截
        if (user == null) {
            logger.error("用户不存在");
            return false;
        }

        if (!refreshToken(user)) {
            return false;
        }

        //判断用户是否有对应权限
//        Set<String> authList = this.sysUserDao.queryAuthList(userId);
//        if (!authList.contains(preAuth.value())) {
//            logger.error("无权限");
//            return false;
//        }
        return true;
    }

    /**
     * token自动续期
     *
     * @param user 用户实体
     * @return 是否刷新成功
     */
    private boolean refreshToken(SysUser user) {
        String token = request.getHeader("Authorization");
//        String cacheToken = (String) (redisUtil.get(sysUser.getPhone() + GlobalConstant.TOKEN));
        //请求头中不存在token，返回false
        if (StringUtil.isEmpty(token)) {
            logger.error("请求头中token不存在");
            return false;
        }

        //用户是否登录只根据redis中token是否存在决定，redis中不存在token，返回false
//        if (StringUtil.isEmpty(cacheToken)) {
//            logger.error("用户未登录");
//            return false;
//        }
        try {
            //验证请求头中的token是否合法
            JwtUtil.verifyJwt(token);
        } catch (TokenExpiredException tokenExpiredException) {
            /*若抛出token过期异常，检查redis中的是否存在token以及请求头中的token与redis中的token是否相同
            如果相同，说明用户仍在操作，只是请求头中的token已经过期，此时需要对token进行续期*/
//            if (cacheToken.equals(token)) {
//                //重新刷新redis中的token的过期时间
//                redisUtil.set(sysUser.getPhone() + GlobalConstant.TOKEN, token, JwtUtil.EXPIRE_TIME * 60 * 2);
//                return true;
//            } else {
//                return false;
//            }
            // 到数据库中查询token是否存在，

        } catch (Exception e) {
            //若抛出除token过期异常之外的其他异常，说明该token不合法
            logger.error("token不合法");
            return false;
        }
        return true;
    }
}
