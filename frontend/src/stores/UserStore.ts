import axios from 'axios'
import { defineStore } from 'pinia'
import qs from 'qs'


interface LoginReq{
  username: string;
  password: string;
}



export const userStore = defineStore('UserStore', {
  state: ()=>({
    id: '',
    username: '',
    password: '',
    photo: '',
    token: '',
    is_login: false,
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
            this.is_login = true
          } else {
            console.log("something unexpectable error")
          }
        })
    },
    async getinfo(){
      await axios.post('http://localhost:8080/user/account/info',{
        headers :{
          'Authorization' : `Bearer ${this.token}`
        }
      }).then((result)=>{
        if(result.data.error_message === 'success'){
          this.id = result.data.id
          this.username = result.data.username
          this.photo = result.data.photo
        }else{
          console.log("get token error")
        }
      })
    },
    logout(){
      localStorage.removeItem("jwt_token")
      this.is_login = false
    },
    updateToken(token:string){
      this.token = token
    },
  },
})
