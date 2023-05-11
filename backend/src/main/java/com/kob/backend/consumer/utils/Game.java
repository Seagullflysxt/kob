package com.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g;
    private static final int[] dx = {-1, 0, 1, 0};
    private static final int[] dy = {0, 1, 0, -1};
    private Player playerA = null;
    private Player playerB = null;
    private Integer nextStepA = null;//0 1 2 3表示上下左右
    private Integer nextStepB = null;
    private ReentrantLock lock = new ReentrantLock();//给会多个线程同时读写的操作加锁
    private String status = "playing";//整个游戏的状态 playing->finished
    private String loser = ""; //all:平局， A：A输， B：B输

    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Integer idB) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        this.playerA = new Player(idA, rows - 2, 1, new ArrayList<>());
        this.playerB = new Player(idB, 1, cols - 2, new ArrayList<>());
    }

    public Player getPlayerA() {
        return this.playerA;
    }
    public Player getPlayerB() {
        return this.playerB;
    }

    public void setNextStepA(Integer nextStepA) {//在wsserver里调用
        lock.lock();//给这个操作加锁，因为可能会同时读写
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();//不管有没有报异常，操作完后解锁
        }

    }
    public void setNextStepB(Integer nextStepB) {
        lock.lock();//操作前先拿锁
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }

    }
    public int[][] getG() {
        return g;
    }

    private boolean check(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) {
            return true;
        }
        g[sx][sy] = 1;
        for (int i = 0; i < 4; i ++) {
            int x = sx + dx[i];
            int y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if (check(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }
        g[sx][sy] = 0;
        return false;
    }

    private boolean draw() {  //画地图
        for (int i = 0; i< this.rows; i ++) {
            for (int j = 0; j < this.cols; j ++) {
                g[i][j] = 0; //0是空地，1是障碍物
            }
        }
        //给四周加上墙
        for(int r = 0;r < this.rows; r ++) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for (int c = 1; c < this.cols; c ++) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }
        //创建随机障碍物,random返回[0,1)的随机值
        Random random= new Random();
        for (int i = 0; i < this.inner_walls_count / 2; i ++) {
            for (int j = 0; j < 1000; j ++) {
                int r = random.nextInt(this.rows);//[0, rows)的一个随机整数
                int c = random.nextInt(this.cols);
                //检查障碍物合法性
                if (g[r][c] == 1 || g[this.rows - 1- r][this.cols - 1 - c] == 1) {
                    continue;
                }
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) {
                    continue;
                }
                //合法就放置障碍物
                g[r][c] = g[this.rows - 1- r][this.cols - 1 - c] = 1;
                break;
            }
        }
        return check(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void creat_map() {
        for (int i = 0; i < 1000; i ++) {
            if (draw()) {
                break;
            }
        }
    }

    private boolean nextStep() {//等待两个玩家的下一步操作

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < 50; i ++) {//等待5s，每秒判断1次
            try {
                Thread.sleep(100);//sleep1s再锁住，还跟用户延迟有关，循环越多服务器压力越大，但是用户体验更好
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {//两个玩家都输入了操作
                        playerA.getSteps().add(nextStepA);//不管是否合法，只要收到前端传来的移动信息，都加到对应玩家的steps里
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();//无论如何都会unlock
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) { //判断a, b是否有重合
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);//最新的蛇头，也就是前端刚输入的移动信息的位置
        if (g[cell.x][cell.y] == 1) {//蛇头撞墙
            return false;
        }
        for (int i = 0; i < n - 1; i ++) {
            if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y) {//蛇头a撞到自己身体
                return false;
            }
        }
        for (int i = 0; i < n - 1; i ++) {
            if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y) {//蛇头a撞到b的身体
                return false;
            }
        }
        return true;
    }
    private void judge() {//判断两个玩家下一步操作是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if (!validA || !validB) {
            status = "finished";

            if (!validA && !validB) {
                loser = "all";
            } else if (!validA) {
                loser = "A";
            } else {
                loser = "B";
            }
        }
    }

    private void sendAllMessage(String message) {//向两名玩家广播信息
        WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }
    private void sendMove() { //server把接收到的两个玩家的操作都广播给他们，传递移动信息
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());

            //移动信息发给前端后，要准备获取下一步
            nextStepA = null;
            nextStepB = null;
        } finally {
            lock.unlock();
        }


    }

    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < rows; i ++) {
            for (int j = 0; j < cols; j ++) {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }
    private void saveToDatabase() {//把对局记录保存到数据库
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );
        WebSocketServer.recordMapper.insert(record);
    }
    private void sendResult() {//向两个玩家(前端）公布游戏结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }
    @Override
    public void run() {//多线程的入口函数
        for (int i = 0; i < 1000; i ++) {
            if (nextStep()) {  //是否获取了两条蛇的下一步操作
                judge();
                if (status.equals("playing")) {
                    sendMove();//server接收到两个玩家输入后，判断合法后，要将每个玩家的输入分别广播给他们
                } else {
                    sendResult();
                    break;
                }
            } else {
                status = "finished";
                //涉及到nextstep的读
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null) {
                        loser = "all";
                    } else if (nextStepA == null) {
                        loser = "A";
                    } else {
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
