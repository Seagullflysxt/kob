package com.kob.backend.service.record;

import com.alibaba.fastjson2.JSONObject;

public interface GetRecordListService {
    //传入一个页表的编号，分页返回
    JSONObject getLIst(Integer page);
}
