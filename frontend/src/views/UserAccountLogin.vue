<template>
  <ContentField>
      <div class="row justify-content-md-center">
          <div class="col-3">
              <form @submit.prevent = "handleSubmit">
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

<script setup lang="ts">
import router from '@/router'
import { userStore } from '@/stores/UserStore'
import {ref} from 'vue'
const store = userStore()
const username = ref("")
const password = ref("")
const error_message = ref("")

function shouldLogin(){
  router.push("{name:'/home'}")
}

function errorLogin(err){
  console.log(`登录错误${err}`)
  error_message.value = "登录错误"
}

function handleSubmit(){
  const jwt_token = localStorage.getItem("jwt_token");
  if(username.value === ""){
    error_message.value = "用户名不能为空"
  }
  if(password.value === ""){
    error_message.value = "密码不能为空"
  }

  if(jwt_token){
    store.updateToken(jwt_token)
    store.getinfo().then(shouldLogin).catch(()=>{
      store.login({
        username: username.value,
        password: password.value
      }).then(shouldLogin).catch((err)=>errorLogin(err))
    })
  }else{
    store.login({
      username: username.value,
      password: password.value
    }).then(shouldLogin).catch((err)=>errorLogin(err))
  }
}


</script>

<style scoped>
button {
    width: 100%;
}
div.error-message {
    color: red;
}
</style>
