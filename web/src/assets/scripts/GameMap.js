import { AcGameObject } from "./AcGameObject";
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
        this.cols = 13;

        this.inner_walls_count = 20;
        this.walls = [];
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
                if (g[r][c] || g[c][r]) {
                    continue;
                }
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) {
                    continue;
                }

                g[r][c] = g[c][r] = true;
                break;
            }
        }

        //先把状态取出来，防止被修改
        //JS里深度复制一个东西
        //检查左下角和右上角是否连通
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
    start() {
        for (let i = 0; i < 1000; i ++) {
            if (this.creat_walls()) {
                break;
            }
        }
        
    }
    //地图大小为13*13个格子，每一帧都更新一下格子边长
    update_size() {
        //.clentWidth是获取div的宽度
        //this.parent
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    update() {
        this.update_size();
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