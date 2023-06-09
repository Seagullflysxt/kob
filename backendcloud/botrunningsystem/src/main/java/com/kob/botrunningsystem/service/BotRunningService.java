package com.kob.botrunningsystem.service;

public interface BotRunningService {
    /**
    bot的userid,bot的执行代码，input是当前地图的信息（哪些地方是障碍物，两条蛇位置,已经走过的格子
    **/
    String addBot(Integer userId, String botCode, String input);

}
