package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread{
    private static List<Player> players = new ArrayList<>();//多个线程公用，涉及到读写冲突
    private ReentrantLock lock = new ReentrantLock();//加锁，手动变成安全的
    private static RestTemplate restTemplate;
    private final static String startGameUrl = "http://127.0.0.1:3000/pk/start/game/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating, Integer botId) {//调用这个函数会更新players
        lock.lock();
        try {
            players.add(new Player(userId, rating, botId, 0));
        } finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();
        try {
            List<Player> newPlayers = new ArrayList<>();
            for (Player player : players) {
                if (!player.getUserId().equals(userId)) {
                    newPlayers.add(player);
                }
            }
            players = newPlayers;
        } finally {
            lock.unlock();
        }
    }

    private void increaseWaitingTime() {//将当前所有等待玩家的等待时间加1
        for (Player player : players) {
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    private boolean checkMatch(Player a, Player b) {//判断两名玩家是否匹配
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        int waitingTime = Math.min(a.getWaitingTime(), b.getWaitingTime());
        return ratingDelta <= waitingTime * 10;
    }

    private void sendResult(Player a, Player b) {//返回匹配结果
        System.out.println("send result: " + a + " " + b);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("a_id", a.getUserId().toString());
        data.add("a_bot_id", a.getBotId().toString());
        data.add("b_id", b.getUserId().toString());
        data.add("b_bot_id", b.getBotId().toString());
        restTemplate.postForObject(startGameUrl, data, String.class);
    }

    private void matchPlayers() {//这个线程每秒执行一次，尝试匹配所有玩家
        System.out.println("match players: " + players.toString());
        boolean[] used = new boolean[players.size()];//true 表示该玩家已经匹配到对手
        //等待时间越长的玩家越优先匹配，否则可能会流失掉
        //players里靠前的玩家等待时间越长，从前往后匹配玩家
        for (int i = 0; i < players.size(); i ++) {
            if (used[i]) {
                continue;
            }
            for (int j = i + 1; j < players.size(); j ++) {
                if (used[j]) {
                    continue;
                }
                Player a = players.get(i);//如果都没匹配到，就取出两名用户，尝试匹配
                Player b = players.get(j);
                if (checkMatch(a, b)) {//i能匹配上了
                    used[i] = true;
                    used[j] = true;
                    sendResult(a, b);//如果能匹配到一起，就返回结果
                    break;
                }
            }
        }

        //把匹配的玩家从玩家池里删掉
        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i ++) {
            if (!used[i]) {
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;
    }
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);//每一次循环，先sleep 1秒
                lock.lock();
                try {
                    increaseWaitingTime();//每隔1秒，将所有当前玩家等待时间加1
                    matchPlayers();
                } finally {
                    lock.unlock();
                }


            } catch (InterruptedException e) {
                e.printStackTrace();//如果报异常，输出异常信息
                break;//然后break掉
            }
        }
    }
}
