package com.kob.backend.service.impl.user.account;

import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.account.LoginService;
import com.kob.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public Map<String, String> getToken(String username, String password) {
        //先把username和password封装起来，因为数据库里存的不是明文
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);//验证这个用户密码是否可以正常登录
        //如果登录失败，会自动处理，没报异常就继续处理
        //登录成功就可以把用户信息取出来
        UserDetailsImpl loginUser = (UserDetailsImpl)authenticate.getPrincipal();
        User user = loginUser.getUser();//把用户取出来
        //取出来后把userid封装成jwt-token
        String jwt = JwtUtil.createJWT(user.getId().toString());

        Map<String, String> map = new HashMap<>();
        map.put("error_message","success");
        map.put("token",jwt);

        return map;
    }
}
