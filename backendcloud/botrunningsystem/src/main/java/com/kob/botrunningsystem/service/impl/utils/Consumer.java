package com.kob.botrunningsystem.service.impl.utils;

import com.kob.botrunningsystem.utils.BotInterface;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class Consumer extends Thread {
    private Bot bot;
    private static RestTemplate restTemplate;
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        Consumer.restTemplate = restTemplate;
    }

    private final static String receiveBotMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";

    //带timeout的start
    //timeout:最多执行多长时间
    public void startTimeOut(long timeout, Bot bot) {
        this.bot = bot;
        this.start();//start后会开一个新的线程去执行这个class里的run函数,也就是去执行编译java代码,当前线程还是继续执行，执行join

        //最多等待run线程timeout秒
        try {
            this.join(timeout);//1.run线程执行完后会立即执行这句话后面的2.等待时间达到了timeout秒，继续执行后面的操作
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.interrupt();//等待timeout秒直接中断当前线程
        }

    }

    private String addUid(String code, String uid) {//在code中的Bot类名后加uid
        int k = code.indexOf(" implements com.kob.botrunningsystem.utils.BotInterface");//在这个字符串前面加
        return code.substring(0, k) + uid + code.substring(k);
    }
    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0, 8);//取前8位就行
        //在这里编译执行代码
        BotInterface botInterface = Reflect.compile(//joor里动态编译一段代码
                "com.kob.botrunningsystem.utils.Bot" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();//编译完后创建一个类再获取一下

        Integer direction = botInterface.nextMove(bot.getInput());//调用动态编译后代码里的方法
        System.out.println("move-direction: " + bot.getUserId() + " " + direction);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", direction.toString());
        restTemplate.postForObject(receiveBotMoveUrl, data, String.class);
    }
}
