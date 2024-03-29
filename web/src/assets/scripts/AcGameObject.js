const AC_GAME_OBJECTS = [];

export class  AcGameObject {
    constructor() {
        //每创建一个对象，就push一个
        //先创建的先执行update
        AC_GAME_OBJECTS.push(this);
        this.timedelta = 0; //每两帧之间的时间，单位是ms
        this.has_called_start = false;
    }

    start() {  //第一帧执行，只执行一次

    }
    update() { //每一帧执行一次，除了第一帧之外

    }
    on_destroy() { //删除之前执行

    }
    destroy() { //从数组里删除该对象
        this.on_destroy();
        
        for (let i in AC_GAME_OBJECTS) {
            const obj = AC_GAME_OBJECTS[i];
            if (obj === this) {
                AC_GAME_OBJECTS.splice(i);
                break;
            }
        }
    }
}

let last_timestamp; //上一次执行的时刻
const step = (timestamp) => { //传入当前函数执行的时刻
    for (let obj of AC_GAME_OBJECTS) {
        if (!obj.has_called_start) {
            obj.has_called_start = true;
            obj.start();
        } else {
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }
    last_timestamp = timestamp;
    requestAnimationFrame(step);
}
requestAnimationFrame(step);