import {
  defineStore
} from 'pinia'

export const useUserStore = defineStore('useUserStore', {
  state: () => {
    return {
      token: '',
      currentPath: ''
    }
  },
  getters: {
    tokenVal: (state) => state.token,
    isLogin: (state) => typeof state.token != 'undefined' && state.token.trim().length > 0
  },
  actions: {
    setToken(payload) {
      this.token = payload
    },
    setCurrentFolder(val) {
      this.currentPath = val
    }
  },
  persist: true
})