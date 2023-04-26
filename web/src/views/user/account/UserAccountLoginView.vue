<template>
    <ContentField v-if="!$store.state.user.pulling_info">
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="error-message">{{ error_message }}</div>
                    <button type="submit" class="btn btn-primary">提交</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import { useStore } from 'vuex'  //全局变量
import { ref } from 'vue'  //变量
import router from '../../../router/index'

export default {
    components: {
       ContentField 
    }, 
    setup() {
        const store = useStore();
        let username = ref('');
        let password = ref('');
        let error_message = ref('');
        

        const jwt_token = localStorage.getItem("jwt_token");
        if (jwt_token) {//如果本地jwttoken存在，取出来更新
            store.commit("updateToken", jwt_token);
            store.dispatch("getinfo", {
                success(resp) {//如果getinfo成功，直接跳转到首页
                    console.log(resp);
                    router.push({ name: 'home'});
                },
                error() {
                    store.commit("updatePullingInfo", false);//如果失败了，就展示登录页面
                }
            })
        } else {
            store.commit("updatePullingInfo", false);
        }
        const login = () => { //提交的话触发这个函数,line5
            error_message.value = "";
            store.dispatch("login", { //调用store/index.js/actions里的login函数
                username: username.value,
                password: password.value,
                success() {
                    store.dispatch("getinfo", {
                        success() {
                            router.push({name:'home'});//登录成功且用户信息从后端取到后跳转到home页面
                            //console.log(store.state.user);
                        }
                    })
                    
                },
                error() {
                    error_message.value = "用户名或密码错误";               
                }
            })//调用store里actions的函数这样调用
        }

        return {
            username,
            password,
            error_message,
            login,
        }
    }
}
</script>

<style scoped>
button {
    width: 100%;
}
div.error-message {
    color:red;
}
</style>