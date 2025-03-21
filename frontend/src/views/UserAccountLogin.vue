<template>
  <ContentField>
      <div class="row justify-content-md-center">
          <div class="col-3">
              <form>
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
import {ref} from 'vue'
const username = ref("")
const password = ref("")
const error_message = ref("")

</script>

<style scoped>
button {
    width: 100%;
}
div.error-message {
    color: red;
}
</style>
