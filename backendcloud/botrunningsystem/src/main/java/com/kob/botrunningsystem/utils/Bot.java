package com.kob.botrunningsystem.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Bot implements java.util.function.Supplier<Integer> {
    static class Cell{
        public int x;
        public int y;
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    private boolean check_tail_increasing(int step) { //检查蛇当前回合是否边长
        if (step <= 10) {
            return true;
        }
        if(step % 3 == 1) {
            return true;
        }
        return false;
    }

    public List<Cell> getCells(int sx, int sy, String steps) { //返回蛇的身体
        List<Cell> res = new ArrayList<>();
        steps = steps.substring(1, steps.length() - 1);
        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};

        int x = sx;
        int y = sy;
        int step = 0;//回合数
        res.add(new Cell(x, y));
        for (int i = 0; i < steps.length(); i ++) {
            int d = steps.charAt(i) - '0';
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

    public Integer nextMove(String input) {//一个厉害点的ai,判断四个方向哪个能走，只判一步
        String[] strs = input.split("#");
        int[][] g = new int[13][14];
        //把地图取出来
        for (int i = 0, k = 0; i < 13; i ++) {
            for (int j = 0; j < 14; j ++, k ++) {
                if (strs[0].charAt(k) == '1') {
                    g[i][j] = 1;
                }
            }
        }
        //把两条蛇的身体取出来
        int aSx = Integer.parseInt(strs[1]);
        int aSy = Integer.parseInt(strs[2]);
        String aSteps = strs[3];
        int bSx = Integer.parseInt(strs[4]);
        int bSy = Integer.parseInt(strs[5]);
        String bSteps = strs[6];


        List<Cell> aCells = getCells(aSx, aSy, aSteps);
        List<Cell> bCells = getCells(bSx, bSy, bSteps);
        //把两条蛇的身体在地图里标注一下
        for (Cell c : aCells) {
            g[c.x][c.y] = 1;
        }
        for (Cell c : bCells) {
            g[c.x][c.y] = 1;
        }

        //枚举上下左右4个方向，找到一个空的格子走
        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};
        for (int i = 0; i < 4; i ++) {
            int x = aCells.get(aCells.size() - 1).x + dx[i];//链表的末尾是蛇头
            int y = aCells.get(aCells.size() - 1).y + dy[i];
            if (x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0) {
                return i;//表示这个方向能走
            }
        }
        return 0;//4个方向都走不了的话随便返回一个方向
    }

    @Override
    public Integer get() {//从文件里把输入读入进来
        File file = new File("input.txt");
        try {
            Scanner sc = new Scanner(file);
            return nextMove(sc.next());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
