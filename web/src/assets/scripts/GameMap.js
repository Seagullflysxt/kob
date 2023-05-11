import { AcGameObject } from "./AcGameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall";
//import { useStore } from "vuex";

export class GameMap extends AcGameObject {
    constructor(ctx, parent, store) {
        super(); //先执行基类的构造函数
        //
        this.ctx = ctx;
        this.parent = parent;
        //每个格子的宽度
        this.L = 0;
        this.store = store;
        
        this.rows = 13;
        this.cols = 14;

        this.inner_walls_count = 20;
        this.walls = [];

        this.snakes = [
            new Snake({id: 0, color:"#4876EC", r: this.rows - 2, c: 1},this),
            new Snake({id: 1, color:"#F94848", r: 1, c: this.cols - 2},this),
        ];
    }

    
    creat_walls() {
        const g = this.store.state.pk.gamemap;
        for (let r = 0; r < this.rows; r ++) {
            for (let c = 0; c < this.cols; c ++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
     }

    //用来绑定事件
    add_listening_events() {
        //先让canvas聚焦
        this.ctx.canvas.focus();

        //const [snake0, snake1] = this.snakes;
        //获取用户输入
        this.ctx.canvas.addEventListener("keydown", e => {
            let d = -1;
            if (e.key === 'w') {
                d = 0;
            } else if (e.key === 'd') {
                d = 1;
            } else if (e.key === 's') {
                d = 2;
            } else if (e.key === 'a') {
                d = 3;
            } 
            // this.store.state.pk.socket.send(JSON.stringify({
            //     test: "hihihi",
            // }));
            
            if (d >= 0) { //如果操作合法，就要向后端发送移动的请求
                this.store.state.pk.socket.send(JSON.stringify({
                    event: "move",
                    direction: d,
                }));
            }
        });
    }
    start() {
        this.creat_walls();
        this.add_listening_events();
    }
    //地图大小为13*13个格子，每一帧都更新一下格子边长
    update_size() {
        //.clentWidth是获取div的宽度
        //this.parent
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    //check两个玩家是否准备好下一步操作，两个蛇都走完了且都获取了下一步操作
    check_ready() {
        for (const snake of this.snakes) {
            if (snake.status !== "idle") {
                return false;
            }
            if (snake.direction === -1) {
                return false;
            }
        }
        return true;
    }

    next_step() {  //让两条蛇进入下一回合
        for(const snake of this.snakes) {
            snake.next_step();
        }
    }

    check_valid(cell) { //检测目标位置是否合法：没有撞到两条蛇的身体和障碍物
        for(const wall of this.walls) {
            if (wall.r === cell.r && wall.c === cell.c) {
                return false;
            }
        }
        for (const snake of this.snakes) {
            let k = snake.cells.length;
            if (!snake.check_tail_increasing()) { //蛇尾前进的时候，蛇尾不要判断
               k --;
            }
            for (let i = 0; i < k; i ++) {
                //cell撞到了蛇的身体就false
                if (snake.cells[i].r === cell.r && snake.cells[i].c === cell.c) {
                    return false;
                }
            }
            return true;
        }
    }

    update() {
        this.update_size();
        if(this.check_ready()) {
            this.next_step();
        }
        this.render();
    }

    render() {
        const color_even = "#AAD751";
        const color_odd = "#A2D048";
        for (let r = 0; r < this.rows; r ++) {
            for (let c = 0; c < this.cols; c ++) {
                if ((r + c) % 2 == 0) {
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}