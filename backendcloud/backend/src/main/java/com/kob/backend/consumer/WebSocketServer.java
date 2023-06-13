package com.kob.backend.consumer;
import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾  ws://localhost:3000/websocket/${}
public class WebSocketServer {

    public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();//userid-ws链接
    //private static CopyOnWriteArraySet<User> matchpool= new CopyOnWriteArraySet<>();
    private User user;//每个链接要知道对应的用户是谁
    private Session session = null;//自己写的，用来server给前端发信息
    // 每个链接用session维护

    public Game game = null;//本链接的game
    //注入mapper
    public static UserMapper userMapper;
    public static RecordMapper recordMapper;
    private static BotMapper botMapper;
    public static RestTemplate restTemplate;
    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }
    @Autowired
    public void setBotMapper(BotMapper botMapper) {
        WebSocketServer.botMapper = botMapper;
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {//就是jwt token，
        // 建立连接,链接建立的时候自动触发
        this.session = session;//建立连接的时候把session存下来
        System.out.println("server:Connected !");

        Integer userId = JwtAuthentication.getUserId(token);//从建立的ws连接取出token，取出对应的userid
        this.user = userMapper.selectById(userId);//根据userid获取当前链接的用户
        //实现一个身份验证，如果用户身份可以正常解析，就成功
        if (user != null) {
            users.put(userId, this);

        } else {
            this.session.close();
        }

        System.out.println(user);
    }

    @OnClose
    public void onClose() {
        // 关闭链接，链接关闭的时候自动触发
        System.out.println("server:Disconnected!");
        //关闭链接的时候移除本user
        if (this.user != null) {
            users.remove(user.getId());
            //matchpool.remove(this.user);
        }
    }

    public static void startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId) {
        User a = userMapper.selectById(aId);
        User b = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId);//如果id是-1，则是人亲自出马，取出来的是null
        Bot botB= botMapper.selectById(bBotId);
        //匹配好后创建地图
        Game game = new Game(13,
                14,
                20,
                a.getId(),
                botA,
                b.getId(),
                botB);
        game.creat_map();
        if (users.get(a.getId()) != null) {//如果用户还在界面
            users.get(a.getId()).game = game;
        }
        if (users.get(b.getId()) != null) {
            users.get(b.getId()).game = game;
        }

        game.start();//启动一个新线程，转到game.run(),下面的函数仍在此线程


        //把和玩家，地图相关信息封装成json
        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getG());

        //配对好后把消息传给a和b
        JSONObject respA = new JSONObject();
        respA.put("event", "start-matching");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respA.put("game", respGame);
        //从链接池获取a的链接
        if (users.get(a.getId()) != null) {
            users.get(a.getId()).sendMessage(respA.toJSONString());//把a的信息传给前端
        }


        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", respGame);
        //从链接池获取b的链接
        if (users.get(b.getId()) != null)
        users.get(b.getId()).sendMessage(respB.toJSONString());//把a的信息传给前端
    }

    private void startMatching(Integer botId) {
        System.out.println("start matching");
        //matchpool.add(this.user);//把user加到匹配池

        //向matching server发送请求，传当前玩家过去
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        data.add("bot_id", botId.toString());
        //向matching server发送请求
        restTemplate.postForObject(addPlayerUrl, data, String.class);//第三个是返回值的class,java的反射机制
    }

    private void stopMatching() {
        System.out.println("stop matching");
        //matchpool.remove(this.user);

        //向matchingserver发请求，取消这名玩家的匹配
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }

    private void move(int direction) {//小红圈的设置game线程的
        if (game.getPlayerA().getId().equals(this.user.getId())) {//如果本用户是蛇A,设置蛇A的
            if (game.getPlayerA().getBotId().equals(-1)) {//只有亲自出马，才接收人的键盘输入
                game.setNextStepA(direction);//两个线程之间通信
            }

        } else if (game.getPlayerB().getId().equals(this.user.getId())) {
            if (game.getPlayerB().getBotId().equals(-1)) {//亲自出马
                game.setNextStepB(direction);
            }
        }
    }
    @OnMessage
    public void onMessage(String message, Session session) {//当作路由
        // 从Client接收消息，也就是后端接收信息，自动调用该函数
        System.out.println("server:Received Message!");
        JSONObject data = JSONObject.parseObject(message);//把前端通过ws用json传来的msg解析出来
        String event = data.getString("event");//把event域取出来
        if ("start-matching".equals(event)) {
            startMatching(data.getInteger("bot_id"));
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("move".equals(event)) {//接收玩家前端亲自操作发来的移动信息
            move(data.getInteger("direction"));
        }
    }
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    //后端给前端发信息，自己写的
    public void sendMessage(String message) {
        synchronized (this.session) {//异步通信要先加一个锁
            try {
                this.session.getBasicRemote().sendText(message);//从后端向当前链接发送信息
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}



