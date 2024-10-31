import { defineStore } from 'pinia'

export const useUserStore = defineStore('useUserStore', {
  state: () => {
    return {
      token: '',
      userId: '',
      currentPath: '',
      role: { id: '' },
      user: {},
      topMenuId: '',
      leftMenuId: ''
    }
  },
  getters: {
    tokenVal: (state) => state.token,
    isLogin: (state) => typeof state.token != 'undefined' && state.token.trim().length > 0,
    currentUserId: (state) => state.userId,
    currentRoleId: (state) => state.role.id,
    hasRole: (state) => null !== state.role.id && typeof state.role.id !== 'undefined',
    currentTopMenuId: (state) => state.topMenuId,
    currentLeftMenuId: (state) => state.leftMenuId
  },
  actions: {
    setToken(payload) {
      this.token = payload
    },

    setUserId(userId) {
      this.userId = userId
    },
    setCurrentFolder(val) {
      this.currentPath = val
    },
    setRole(val) {
      this.role = val
    },
    setUser(val) {
      this.user = val
      this.userId = val.id
    },
    setTopMenuId(topMenuId) {
      this.topMenuId = topMenuId
    },
    setLeftMenuId(leftMenuId) {
      this.leftMenuId = leftMenuId
    }
  },
  persist: true
})
