<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'"/>      
    <MatchGround v-if="$store.state.pk.status === 'matching'"/> 
</template>

<script>
import PlayGround from '../../components/PlayGround.vue'
import MatchGround from '../../components/MatchGround.vue'
import { onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex'

export default {
    components: {
       PlayGround,
       MatchGround,
    },
    setup() {
        const store = useStore();
        const socketUrl = `ws://localhost:3000/websocket/${store.state.user.token}`;

        let socket = null;
        onMounted(() => {//组件挂载完成后创建ws链接
            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            });
            
            socket = new WebSocket(socketUrl);

            socket.onopen = () => {
                console.log("client: connected!");
                store.commit("updateSocket", socket);
            }

            socket.onmessage = msg => { //对方传来的msg,msg里有一个数据，
                const data = JSON.parse(msg.data);
                //console.log(data);
                if (data.event === "start-matching") {//匹配成功
                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                    });
                    setTimeout(() => {
                        store.commit("updateStatus", "playing");
                    }, 2000);//2000ms之后执行
                    store.commit("updateGamemap", data.gamemap);//更新地图
                }
            }

            socket.onclose = () => {
                console.log("client:disconnected!");
            }
        });

        onUnmounted(() => {
            socket.close();//断开链接
            store.commit("updateStatus", "matching");//对战时如果跳到其他页面视为lose
        })
    }
}
</script>

<style scoped>

</style>