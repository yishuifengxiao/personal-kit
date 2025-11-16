<template>
  <div class="search-results">
    <div class="results-content">
      <div class="results-header">
        <div class="search-bar-container">
          <div class="logo-section">
            <h2 class="logo" @click="goToSearch">拾光搜索</h2>
          </div>
          <div class="search-bar">
            <a-input 
              v-model:value="searchQuery" 
              placeholder="请输入搜索关键词"
              size="large"
              allowClear
              @pressEnter="handleSearch"
              class="search-input"
            >
              <template #prefix>
                <SearchOutlined />
              </template>
            </a-input>
            <a-button 
              type="primary" 
              size="large"
              @click="handleSearch"
              class="search-btn"
              :loading="searchLoading"
            >
              拾光一下
            </a-button>
          </div>
        </div>
      </div>

      <div class="results-container">
        <div class="results-stats">
          <span>找到约 {{ totalResults }} 条结果</span>
          <span class="search-time">（用时 {{ searchTime }} 秒）</span>
        </div>

        <div class="results-list">
          <div 
            v-for="result in searchResults" 
            :key="result.id"
            class="result-item"
            @click="openResult(result.url)"
          >
            <div class="result-title">
              <a-icon type="link" class="result-icon" />
              {{ result.title }}
            </div>
            <div class="result-url">{{ result.url }}</div>
            <div class="result-description">{{ result.description }}</div>
            <div class="result-meta">
              <span class="result-date">{{ result.date }}</span>
              <span class="result-category">{{ result.category }}</span>
            </div>
          </div>
        </div>

        <div class="results-pagination">
          <a-pagination
            v-model:current="currentPage"
            v-model:pageSize="pageSize"
            :total="totalResults"
            :showSizeChanger="true"
            :showQuickJumper="true"
            :showTotal="total => `共 ${total} 条结果`"
            @change="handlePageChange"
          />
        </div>
      </div>

      <div class="no-results" v-if="searchResults.length === 0 && !searchLoading">
        <div class="no-results-icon">🔍</div>
        <div class="no-results-text">未找到相关结果</div>
        <div class="no-results-suggestion">建议：检查关键词拼写或尝试其他关键词</div>
      </div>

      <div class="loading-container" v-if="searchLoading">
        <a-spin size="large" tip="正在搜索中..." />
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { SearchOutlined } from '@ant-design/icons-vue'

export default defineComponent({
  name: 'SearchResults',
  components: {
    SearchOutlined
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const searchQuery = ref('')
    const searchLoading = ref(false)
    const searchResults = ref([])
    const totalResults = ref(0)
    const searchTime = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)

    // 模拟搜索结果数据
    const mockResults = [
      {
        id: 1,
        title: 'Vue.js - 渐进式 JavaScript 框架',
        url: 'https://cn.vuejs.org/',
        description: 'Vue.js是一套用于构建用户界面的渐进式框架。与其他大型框架不同的是，Vue被设计为可以自底向上逐层应用。',
        date: '2024-01-15',
        category: '技术文档'
      },
      {
        id: 2,
        title: 'React 官方中文文档 - 用于构建用户界面的 JavaScript 库',
        url: 'https://zh-hans.react.dev/',
        description: 'React是一个用于构建用户界面的JavaScript库。React主要用于构建UI，很多人认为React是MVC中的V（视图）。',
        date: '2024-01-14',
        category: '技术文档'
      },
      {
        id: 3,
        title: 'Ant Design Vue - 企业级UI组件库',
        url: 'https://www.antdv.com/',
        description: 'Ant Design Vue是Ant Design的Vue版本，提供了一套企业级的UI设计语言和React组件库。',
        date: '2024-01-13',
        category: 'UI组件'
      },
      {
        id: 4,
        title: 'Vite - 下一代前端工具链',
        url: 'https://cn.vitejs.dev/',
        description: 'Vite是一种新型前端构建工具，能够显著提升前端开发体验。它利用浏览器原生的ES模块支持。',
        date: '2024-01-12',
        category: '开发工具'
      },
      {
        id: 5,
        title: 'TypeScript 官方文档',
        url: 'https://www.tslang.cn/',
        description: 'TypeScript是JavaScript的超集，可以编译成纯JavaScript。TypeScript为JavaScript添加了类型系统。',
        date: '2024-01-11',
        category: '编程语言'
      },
      {
        id: 6,
        title: 'Node.js 官方网站',
        url: 'https://nodejs.org/zh-cn/',
        description: 'Node.js是一个基于Chrome V8引擎的JavaScript运行时环境，让JavaScript可以运行在服务器端。',
        date: '2024-01-10',
        category: '运行时环境'
      },
      {
        id: 7,
        title: 'Webpack - 模块打包工具',
        url: 'https://webpack.js.org/',
        description: 'Webpack是一个现代JavaScript应用程序的静态模块打包器，它将应用程序的依赖关系图构建。',
        date: '2024-01-09',
        category: '构建工具'
      },
      {
        id: 8,
        title: 'Git - 分布式版本控制系统',
        url: 'https://git-scm.com/',
        description: 'Git是一个免费的开源分布式版本控制系统，旨在快速高效地处理从小型到大型项目的所有内容。',
        date: '2024-01-08',
        category: '版本控制'
      }
    ]

    const performSearch = () => {
      if (!searchQuery.value.trim()) {
        searchResults.value = []
        totalResults.value = 0
        return
      }

      searchLoading.value = true
      const startTime = Date.now()

      // 模拟搜索延迟
      setTimeout(() => {
        const query = searchQuery.value.toLowerCase().trim()
        
        // 过滤搜索结果
        const filteredResults = mockResults.filter(result => 
          result.title.toLowerCase().includes(query) ||
          result.description.toLowerCase().includes(query) ||
          result.category.toLowerCase().includes(query)
        )

        // 分页处理
        const startIndex = (currentPage.value - 1) * pageSize.value
        const endIndex = startIndex + pageSize.value
        searchResults.value = filteredResults.slice(startIndex, endIndex)
        totalResults.value = filteredResults.length
        
        // 计算搜索时间
        searchTime.value = ((Date.now() - startTime) / 1000).toFixed(2)
        searchLoading.value = false
      }, 800)
    }

    const handleSearch = () => {
      if (!searchQuery.value.trim()) {
        return
      }
      
      // 更新URL参数
      router.push({
        name: 'search_results',
        query: { q: searchQuery.value.trim(), page: 1 }
      })
    }

    const handlePageChange = (page, pageSize) => {
      currentPage.value = page
      router.push({
        name: 'search_results',
        query: { q: searchQuery.value.trim(), page: page }
      })
      performSearch()
    }

    const openResult = (url) => {
      window.open(url, '_blank')
    }

    const goToSearch = () => {
      router.push({ name: 'my_search' })
    }

    onMounted(() => {
      // 从URL参数获取搜索词
      if (route.query.q) {
        searchQuery.value = route.query.q
        performSearch()
      }
    })

    watch(() => route.query.q, (newQuery) => {
      if (newQuery) {
        searchQuery.value = newQuery
        currentPage.value = parseInt(route.query.page) || 1
        performSearch()
      }
    })

    return {
      searchQuery,
      searchLoading,
      searchResults,
      totalResults,
      searchTime,
      currentPage,
      pageSize,
      handleSearch,
      handlePageChange,
      openResult,
      goToSearch
    }
  }
})
</script>

<style scoped>
.search-results {
  min-height: 100vh;
  background: #ffffff;
}

.results-content {
  min-height: 100vh;
  background: #ffffff;
}

.results-header {
  background: white;
  border-bottom: 1px solid #f0f0f0;
  padding: 20px 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.search-bar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 30px;
}

.logo-section {
  flex-shrink: 0;
}

.logo {
  font-size: 1.8rem;
  font-weight: 700;
  color: #1890ff;
  margin: 0;
  cursor: pointer;
  transition: color 0.3s ease;
}

.logo:hover {
  color: #40a9ff;
}

.search-bar {
  flex: 1;
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  flex: 1;
}

.search-input :deep(.ant-input) {
  border-radius: 10px;
  padding-left: 45px;
  padding-right: 20px;
  font-size: 16px;
  border: 2px solid #e0e0e0;
  transition: all 0.3s ease;
  height: 50px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.search-input :deep(.ant-input:focus) {
  border-color: #1890ff;
  box-shadow: 0 2px 15px rgba(24, 144, 255, 0.3);
  outline: none;
}

.search-input :deep(.ant-input-prefix) {
  color: #999;
  font-size: 18px;
  left: 15px;
}

.search-btn {
  border-radius: 10px;
  padding: 0 30px;
  font-weight: 500;
  height: 50px;
  background: #1890ff;
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 2px 10px rgba(24, 144, 255, 0.3);
}

.search-btn:hover {
  background: #40a9ff;
  box-shadow: 0 4px 15px rgba(24, 144, 255, 0.4);
  transform: translateY(-1px);
}

.results-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 20px;
}

.results-stats {
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
}

.search-time {
  color: #999;
  margin-left: 10px;
}

.results-list {
  margin-bottom: 40px;
}

.result-item {
  background: white;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.result-item:hover {
  background: #fafafa;
  border-color: #e0e0e0;
}

.result-title {
  font-size: 18px;
  font-weight: 400;
  color: #1a0dab;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.result-icon {
  font-size: 14px;
  color: #006621;
}

.result-url {
  font-size: 14px;
  color: #006621;
  margin-bottom: 8px;
  word-break: break-all;
}

.result-description {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  margin-bottom: 12px;
}

.result-meta {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #999;
}

.result-date,
.result-category {
  display: flex;
  align-items: center;
  gap: 4px;
}

.results-pagination {
  text-align: center;
  margin-top: 40px;
}

.no-results {
  text-align: center;
  padding: 80px 20px;
}

.no-results-icon {
  font-size: 4rem;
  margin-bottom: 20px;
  opacity: 0.6;
}

.no-results-text {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 10px;
}

.no-results-suggestion {
  font-size: 14px;
  color: #999;
}

.loading-container {
  text-align: center;
  padding: 80px 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-bar-container {
    flex-direction: column;
    gap: 20px;
    padding: 0 15px;
  }
  
  .search-bar {
    width: 100%;
  }
  
  .results-container {
    padding: 20px 15px;
  }
  
  .result-item {
    padding: 15px;
  }
  
  .result-title {
    font-size: 16px;
  }
  
  .result-meta {
    flex-direction: column;
    gap: 8px;
  }
}

@media (max-width: 480px) {
  .logo {
    font-size: 1.5rem;
  }
  
  .results-stats {
    font-size: 12px;
  }
  
  .result-description {
    font-size: 13px;
  }
}
</style>