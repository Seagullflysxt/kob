package com.kob.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @TableId(type = IdType.AUTO) //id改为自增后要加的plus注解，new的时候不用传入id,设置为null就行了
    private Integer id;
    private String username;
    private String password;
    private String photo;
    private Integer rating;
}
