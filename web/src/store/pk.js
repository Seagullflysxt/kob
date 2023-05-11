
export default {
    state: {
        status: "matching",//正在匹配， playling表示对战界面
        socket: null,//前端和后端建立的连接是什么
        opponent_username: "",//对手的用户名
        opponent_photo: "",//对手的头像
        gamemap: null,
        a_id : 0,
        a_sx: 0,
        a_sy: 0,
        b_id: 0,
        b_sx: 0,
        b_sy: 0,
        gameObject: null,
        loser: "none",//none, all, A, B
    },
    getters: {
    },
    mutations: {//调用用store.commit("函数名",)
        updateSocket(state, socket) {
            state.socket = socket;
        },
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },
        updateStatus(state, status) {
            state.status = status;
        },
        updateGame(state, game) {
            state.gamemap = game.map;
            state.a_id = game.a_id;
            state.a_sx = game.a_sx;
            state.a_sy = game.a_sy;
            state.b_id = game.b_id;
            state.b_sx = game.b_sx;
            state.b_sy = game.b_sy;
        },
        updateGameObject(state, gameObject) {
            state.gameObject = gameObject;
        },
        updateLoser(state, loser) {
            state.loser = loser;
        }
    },
    actions: {//调用用store.dispatch("函数名", )
        
        
        
    },
    modules: {
    }
}