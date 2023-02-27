<template>
  <div>
    <div>Bot昵称: {{ bot_name }}</div>
    <div>Bot战力: {{ bot_rating }} </div>
  </div>
  <router-view/>
</template>

<script>
import $ from 'jquery';
import { ref } from 'vue';
export default {
  name: "App",
  setup: () => {
    let bot_name = ref("");
    let bot_rating = ref("");

    //向服务器（backend）发送请求
    //后端根据url看controller上的路径，找到函数执行，反水json格式的数据，传回给前端（用户浏览器）
    //用户浏览器拿到数据后会将前端页面这两个值换成传过来的值
    $.ajax({
      url: "http://127.0.0.1:3000/pk/getbotinfo/",
      type: "get",
      success: resp => {
        bot_name.value = resp.name;
        bot_rating.value = resp.rating;
      }
    })
     return {
      bot_name,
      bot_rating
    }
  }
}

</script> 

<style>
body {  
  /*
  @/是当前目录根目录
  */
  background-image: url("@/assets/background.png");
  background-size: cover;
}
</style>
