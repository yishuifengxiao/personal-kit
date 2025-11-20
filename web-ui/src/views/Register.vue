<template>
  <div class="register" :style="{ backgroundImage: 'url(' + backgroundImage + ')' }">
    <div class="register-content">
      <div class="register-container">
        <img :src="registerFormImage" class="registerFormImage" />
        <div class="div-form">
          <div class="form_title">欢迎注册系统</div>
          <a-form class="form" :model="formState" name="basic" :label-col="{ span: 4 }" :wrapper-col="{ span: 16 }"
            autocomplete="off" @finish="onFinish" @finishFailed="onFinishFailed">
            <a-form-item label="账号" name="username" class="username" :rules="[{ required: true, message: '账号不能为空' }]">
              <a-input name="username" v-model:value="formState.username" size="large" allowClear />
            </a-form-item>

            <a-form-item label="邮箱" name="email" class="email" :rules="[{ required: true, message: '邮箱不能为空' }, { type: 'email', message: '请输入有效的邮箱地址' }]">
              <a-input name="email" v-model:value="formState.email" size="large" allowClear />
            </a-form-item>

            <a-form-item label="密码" name="password" class="password" :rules="[{ required: true, message: '密码不能为空' }]">
              <a-input-password name="password" v-model:value="formState.password" size="large" allowClear />
            </a-form-item>

            <a-form-item label="记住账号" name="remember">
              <a-checkbox name="remember" v-model:checked="formState.remember">是</a-checkbox>
            </a-form-item>

            <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
              <a-button block type="primary" html-type="submit" size="large">注册</a-button>
            </a-form-item>
          </a-form>
          <div class="form_tail">
            <div class="form_tail_left"><a-button type="link" @click="goToLogin">已有账号？立即登录</a-button></div>
            <div class="form_tail_right"><a-button type="link" @click="goToForgotPassword">忘记密码</a-button></div>
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
import registerFormImage from '@/assets/backgroup/login_form.png'
import ViewChoose from './login/ViewChoose.vue'

export default defineComponent({
  data() {
    return {
      backgroundImage,
      registerFormImage
    }
  },

  methods: {
    onFinish(values) {
      this.$http
        .request({
          url: '/personkit/register',
          data: values,
          method: 'post'
        })
        .then((res) => {
          this.$message.success('注册成功！')
          this.$router.push({ name: 'login' })
        })
        .catch((err) => {
          console.log(err)
          this.$message.error('注册失败，请重试')
        })
    },

    onFinishFailed(errorInfo) {
      console.log('Failed:', errorInfo)
    },

    goToLogin() {
      this.$router.push({ name: 'login' })
    },

    goToForgotPassword() {
      this.$router.push({ name: 'forgotPassword' })
    }
  },

  setup() {
    const formState = reactive({
      username: '',
      email: '',
      password: '',
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
.register {
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-content {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-container {
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

.registerFormImage {
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
  .register-container {
    width: 95%;
    height: 80vh;
    max-height: 600px;
  }
  
  .registerFormImage {
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
  .register-container {
    flex-direction: column;
    height: auto;
    min-height: 80vh;
    max-height: none;
    width: 90%;
    max-width: 400px;
  }
  
  .registerFormImage {
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
  .register-container {
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
  .register-container {
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