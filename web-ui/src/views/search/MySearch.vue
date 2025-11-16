<template>
  <div class="my-search">
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
                placeholder="请输入搜索关键词"
                size="large"
                allowClear
                @pressEnter="handleSearch"
                class="search-input"
              />
            </a-form-item>
            
            <a-form-item>
              <a-button 
                type="primary" 
                size="large" 
                html-type="submit"
                class="search-btn"
                :loading="searchLoading"
              >
                拾光一下
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
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue'
import { useRouter } from 'vue-router'
import { SearchOutlined } from '@ant-design/icons-vue'

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

    return {
      searchQuery,
      searchLoading,
      handleSearch,
      onSearch,
      onSearchFailed
    }
  }
})
</script>

<style scoped>
.my-search {
  width: 100vw;
  min-height: 100vh;
  background-color: #ffffff;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 80px 20px 20px;
  margin: 0;
  box-sizing: border-box;
}

.search-content {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  transform: translateY(-50px);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.search-container {
  text-align: center;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.search-header {
  margin-bottom: 40px;
}

.search-title {
  font-size: 3.5rem;
  font-weight: 700;
  color: #333333;
  margin-bottom: 10px;
  font-family: 'Microsoft YaHei', '微软雅黑', 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  letter-spacing: 1px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-subtitle {
  font-size: 1.2rem;
  color: #666666;
  margin-bottom: 0;
  font-weight: 400;
}

.search-form-container {
  margin-bottom: 80px;
}

.search-form {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0;
  width: 100%;
}

.search-input {
  width: 600px;
  height: 44px;
  border: 2px solid #c4c7ce;
  border-right: none;
  border-radius: 10px 0 0 10px;
  padding: 0 16px;
  font-size: 16px;
  background: #ffffff;
  transition: all 0.3s ease;
  outline: none;
  box-shadow: none;
  color: #222222;
  margin: 0 auto;
}

.search-input:focus {
  border-color: #4a90e2;
  box-shadow: none;
}

.search-input :deep(.ant-input) {
  border: none;
  box-shadow: none;
  outline: none;
  background: transparent;
  height: 100%;
  padding: 0;
  font-size: 16px;
  color: #222222;
}

.search-input :deep(.ant-input:focus) {
  box-shadow: none;
  border: none;
  outline: none;
}

.search-input :deep(.ant-input-affix-wrapper) {
  border: none;
  box-shadow: none;
  outline: none;
  background: transparent;
  padding: 0;
}

.search-input :deep(.ant-input-affix-wrapper:focus) {
  box-shadow: none;
  border: none;
  outline: none;
}

.search-btn {
  height: 44px;
  border-radius: 0 10px 10px 0;
  background: #4a90e2;
  border: 1px solid #4a90e2;
  border-left: none;
  padding: 0 32px;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  cursor: pointer;
  color: #ffffff;
  box-shadow: none;
}

.search-btn:hover {
  background: #357abd;
  box-shadow: none;
}

.search-features {
  display: flex;
  justify-content: center;
  gap: 60px;
  margin-top: 60px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.feature-icon {
  font-size: 2rem;
  margin-bottom: 5px;
}

.feature-text {
  font-size: 1rem;
  color: #666666;
  font-weight: 400;
}

@media (max-width: 768px) {
  .my-search {
    padding-top: 80px;
  }
  
  .search-title {
    font-size: 2.8rem;
  }
  
  .search-subtitle {
    font-size: 1rem;
  }
  
  .search-input {
    width: 400px;
  }
  
  .search-features {
    gap: 40px;
  }
}

@media (max-width: 480px) {
  .my-search {
    padding-top: 60px;
  }
  
  .search-title {
    font-size: 2.2rem;
  }
  
  .search-subtitle {
    font-size: 0.9rem;
  }
  
  .search-input {
    width: 250px;
  }
  
  .search-form {
    flex-direction: column;
    gap: 15px;
  }
  
  .search-input {
    border-radius: 10px;
    border: 2px solid #c4c7ce;
    border-right: 2px solid #c4c7ce;
    width: 100%;
    max-width: 300px;
  }
  
  .search-btn {
    border-radius: 10px;
    width: 120px;
    border: 1px solid #4a90e2;
    border-left: 1px solid #4a90e2;
  }
  
  .search-features {
    flex-direction: column;
    gap: 30px;
  }
}
</style>