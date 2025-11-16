<template>
  <div class="my-search" :style="{ backgroundImage: 'url(' + backgroundImage + ')' }">
    <div class="search-content">
      <div class="search-container">
        <div class="search-header">
          <h1 class="search-title">拾光搜索</h1>
          <p class="search-subtitle">发现精彩内容，搜索无限可能</p>
        </div>
        
        <div class="search-form-container">
          <a-form class="search-form" @finish="onSearch" @finishFailed="onSearchFailed">
            <a-form-item name="searchQuery" :rules="[{ required: true, message: '请输入搜索关键词' }]">
              <a-input 
                v-model:value="searchQuery" 
                placeholder="请输入搜索关键词..."
                size="large"
                allowClear
                @pressEnter="handleSearch"
                class="search-input"
              >
                <template #prefix>
                  <SearchOutlined />
                </template>
              </a-input>
            </a-form-item>
            
            <a-form-item>
              <a-button 
                type="primary" 
                size="large" 
                html-type="submit"
                class="search-btn"
                :loading="searchLoading"
              >
                <SearchOutlined />
                搜索
              </a-button>
            </a-form-item>
          </a-form>
        </div>

        <div class="search-features">
          <div class="feature-item">
            <div class="feature-icon">🔍</div>
            <div class="feature-text">智能搜索</div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">⚡</div>
            <div class="feature-text">快速响应</div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">🎯</div>
            <div class="feature-text">精准匹配</div>
          </div>
        </div>

        <div class="search-footer">
          <a-button type="link" @click="goToHome">返回首页</a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue'
import { useRouter } from 'vue-router'
import { SearchOutlined } from '@ant-design/icons-vue'
import backgroundImage from '@/assets/backgroup/login-bg.png'

export default defineComponent({
  name: 'MySearch',
  components: {
    SearchOutlined
  },
  setup() {
    const router = useRouter()
    const searchQuery = ref('')
    const searchLoading = ref(false)

    const handleSearch = () => {
      if (!searchQuery.value.trim()) {
        return
      }
      
      searchLoading.value = true
      
      // 模拟搜索延迟
      setTimeout(() => {
        searchLoading.value = false
        router.push({
          name: 'search_results',
          query: { q: searchQuery.value.trim() }
        })
      }, 500)
    }

    const onSearch = (values) => {
      handleSearch()
    }

    const onSearchFailed = (errorInfo) => {
      console.log('Search failed:', errorInfo)
    }

    const goToHome = () => {
      router.push({ name: 'index' })
    }

    return {
      backgroundImage,
      searchQuery,
      searchLoading,
      handleSearch,
      onSearch,
      onSearchFailed,
      goToHome
    }
  }
})
</script>

<style scoped>
.my-search {
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-content {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.3);
}

.search-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  max-width: 800px;
  width: 90%;
  padding: 60px 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 2rem;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
}

.search-header {
  text-align: center;
  margin-bottom: 40px;
}

.search-title {
  font-size: 3rem;
  font-weight: 700;
  color: #1890ff;
  margin-bottom: 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.search-subtitle {
  font-size: 1.2rem;
  color: #666;
  margin: 0;
}

.search-form-container {
  width: 100%;
  max-width: 600px;
  margin-bottom: 40px;
}

.search-form {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.search-input {
  flex: 1;
}

.search-input :deep(.ant-input) {
  border-radius: 25px;
  padding-left: 45px;
  padding-right: 20px;
  font-size: 16px;
  border: 2px solid #e8e8e8;
  transition: all 0.3s ease;
}

.search-input :deep(.ant-input:focus) {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.search-input :deep(.ant-input-prefix) {
  color: #1890ff;
  font-size: 18px;
  left: 15px;
}

.search-btn {
  border-radius: 25px;
  padding: 0 30px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(24, 144, 255, 0.4);
}

.search-features {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 40px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.feature-icon {
  font-size: 2rem;
  margin-bottom: 8px;
  animation: bounce 2s infinite;
}

.feature-text {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.search-footer {
  text-align: center;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-container {
    padding: 40px 20px;
    margin: 20px;
  }
  
  .search-title {
    font-size: 2.5rem;
  }
  
  .search-subtitle {
    font-size: 1rem;
  }
  
  .search-form {
    flex-direction: column;
    gap: 12px;
  }
  
  .search-btn {
    width: 100%;
  }
  
  .search-features {
    gap: 20px;
  }
  
  .feature-icon {
    font-size: 1.5rem;
  }
}

@media (max-width: 480px) {
  .search-container {
    padding: 30px 15px;
  }
  
  .search-title {
    font-size: 2rem;
  }
  
  .search-features {
    flex-direction: column;
    gap: 15px;
  }
}
</style>