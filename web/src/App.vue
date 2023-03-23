<template>
  <NavBar />
  <router-view/>
</template>

<script>
import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
//import $ from 'jquery'

export default {
  components: {
    NavBar
  },
  //setup() {
  //   $.ajax({
  //     url:"http://localhost:3000/user/account/token/",
  //     type: "post",
  //     data: {
  //       username: 'bb',
  //       password: 'pbb',
  //     },
  //     success(resp) {  //resp就是后端返回的map
  //       console.log(resp);
  //     },
  //     error(resp) {
  //       console.log(resp);
  //     }
  //   });

  //   $.ajax({
  //     url:"http://localhost:3000/user/account/info/",
  //     type: "get",
  //     headers: {//要传一个表头,在filter里,获取信息要进行验证
  //       Authorization: "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3YjYyYmY5OWJhMTk0NGZiYjg3Y2Q5ZjhhYzZlNmEyMSIsInN1YiI6IjIiLCJpc3MiOiJzZyIsImlhdCI6MTY3OTMxNDU4OSwiZXhwIjoxNjgwNTI0MTg5fQ.leXo0I6mNbYkORs3hnRJ_GWzZUCo7dGaOVITxxw3_4A"
  //     },
  //     success(resp) {
  //       console.log(resp);
  //     },
  //     error(resp) {
  //       console.log(resp);
  //     }
  //   });

  //   $.ajax({
  //     url:"http://localhost:3000/user/account/register/",
  //     type: "post",
  //     data: {
  //       username: "dd",
  //       password: "pdd",
  //       confirmedPassword: "pdd",
  //     },
  //     success(resp) {
  //       console.log(resp);
  //     },
  //     error(resp) {
  //       console.log(resp);
  //     }
  //   });
  // }
}

</script> 

<style>
body {  
  /*
  @/是当前目录根目录
  */
  background-image: url("@/assets/images/background.jpg");
  background-size: cover;
}
</style>
