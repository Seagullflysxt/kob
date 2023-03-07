<template>
    <div class="playground">
        <GameMap />
    </div>
</template>

<script>
import GameMap from './GameMap.vue';

export default {
    components: {
        GameMap,
    }
}
</script>

<style scoped>
div.playground{
    /*60%的浏览器宽度
    70%的高度
    margin:上间距40px左右两边用auto可以自动居中 */
    width: 60vw;  
    height: 70vh;
    /*background-color:lightblue; */
    margin: 40px auto;
}
</style>