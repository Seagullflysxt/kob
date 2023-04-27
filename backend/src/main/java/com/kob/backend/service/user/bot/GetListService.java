package com.kob.backend.service.user.bot;

import com.kob.backend.pojo.Bot;

import java.util.List;

public interface GetListService {//每个用户返回自己的botlist
    List<Bot> getList();
}
