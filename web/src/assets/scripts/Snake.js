import { AcGameObject } from "./AcGameObject";
import { Cell } from "./Cell";

export class Snake extends AcGameObject {
    constructor(info, gamemap) {
        super();

        this.id = info.id; //区分两条蛇
        this.color = info.color;//两条蛇颜色不一样
        this.gamemap = gamemap;

        this.cells = [new Cell(info.r, info.c)];//存放蛇的身体，cells[0]存放蛇头
        this.next_cell = null;//下一步的目标位置

        this.speed = 5; //蛇每秒走5个格子
        this.direction = -1;//表示下一回合的指令， -1表示没有指令， 0 1 2 3表示上右下左
        this.status = "idle"; //idle表示静止，move表示正在移动，die已经死了

        this.dr = [-1, 0, 1, 0]; //4个方向行的偏移量
        this.dc = [0, 1, 0, -1];

        this.step = 0;  //当前回合数
        this.eps = 1e-2; //允许的误差，当两个点坐标误差落入这个范围，就认为两个点重合

        this.eye_direction = 0;//左下角的蛇初始朝上
        if (this.id === 1) {
            this.eye_direction = 2; //右上角的蛇初始朝下
        }

        this.eys_dx = [  //蛇眼睛不同方向x的偏移量
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1],
        ];
        this.eys_dy = [  //蛇眼睛不同方向y的偏移量
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],
    ];
    }

    start() {

    }

    //一个统一的接口，用来设置方向
    set_direction(d) {
        this.direction = d;
    }

    //检测当前回合，蛇的长度是否增加。规定：前10回合，每回合都变长,之后每三步，长一格
    check_tail_increasing() {
        if (this.step <= 10) {
            return true;
        }
        if(this.step % 3 === 1) {
            return true;
        }
        return false;
    }
    next_step() {//将蛇的状态变为走下一步
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]); 
        this.direction = -1; //清空操作
        this.status = "move";
        this.step ++;
        this.eye_direction = d;

        //移动的时候只动头和尾，头部要抛出一个新球，尾部向前移动一个,头部多了一个自己的复制
        const k = this.cells.length;
        for(let i = k; i > 0; i --) {
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));
        }
        if (!this.gamemap.check_valid(this.next_cell)) { //下一步操作撞了，蛇瞬间去世
            this.status = "die";
        }
        
    }
    update_move() {       
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);//每一回合需要走的（直线）距离

        if(distance < this.eps) {
            this.cells[0] = this.next_cell; //添加一个新蛇头，将目标点作为真实的头部
            this.next_cell = null;
            this.status = "idle"; //走完了，停下

            if (!this.check_tail_increasing()) {
                this.cells.pop();   //如果蛇不变长，就把尾部砍掉
            }
        } else {
            const move_distance = this.speed * this.timedelta / 1000; //每一帧移动的距离
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy / distance;

            if (!this.check_tail_increasing()) {
                //如果蛇不变长，蛇尾就要走到倒数第二个cell(x,y,r,c一样），否则蛇尾不动
                const k = this.cells.length;
                const tail = this.cells[k - 1];
                const tail_target = this.cells[k - 2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;  //蛇头到目标点的距离和蛇尾到目标点的距离一样
                tail.y += move_distance * tail_dy / distance;
            }
        }
    }
    update() {  //每一帧执行一次
        if (this.status === 'move') {
            this.update_move();
        }
        
        this.render();
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        if(this.status === "die") {
            ctx.fillStyle = "white";
        }
        //画圆
        for (const cell of this.cells) {
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2);
            ctx.fill();
        }
        
        //使蛇圆润
        for (let i = 1; i < this.cells.length; i ++) {
            const a = this.cells[i - 1];
            const b = this.cells[i];
            if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps) {
                continue;   //两个点重合就不用画
            }
            //竖方向
            if (Math.abs(a.x - b.x) < this.eps) {
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
            } else {//横方向
                
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }           
        }

        ctx.fillStyle = "black";
        for (let i = 0; i < 2; i ++) {
            const eye_x = (this.cells[0].x + this.eys_dx[this.eye_direction][i]* 0.15)  * L;
            const eys_y = (this.cells[0].y + this.eys_dy[this.eye_direction][i] *0.15)  * L;
            ctx.beginPath();
            ctx.arc(eye_x, eys_y, L * 0.05, 0, Math.PI * 2)
            ctx.fill();
        }
    }
}