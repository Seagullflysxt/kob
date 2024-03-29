import $ from "jquery";

export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
        pulling_info: true,//是否正在从云端拉取信息
    },
    getters: {
    },
    mutations: {//调用用store.commit("函数名",)
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token) {
            state.token = token;
        },
        logout(state) {
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        },
        updatePullingInfo(state, pulling_info) {
            state.pulling_info = pulling_info;
        }
    },
    actions: {//调用用store.dispatch("函数名", )
        login(context, data) {
            $.ajax({
                url:"https://app5596.acapp.acwing.com.cn/api/user/account/token/",
                type: "post",
                data: {
                  username: data.username,
                  password: data.password,
                },
                success(resp) {  //成功的话把token存下来
                    //在actions里调用mutation的函数要用commit和字符串
                    if (resp.error_message === "success") {
                        //把token存到本地localstorage里，实现登录持久化
                        localStorage.setItem("jwt_token", resp.token);//
                        context.commit("updateToken", resp.token);
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }                  
                },
                error(resp) {
                  data.error(resp);
                }
              });
        },
        getinfo(context, data) {
            $.ajax({
                url: "https://app5596.acapp.acwing.com.cn/api/user/account/info/",
                type:"get",
                headers: {//要传一个表头,在filter里,获取信息要进行验证，访问的api需要授权的时候要加表头
                    Authorization: "Bearer " + context.state.token,
                },//传给后端token,把对应的用户信息拿过来
                success(resp) {
                    if (resp.error_message === "success") {
                        context.commit("updateUser", {
                        ...resp, //将resp内容解析出来
                        is_login: true,
                    });
                    data.success(resp);
                    } else {
                        data.error(resp);
                    }                 
                },
                error(resp) {
                     data.error(resp);   
                }
            })
        },
        logout(context) {
            localStorage.removeItem("jwt_token");//logout时删掉token
            context.commit("logout");
        }
    },
    modules: {
    }
}