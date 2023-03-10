import { AcGameObject } from "./AcGameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall";

export class GameMap extends AcGameObject {
    constructor(ctx, parent) {
        super(); //先执行基类的构造函数
        //
        this.ctx = ctx;
        this.parent = parent;
        //每个格子的宽度
        this.L = 0;
        
        this.rows = 13;
        this.cols = 14;

        this.inner_walls_count = 20;
        this.walls = [];

        this.snakes = [
            new Snake({id: 0, color:"#4876EC", r: this.rows - 2, c: 1},this),
            new Snake({id: 1, color:"#F94848", r: 1, c: this.cols - 2},this),
        ];
    }

    check_connectivity(g, sx, sy, tx, ty) {
        if(sx == tx && sy == ty) {
            return true;
        }
        g[sx][sy] = true;
        let dx = [-1, 0, 1, 0], dy = [0, 1, 0, -1];
        for (let i = 0; i < 4; i ++) {
            let x = sx + dx[i], y = sy + dy[i];
            if (!g[x][y] && this.check_connectivity(g, x, y, tx, ty)) {
                return true;
            }
        }
        return false;
         
    }
    creat_walls() {
        const g = [];
        for (let r = 0; r < this.rows; r ++) {
            g[r] = [];
            for (let c = 0; c < this.cols; c ++) {
                g[r][c] = false;
            }
        }
        //给四周加上墙
        for(let r = 0;r < this.rows; r ++) {
            g[r][0] = g[r][this.cols - 1] = true;
        }
        for (let c = 1; c < this.cols - 1; c ++) {
            g[0][c] = g[this.rows - 1][c] = true;
        }

        //创建随机障碍物,random返回[0,1)的随机值
        for (let i = 0; i < this.inner_walls_count/2; i ++) {
            for (let j = 0; j < 1000; j ++) {
                let r = parseInt(Math.random() * this.rows);
                let c = parseInt(Math.random() * this.cols);
                if (g[r][c] || g[this.rows - 1- r][this.cols - 1 - c]) {
                    continue;
                }
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) {
                    continue;
                }

                g[r][c] = g[this.rows - 1- r][this.cols - 1 - c] = true;
                break;
            }
        }

        //先把状态取出来，防止被修改
        //JS里深度复制一个东西
        //检查左下角和右上角是否连通
        //先把g转成json，再用json把它解析出来
        const copy_g = JSON.parse(JSON.stringify(g));

        if (!this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2)) {
            return false;
        }
        for (let r = 0; r < this.rows; r ++) {
            for (let c = 0; c < this.cols; c ++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }

        return true;
    }

    //用来绑定事件
    add_listening_events() {
        //先让canvas聚焦
        this.ctx.canvas.focus();

        const [snake0, snake1] = this.snakes;
        //获取用户输入
        this.ctx.canvas.addEventListener("keydown", e => {
            if (e.key === 'w') {
                snake0.set_direction(0);
            } else if (e.key === 'd') {
                snake0.set_direction(1);
            } else if (e.key === 's') {
                snake0.set_direction(2);
            } else if (e.key === 'a') {
                snake0.set_direction(3);
            } else if (e.key === 'ArrowUp') {
                snake1.set_direction(0);
            } else if (e.key === 'ArrowRight') {
                snake1.set_direction(1);
            } else if (e.key === 'ArrowDown') {
                snake1.set_direction(2);
            } else if (e.key === 'ArrowLeft') {
                snake1.set_direction(3);
            }
    });
}
    start() {
        for (let i = 0; i < 1000; i ++) {
            if (this.creat_walls()) {
                break;
            }
        }
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