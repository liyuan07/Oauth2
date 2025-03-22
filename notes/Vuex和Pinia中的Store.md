# Vuex和Pinia中的Store

## Vuex

vuex中的store默认有五种基本对象，

1. state：存储状态（变量），是唯一的

2. getters：在获取数据之前的再次编译，可以理解为state的计算属性。在组件中使用$store.**getters.**fucname()

   ```js
   export default{
   	totalSize(state){
   		return state.todos.length
   	}
   }
   ```

   

3. mutations：同步的修改state。在组建中使用$store.**commit**('mutation name')。mutation的定义的第一个参数是state

   ```js
   export default{
       state: {
           id: "",
           username : "",
           password: "",
           photo: "",
           token: "",
           is_login: false,
           pulling_info: true,
       },
       getters:{
   
       },
       mutations: {
           updateUser(state, user) {
               state.id = user.id;
               state.username = user.username;
               state.photo = user.photo;
               state.is_login = user.is_login;
           },
           updateToken(state, token) {
               state.token = token;
           },
           logout(state) {
               state.id = "";
               state.username = "";
               state.photo = "";
               state.token = "";
               state.is_login = false;
           },
           updatePullingInfo(state , pulling_info){
               state.pulling_info = pulling_info;
           }
       },
   ```

   

4. actions：异步操作间接修改state，可以包含定时器和ajax等。在组建中使用$store.dispatch('actionname',data)。它接收一个`context`对象作为参数，这个对象包含了`state`、`getters`、`commit`等属性

   ```js
   login(context , data){
               $.ajax({
                   url: "http://localhost:3000/user/account/token/",
                   type: "post",
                   data: {
                       username : data.username,
                       password : data.password,
                   },
                   success(resp){
                       if(resp.error_message === 'success'){
                           localStorage.setItem("jwt_token" , resp.token);
                           context.commit("updateToken" , resp.token);
                           data.success(resp);
                       }else{
                           data.error(resp);
                       }
                   },
                   error(resp){
                       data.error(resp);
                   }
               });
           },
   ```

   `context`并不是一个完整的`store`实例，但它包含了与`store`相关的一些属性和方法，使得`action`能够和`store`进行交互，同时也能保证一定的隔离性。例如，`context`对象不包含`store`的一些私有方法和属性，这样可以避免在`action`中意外修改`store`的内部状态。

   - **`state`**：和`store`里的`state`相同，可用于访问`store`的状态。
   - **`getters`**：和`store`里的`getters`相同，可用于访问`store`的`getters`。
   - **`commit`**：这是一个方法，用于提交`mutation`，从而改变`store`的状态。
   - **`dispatch`**：这是一个方法，用于触发另一个`action`。

5. modules：store的子模块，为了开发大型项目，方便状态管理。

## Pinia

在`action`里可以直接访问`this`来获取`state`和`getters`。

Pinia 的`action`不区分同步和异步操作，你既可以在`action`里进行同步操作，也能进行异步操作，比如发送网络请求、设置定时器等。

没有`context`了，也不需要使用context.submit啥的了。接下来看一下vuex中的一段代码。这段代码使用了context.submit来调用mutation中的updateToekn。

```js
import $ from 'jquery'

export default{
    state: {
        token: "",
    },
    getters:{

    },
    mutations: {
        updateToken(state, token) {
            state.token = token;
        }
    },
    actions: {
        login(context , data){
            $.ajax({
                url: "http://localhost:3000/user/account/token/",
                type: "post",
                data: {
                    username : data.username,
                    password : data.password,
                },
                success(resp){
                    if(resp.error_message === 'success'){
                        localStorage.setItem("jwt_token" , resp.token);
                        context.commit("updateToken" , resp.token);
                        data.success(resp);
                    }else{
                        data.error(resp);
                    }
                },
                error(resp){
                    data.error(resp);
                }
            });
        }
    modules: {
    }
}

```

在pinia中，可以直接使用mutation中的方法，如下所示。直接使用`this.updateToken`就可以了。在组件外也直接使用store.updateToken就可以了

```js
import axios from 'axios'
import { defineStore } from 'pinia'
import qs from 'qs'


interface LoginReq{
  username: string;
  password: string;
}



export const userStore = defineStore('UserStore', {
  state: ()=>({
    token: '',
  }),
  getters: {

  },
  actions: {
    async login(data : LoginReq) {
      await axios
        .post('http://localhost:8080/user/account/login', qs.stringify({
          username: data.username,
          password: data.password,
        }),{
          headers: {
            'Content-Type' : "application/x-www-form-urlencoded"
          }
        })
        .then((result) => {
          if (result.data.error_message === 'success') {
            localStorage.setItem('jwt_token', result.data.token)
            this.updateToken(result.data.token)
          } else {
            console.log("something unexpectable error")
          }
        })
        .catch((err) => {
          console.log('login error' + err)
        })
    }
    updateToken(token:string){
      this.token = token
    }
  },
})

```