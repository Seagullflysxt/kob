import $ from "jquery";

export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
    },
    getters: {
    },
    mutations: {
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
        }
    },
    actions: {
        login(context, data) {
            $.ajax({
                url:"http://localhost:3000/user/account/token/",
                type: "post",
                data: {
                  username: data.username,
                  password: data.password,
                },
                success(resp) {  //成功的话把token存下来
                    //在actions里调用mutation的函数要用commit和字符串
                    if (resp.error_message === "success") {
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
                url: "http://localhost:3000/user/account/info/",
                type:"get",
                headers: {//要传一个表头,在filter里,获取信息要进行验证
                    Authorization: "Bearer " + context.state.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        context.commit("updateUser", {
                        ...resp, //将resp内容解析出来
                        is_login: true,
                    });
                    data.success(resp);
                    }  else {
                        data.error(resp);
                    }                 
                },
                error(resp) {
                     data.error(resp);   
                }
            })
        },
        logout(context) {
            context.commit("logout");
        }
    },
    modules: {
    }
}