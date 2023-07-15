package com.zsx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zsx.reggie.common.R;
import com.zsx.reggie.entity.User;
import com.zsx.reggie.service.UserService;
import com.zsx.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/sendSMS")
    public R<String> sendSMS(@RequestBody User user, HttpSession session) throws Exception {

        //获取手机号
        String userPhone = user.getPhone();

        if (StringUtils.isNotEmpty(userPhone)) {
            //生成验证码
            String code = ValidateCodeUtils.generateValidateCode4String(4);

            log.info("手机号{}的验证码：{}", userPhone, code);

            //调用阿里云短信服务，发短信，填入code和其他参数
//            SMSUtils smsUtils = new SMSUtils();
//            smsUtils.sendSms( userPhone, code);

            //需要将短信验证码保存到session中
            session.setAttribute(userPhone, code);

            return R.success("验证码发送成功");
        }

        return R.error("发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) throws Exception {
        log.info("请求参数：{}", map);

        String phone = map.get("phone").toString();

        String code = map.get("code").toString();

        //取出session中的code，进行比对
        String codeInSession = session.getAttribute(phone).toString();

        if (StringUtils.isNotEmpty(codeInSession) && codeInSession.equals(code)){
            //查看是否是新用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(queryWrapper);
            //如果是新用户，则自动注册到表里
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }

        return R.error("登录失败");
    }

}
