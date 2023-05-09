<template>
    <div ref="parent" class="gamemap">
        <canvas ref="canvas" tabindex="0"></canvas>
    </div>
</template>

<script>
import { GameMap } from '@/assets/scripts/GameMap';
import { ref, onMounted } from 'vue';
import { useStore } from 'vuex';

export default{
    setup() {
        const store = useStore();

        let parent = ref(null);
        let canvas = ref(null);
        //当组件挂载完成后需要执行什么操作，创建gamemap对象
        onMounted(() => {
            new GameMap(canvas.value.getContext('2d'), parent.value, store)//store里有从后端发过来的gamemap
        });

        return {
            parent,
            canvas
        }
    }
}
</script>

<style scoped>
div.gamemap {
    /*100%代表和其父元素等长*/
    width:100%;
    height: 100%;
    /*background-color: black;*/
    /*让地图水平以及垂直居中*/
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>