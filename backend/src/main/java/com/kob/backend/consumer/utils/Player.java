package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private Integer id;
    private Integer sx;
    private Integer sy;
    private List<Integer> steps;//玩家每一步的方向

    private boolean check_tail_increasing(int step) { //检查蛇当前回合是否边长
        if (step <= 10) {
            return true;
        }
        if(step % 3 == 1) {
            return true;
        }
        return false;
    }

    public List<Cell> getCells() { //返回蛇的身体
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};

        int x = sx;
        int y = sy;
        int step = 0;//回合数
        res.add(new Cell(x, y));
        for (int d : steps) {
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            step ++;
            if (!check_tail_increasing(step)) {//不变长就把蛇尾删掉
                res.remove(0);
            }
        }
        return res;
    }
    public String getStepsString() {
        StringBuilder res = new StringBuilder();
        for (int d : steps) {
            res.append(d);
        }
        return res.toString();
    }
}
