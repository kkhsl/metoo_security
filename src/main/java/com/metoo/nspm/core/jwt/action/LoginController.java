package com.metoo.nspm.core.jwt.action;

import com.metoo.nspm.core.config.utils.ResponseUtil;
import com.metoo.nspm.core.jwt.util.JwtUtil;
import com.metoo.nspm.core.service.AuthCodeService;
import com.metoo.nspm.core.service.IUserService;
import com.metoo.nspm.core.utils.CaptchaUtil;
import com.metoo.nspm.core.utils.CommUtil;
import com.metoo.nspm.core.utils.CookieUtil;
import com.metoo.nspm.core.vo.Result;
import com.metoo.nspm.entity.nspm.AuthCode;
import com.metoo.nspm.entity.nspm.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Api(description = "登录控制器")
@RestController
//@RequestMapping(value = "/admin")
public class LoginController {

    Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private IUserService userService;
    @Autowired
    private AuthCodeService authCodeService;

    @ApiOperation("登录")
    @RequestMapping("/login")
    public Object login(HttpServletRequest request, HttpServletResponse response,
                        String username, String password, String isRememberMe){
        String msg = "";
        // 通过安全工具类获取 Subject
        Subject subject = SecurityUtils.getSubject();

        // 获取当前已登录用户
        Session session = SecurityUtils.getSubject().getSession();
        session.getStartTimestamp();
        if(StringUtils.isEmpty(username)){
            return ResponseUtil.badArgument("用户名必填");
        }
        if(StringUtils.isEmpty(password)){
            return ResponseUtil.badArgument("密码必填");
        }
        boolean flag = true;// 当前用户是否已登录
        if(subject.getPrincipal() != null && subject.isAuthenticated()){
            String userName = subject.getPrincipal().toString();
            if(userName.equals(username)){
                flag = false;
              }
            }
            if(flag){
                UsernamePasswordToken token = new UsernamePasswordToken(username,password);
                try {
                    if(isRememberMe != null && isRememberMe.equals("1")){
                        token.setRememberMe(true);
                        // 或 UsernamePasswordToken token = new UsernamePasswordToken(username,password,true);
                    }else{
                        token.setRememberMe(false);
                    }
                    subject.login(token);

                    session.removeAttribute("captcha");

                    SysUser user = this.userService.selectByName(username);

                    Map payload = new HashMap();

                    payload.put("userId", String.valueOf(user.getId()));

                    String code = CommUtil.randomString(8);

                    payload.put("code", code);

                    String token1 = JwtUtil.getToken(payload);

                    response.setHeader("Authorization", "Bearea " + token);

                    // 清空其余token

                    // 保存token到数据库

                    AuthCode authCode = new AuthCode();
                    authCode.setCode(code);
                    authCode.setToken(token1);
                    authCode.setUserId(user.getId());
                    int i = this.authCodeService.save(authCode);

                    if(i > 0){
                        // 记录到数据库
                        Map result = new HashMap();
                        result.put("token", token1);
                        result.put("code", code);
                        result.put("userId", user.getId());
                        return ResponseUtil.ok(result);
                    }
                    return ResponseUtil.ok();
                    //  return "redirect:/index.jsp";
                } catch (UnknownAccountException e) {
                    e.printStackTrace();
                msg = "用户名错误";
                System.out.println("用户名错误");
                return new Result(410, msg);
            } catch (IncorrectCredentialsException e){
                e.printStackTrace();
                msg = "密码错误";
                System.out.println("密码错误");
                return new Result(420, msg);
            }
        }else{
            return new Result(200, "用户已登录");
        }
    }


    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        System.out.println("Arp开始采集: " + System.currentTimeMillis());
//        // 采集时间
//        Calendar cal = Calendar.getInstance();
//        cal.clear(Calendar.SECOND);
//        cal.clear(Calendar.MILLISECOND);
//        Date date = cal.getTime();
//        // 此处开启两个线程
//        // 存在先后顺序，先录取arp，在根据arp解析数据
//        this.gatherService.gatherMacItem(date);
//        this.gatherService.gatherArpItem(date);
//        this.gatherService.gatherRouteItem(date);

        //设置响应头信息，通知浏览器不要缓存
        response.setHeader("Expires", "-1");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "-1");
        response.setContentType("image/jpeg");

        // 获取验证码
        String code = CaptchaUtil.getRandomCode();
        // 将验证码输入到session中，用来验证
        HttpSession session = request.getSession();

        session.setAttribute("captcha", code);
        this.removeAttrbute(session, "captcha");
        // 输出到web页面
        ImageIO.write(CaptchaUtil.genCaptcha(code), "jpg", response.getOutputStream());
    }

    public void removeAttrbute(final HttpSession session, final String attrName) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                // 删除session中存的验证码
                session.removeAttribute(attrName);
                timer.cancel();
            }
        }, 5 * 60 * 1000); //5 * 60 * 1000
    }

    @RequestMapping("/logout")
    public Object logout(HttpServletRequest request, HttpServletResponse response){
        Subject subject = SecurityUtils.getSubject();
        if(subject.getPrincipal() != null){
            // 清除cookie
            subject.logout(); // 退出登录

            CookieUtil.removeCookie(request, response, "JSESSIONID");

            String token = request.getHeader("Authorization");

            if(token == null || token.equals("")){
                return ResponseUtil.ok();
            }
            if(!token.startsWith("Bearer ")){
                return ResponseUtil.ok();
            }
            token = token.replace("Bearer ", "");
            AuthCode authCode = this.authCodeService.selectObjByToken(token);
            if(authCode != null){
                // 删除
                this.authCodeService.delete(authCode.getId());
            }

            return ResponseUtil.ok();
        }else{
            return new Result(401,"log in");
        }
    }

}

