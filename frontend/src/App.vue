<template>
  <router-link to="/user/account/register">注册</router-link>
  <router-link to="/user/account/login">登录</router-link>
  <router-view/>
</template>

<script setup lang="ts">
// import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"

</script>

<style>
body{
  background-image: url("@/assets/images/10465.jpg");
  background-size: cover;
}

</style>
