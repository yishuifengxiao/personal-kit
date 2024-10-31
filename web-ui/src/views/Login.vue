<template>
  <div class="login" :style="{ backgroundImage: 'url(' + backgroundImage + ')' }">
    <div class="login-content">
      <img :src="loginFormImage" class="loginFormImage" />
      <div class="div-form">
        <div class="form_title">欢迎登录系统</div>
        <a-form
          class="form"
          :model="formState"
          name="basic"
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 16 }"
          autocomplete="off"
          @finish="onFinish"
          @finishFailed="onFinishFailed"
        >
          <a-form-item
            label="账号"
            name="username"
            class="username"
            :rules="[{ required: true, message: '账号不能为空' }]"
          >
            <a-input name="username" v-model:value="formState.username" size="large" allowClear />
          </a-form-item>

          <a-form-item
            label="密码"
            name="password"
            class="password"
            :rules="[{ required: true, message: '密码不能为空' }]"
          >
            <a-input-password
              name="password"
              v-model:value="formState.password"
              size="large"
              allowClear
            />
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
          this.setToken(res.token)
          this.setUser(res)
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

  width: 100vw !important;
  height: 100vh !important;
  max-width: 100vw !important;
  max-height: 100vh !important;
}

.login-content {
  width: 100vw !important;
  height: 100vh !important;
  max-width: 100vw !important;
  max-height: 100vh !important;
  padding-top: 15vh;
  display: inline-flex;
}

.loginFormImage {
  display: inline-block;
  width: 30vw;
  height: 70vh;
  margin-left: 20vw;
  border-top-left-radius: 2rem;
  border-bottom-left-radius: 2rem;
}

.div-form {
  display: inline-block;
  width: 30vw;
  height: 70vh;
  background-color: white;
  border-top-right-radius: 2rem;
  border-bottom-right-radius: 2rem;
  vertical-align: middle !important;
}

.form_title {
  margin-top: 40px;
  margin-bottom: 90px;
  font-size: 2rem;
  font-weight: 700;
  color: #333;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.form_tail div {
  display: inline-block;
}

.form_tail_right {
  float: right;
}
</style>
