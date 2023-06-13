package com.kob.backend.service.impl.record;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import com.kob.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import java.util.LinkedList;
import java.util.List;

@Service
public class GetRecordListServiceImpl implements GetRecordListService {
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject getLIst(Integer page) {//展示第page页的信息，每个页面展示10条记录
        IPage<Record> recordIPage = new Page<>(page, 10);//第一个参数：第几页，第二个参数：每一页里有多少个项目
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");//先将所有的record降序排序，
        //排完序后，返回其中第page页的内容
        List<Record> records = recordMapper.selectPage(recordIPage, queryWrapper).getRecords();

        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();//对战双方用户名&头像
        for (Record record : records) {
            User userA = userMapper.selectById(record.getAId());
            User userB = userMapper.selectById(record.getBId());
            JSONObject item = new JSONObject();
            item.put("a_photo", userA.getPhoto());
            item.put("a_username", userA.getUsername());
            item.put("b_photo", userB.getPhoto());
            item.put("b_username", userB.getUsername());
            String result = "平局";
            if ("A".equals(record.getLoser())) {
                result = "B胜";
            } else if ("B".equals(record.getLoser())) {
                result = "A胜";
            }
            item.put("result", result);
            item.put("record", record);
            items.add(item);
        }
        resp.put("records", items);
        resp.put("records_count", recordMapper.selectCount(null));

        return resp;
    }
}
