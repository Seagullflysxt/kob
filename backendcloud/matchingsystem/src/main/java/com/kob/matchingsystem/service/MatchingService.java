package com.kob.matchingsystem.service;

public interface MatchingService {
    String addPlayer(Integer userId, Integer rating, Integer botId);//往匹配池里添加玩家
    String removePlayer(Integer userId);
}
