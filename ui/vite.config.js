import {
  fileURLToPath,
  URL
} from 'node:url'

import {
  defineConfig
} from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src',
        import.meta.url))
    }
  },
  server: {
    port: 3000,
    //设置 server.hmr.overlay 为 false 可以禁用开发服务器错误的屏蔽
    hmr: {
      overlay: false
    }
  },
})