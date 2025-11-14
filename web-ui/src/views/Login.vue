<template>
  <div class="login" :style="{ backgroundImage: 'url(' + backgroundImage + ')' }">
    <div class="login-content">
      <div class="login-container">
        <img :src="loginFormImage" class="loginFormImage" />
        <div class="div-form">
          <div class="form_title">欢迎登录系统</div>
          <a-form class="form" :model="formState" name="basic" :label-col="{ span: 4 }" :wrapper-col="{ span: 16 }"
            autocomplete="off" @finish="onFinish" @finishFailed="onFinishFailed">
            <a-form-item label="账号" name="username" class="username" :rules="[{ required: true, message: '账号不能为空' }]">
              <a-input name="username" v-model:value="formState.username" size="large" allowClear />
            </a-form-item>

            <a-form-item label="密码" name="password" class="password" :rules="[{ required: true, message: '密码不能为空' }]">
              <a-input-password name="password" v-model:value="formState.password" size="large" allowClear />
            </a-form-item>

            <a-form-item label="记住账号" name="remember">
              <a-checkbox name="remember" v-model:checked="formState.remember">是</a-checkbox>
            </a-form-item>

            <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
              <a-button block type="primary" html-type="submit" size="large">登陆</a-button>
            </a-form-item>
          </a-form>
          <div class="form_tail">
            <div class="form_tail_left"><a-button type="link">注册账号</a-button></div>
            <div class="form_tail_right"><a-button type="link">忘记密码</a-button></div>
          </div>
        </div>
      </div>
    </div>
    <ViewChoose></ViewChoose>
  </div>
</template>

<script>
import { reactive, defineComponent } from 'vue'
import backgroundImage from '@/assets/backgroup/login-bg.png'
import loginFormImage from '@/assets/backgroup/login_form.png'
import { mapActions } from 'pinia'
import { useUserStore } from '@/stores/user'
import ViewChoose from './login/ViewChoose.vue'
export default defineComponent({
  data() {
    return {
      backgroundImage,
      loginFormImage
    }
  },

  methods: {
    ...mapActions(useUserStore, ['setToken', 'setUser']),
    onFinish(values) {
      this.$http
        .request({
          url: '/personkit/login',
          data: values,
          method: 'post'
        })
        .then((res) => {
  
          this.setToken(res.value)
          this.doAction()
        })
        .catch((err) => console.log(err))
    },

    onFinishFailed(errorInfo) {
      console.log('Failed:', errorInfo)
    },

    doAction() {
      this.$router.push({ name: 'home' })
    }
  },
  setup() {
    const formState = reactive({
      username: 'yishui',
      password: '123456',
      remember: true
    })
    return {
      formState
    }
  },
  components: {
    ViewChoose
  }
})
</script>

<style scoped>
.login {
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-content {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: 1200px;
  width: 90%;
  height: 70vh;
  min-height: 500px;
  max-height: 700px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 2rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.loginFormImage {
  width: 50%;
  height: 100%;
  object-fit: cover;
  flex-shrink: 0;
}

.div-form {
  width: 50%;
  height: 100%;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background-color: white;
}

.form_title {
  margin-bottom: 40px;
  font-size: 2rem;
  font-weight: 700;
  color: #333;
  text-align: center;
}

.form {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form_tail {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .login-container {
    width: 95%;
    height: 80vh;
    max-height: 600px;
  }
  
  .loginFormImage {
    width: 45%;
  }
  
  .div-form {
    width: 55%;
    padding: 30px;
  }
  
  .form_title {
    font-size: 1.8rem;
    margin-bottom: 30px;
  }
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    height: auto;
    min-height: 80vh;
    max-height: none;
    width: 90%;
    max-width: 400px;
  }
  
  .loginFormImage {
    width: 100%;
    height: 200px;
    border-radius: 2rem 2rem 0 0;
  }
  
  .div-form {
    width: 100%;
    padding: 30px 20px;
  }
  
  .form_title {
    font-size: 1.6rem;
    margin-bottom: 25px;
  }
}

@media (max-width: 480px) {
  .login-container {
    width: 95%;
    max-width: 350px;
  }
  
  .div-form {
    padding: 20px 15px;
  }
  
  .form_title {
    font-size: 1.4rem;
    margin-bottom: 20px;
  }
}

/* 超小屏幕适配 */
@media (max-width: 320px) {
  .login-container {
    border-radius: 1rem;
  }
  
  .div-form {
    padding: 15px 10px;
  }
  
  .form_title {
    font-size: 1.2rem;
  }
}
</style>
