<template>
    <ContentField>
        我的Bot
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import $ from 'jquery'
import { useStore } from 'vuex';

export default {
    components: {
       ContentField 
    },
    setup() {
        const store = useStore();

        // $.ajax({
        //     url: "http://localhost:3000/user/bot/add/",
        //     type: "post",
        //     data: {
        //         title: "Bot的标题",
        //         description: "Bot的描述",
        //         content: "Bot的代码",
        //     },
        //     headers: {//要传一个表头,在filter里,获取信息要进行验证，访问的api需要授权的时候要加表头
        //             Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     }
        // }),
        // $.ajax({
        //     url: "http://localhost:3000/user/bot/update/",
        //     type: "post",
        //     data: {
        //         bot_id: 6,
        //         title: "Bot的标题8",
        //         description: "Bot的描述8",
        //         content: "Bot的代码8",
        //     },
        //     headers: {//要传一个表头,在filter里,获取信息要进行验证，访问的api需要授权的时候要加表头
        //             Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     }
        // })
        $.ajax({
            url: "http://localhost:3000/user/bot/getlist/",
            type: "get",
            
            headers: {//要传一个表头,在filter里,获取信息要进行验证，访问的api需要授权的时候要加表头
                    Authorization: "Bearer " + store.state.user.token,
            },
            success(resp) {
                console.log(resp);
            },
            error(resp) {
                console.log(resp);
            }
        })
        // $.ajax({
        //     url: "http://localhost:3000/user/bot/remove/",
        //     type: "post",
        //     data: {
        //         bot_id: 2,                
        //     },
        //     headers: {//要传一个表头,在filter里,获取信息要进行验证，访问的api需要授权的时候要加表头
        //             Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     }
        // })
    }
}
</script>

<style scoped>

</style>