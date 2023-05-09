

export default {
    state: {
        status: "matching",//正在匹配， playling表示对战界面
        socket: null,//前端和后端建立的连接是什么
        opponent_username: "",//对手的用户名
        opponent_photo: "",//对手的头像
        gamemap: null,
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
        updateGamemap(state, gamemap) {
            state.gamemap = gamemap;
        }
    },
    actions: {//调用用store.dispatch("函数名", )
        
        
        
    },
    modules: {
    }
}