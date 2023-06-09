package com.kob.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private Queue<Bot> bots = new LinkedList<>();


    public void addBot(Integer userId, String botCode, String input) {
        lock.lock();
        try {
            bots.add(new Bot(userId, botCode, input));
            condition.signalAll();//唤起别的线程，如果在await()阻塞住了，就会被唤醒
        } finally {
            lock.unlock();
        }
    }

    //执行代码
    private void consume(Bot bot) {//为了简单此处只执行java代码，
        // 以后想为了安全同时支持多语言的话，只用改这个函数，改为docker的一个执行（先搜java里怎么执行终端命令，把命令放进来就可以了
        Consumer consumer = new Consumer();
        consumer.startTimeOut(2000, bot);//2s
    }
    @Override
    public void run() {
        while (true) {
            lock.lock();
            if (bots.isEmpty()) {//如果队列空，就把本线程阻塞住
                try {
                    condition.await();//睡住的时候会自动将锁释放，await里自带一个锁释放操作
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();//如果报异常记得要解锁
                    break;
                }
            } else {//不空就把队头拿出来执行
                Bot bot = bots.remove();//返回并删除队头
                lock.unlock();//取完对头后就可以解锁了

                //解锁完后要记录 消耗一下这个任务
                consume(bot);//这个函数会比较耗时，可能会执行几秒钟，因为编译执行一段代码是很慢的，做这个操作前一定要解锁
                //正在执行bot代码的时候，如果有别的任务进来，十个八个都会加到队列里面
            }
        }
    }
}
