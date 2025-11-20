<template>
  <div class="forgot-password" :style="{ backgroundImage: 'url(' + backgroundImage + ')' }">
    <div class="forgot-password-content">
      <div class="forgot-password-container">
        <img :src="forgotPasswordFormImage" class="forgotPasswordFormImage" />
        <div class="div-form">
          <div class="form_title">忘记密码</div>
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

            <a-form-item label="验证码" name="verificationCode" class="verification-code" :rules="[{ required: true, message: '请输入验证码' }]">
              <a-row :gutter="8">
                <a-col :span="14">
                  <a-input name="verificationCode" v-model:value="formState.verificationCode" size="large" allowClear />
                </a-col>
                <a-col :span="10">
                  <a-button type="primary" size="large" @click="sendVerificationCode" :disabled="countdown > 0">
                    {{ countdown > 0 ? `${countdown}秒后重试` : '发送验证码' }}
                  </a-button>
                </a-col>
              </a-row>
            </a-form-item>

            <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
              <a-button block type="primary" html-type="submit" size="large">重置密码</a-button>
            </a-form-item>
          </a-form>
          <div class="form_tail">
            <div class="form_tail_left"><a-button type="link" @click="goToLogin">返回登录</a-button></div>
            <div class="form_tail_right"><a-button type="link" @click="goToRegister">注册账号</a-button></div>
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
import forgotPasswordFormImage from '@/assets/backgroup/login_form.png'
import ViewChoose from './login/ViewChoose.vue'

export default defineComponent({
  data() {
    return {
      backgroundImage,
      forgotPasswordFormImage,
      countdown: 0
    }
  },

  methods: {
    onFinish(values) {
      this.$http
        .request({
          url: '/personkit/reset-password',
          data: values,
          method: 'post'
        })
        .then((res) => {
          this.$message.success('密码重置成功！')
          this.$router.push({ name: 'login' })
        })
        .catch((err) => {
          console.log(err)
          this.$message.error('密码重置失败，请重试')
        })
    },

    onFinishFailed(errorInfo) {
      console.log('Failed:', errorInfo)
    },



    sendVerificationCode() {
      if (!this.formState.email) {
        this.$message.warning('请先输入邮箱地址')
        return
      }
      
      // 模拟发送验证码
      this.countdown = 60
      const timer = setInterval(() => {
        this.countdown--
        if (this.countdown <= 0) {
          clearInterval(timer)
        }
      }, 1000)
      
      this.$message.success('验证码已发送到您的邮箱')
    },

    goToLogin() {
      this.$router.push({ name: 'login' })
    },

    goToRegister() {
      this.$router.push({ name: 'register' })
    }
  },

  setup() {
    const formState = reactive({
      username: '',
      email: '',
      password: '',
      verificationCode: ''
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
.forgot-password {
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.forgot-password-content {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.forgot-password-container {
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

.forgotPasswordFormImage {
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
    .forgot-password-container {
      width: 95%;
      height: 80vh;
      max-height: 600px;
    }
  
  .forgotPasswordFormImage {
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
  .forgot-password-container {
    flex-direction: column;
    height: auto;
    min-height: 80vh;
    max-height: none;
    width: 90%;
    max-width: 400px;
  }
  
  .forgotPasswordFormImage {
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
  .forgot-password-container {
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
  .forgot-password-container {
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