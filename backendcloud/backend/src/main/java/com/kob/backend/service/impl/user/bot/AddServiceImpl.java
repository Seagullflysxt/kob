package com.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddServiceImpl implements AddService {

    @Autowired
    BotMapper botMapper;

    @Override
    public Map<String, String> add(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        //前端传来token,这里把token对应的用户取出来，把用户信息再传给前端
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        //把请求添加的用户取出来

        //要添加的信息
        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Map<String, String> map = new HashMap<>();

        if (title == null || title.length() == 0) {
            map.put("error_message", "标题不能为空");
            return map;
        }
        if (title.length() > 100) {
            map.put("error_message", "标题长度不能大于100");
            return map;
        }
        if (description == null || description.length() == 0) {
            description = "这个用户很懒，什么也没留下~";
        }
        if (description.length() > 300) {
            map.put("error_message", "Bot描述的长度不能大于300");
            return map;
        }
        if (content == null || content.length() == 0) {
            map.put("error_message", "代码不能为空");
            return map;
        }
        if (content.length() > 10000) {
            map.put("error_message", "代码长度不能超过10000");
            return map;
        }

        QueryWrapper<Bot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_Id", user.getId());
        if (botMapper.selectCount(queryWrapper) >= 10) {
            map.put("error_message", "每个用户最多只能创建10个Bot!");
            return map;
        }

        //取回来的data没问题就可以创建bot
        Date now = new Date();
        Bot bot = new Bot(null, user.getId(), title, description, content, now, now);

        //创建好bot就可以加到数据库里了
        botMapper.insert(bot);
        map.put("error_message", "success");

        return map;
    }
}
