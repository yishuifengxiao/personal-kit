<template>
  <div class="search-results">
    <div class="results-content">
      <div class="results-header">
        <div class="search-bar-container">
          <div class="logo-section">
            <h2 class="logo" @click="goToSearch">拾光搜索</h2>
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
            :showSizeChanger="false"
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
        title: 'Vue.js - 渐进式 JavaScript 框架 | 官方中文文档',
        url: 'https://cn.vuejs.org/',
        description: 'Vue.js是一套用于构建用户界面的渐进式框架。与其他大型框架不同的是，Vue被设计为可以自底向上逐层应用。Vue的核心库只关注视图层，不仅易于上手，还便于与第三方库或既有项目整合。',
        date: '2024-01-15',
        category: '技术文档'
      },
      {
        id: 2,
        title: 'React 官方中文文档 - 用于构建用户界面的 JavaScript 库',
        url: 'https://zh-hans.react.dev/',
        description: 'React是一个用于构建用户界面的JavaScript库。React主要用于构建UI，很多人认为React是MVC中的V（视图）。React起源于Facebook的内部项目，用来架设Instagram的网站，并于2013年5月开源。',
        date: '2024-01-14',
        category: '技术文档'
      },
      {
        id: 3,
        title: 'Ant Design Vue - 企业级UI设计语言和Vue组件库',
        url: 'https://www.antdv.com/',
        description: 'Ant Design Vue是Ant Design的Vue版本，提供了一套企业级的UI设计语言和React组件库。Ant Design Vue致力于提升用户和设计者的使用体验，让设计和开发更专注于更好的用户体验。',
        date: '2024-01-13',
        category: 'UI组件'
      },
      {
        id: 4,
        title: 'Vite - 下一代前端工具链 | 官方中文文档',
        url: 'https://cn.vitejs.dev/',
        description: 'Vite是一种新型前端构建工具，能够显著提升前端开发体验。它利用浏览器原生的ES模块支持，提供了极快的冷启动、即时热模块更新（HMR）和真正的按需编译。',
        date: '2024-01-12',
        category: '开发工具'
      },
      {
        id: 5,
        title: 'TypeScript 官方文档 - JavaScript的超集',
        url: 'https://www.tslang.cn/',
        description: 'TypeScript是JavaScript的超集，可以编译成纯JavaScript。TypeScript为JavaScript添加了类型系统，可以在任何浏览器、任何计算机和任何操作系统上运行，并且是开源的。',
        date: '2024-01-11',
        category: '编程语言'
      },
      {
        id: 6,
        title: 'Node.js 官方网站 - JavaScript运行时环境',
        url: 'https://nodejs.org/zh-cn/',
        description: 'Node.js是一个基于Chrome V8引擎的JavaScript运行时环境，让JavaScript可以运行在服务器端。Node.js使用了一个事件驱动、非阻塞式I/O的模型，使其轻量又高效。',
        date: '2024-01-10',
        category: '运行时环境'
      },
      {
        id: 7,
        title: 'Webpack - 现代JavaScript应用程序的静态模块打包器',
        url: 'https://webpack.js.org/',
        description: 'Webpack是一个现代JavaScript应用程序的静态模块打包器。当webpack处理应用程序时，它会递归地构建一个依赖关系图，其中包含应用程序需要的每个模块，然后将所有这些模块打包成一个或多个bundle。',
        date: '2024-01-09',
        category: '构建工具'
      },
      {
        id: 8,
        title: 'Git - 分布式版本控制系统的完整指南',
        url: 'https://git-scm.com/',
        description: 'Git是一个免费的开源分布式版本控制系统，旨在快速高效地处理从小型到大型项目的所有内容。Git易于学习，占用空间小，性能极快，比Subversion、CVS、Perforce和ClearCase等SCM工具具有更好的性能。',
        date: '2024-01-08',
        category: '版本控制'
      },
      {
        id: 9,
        title: 'ESLint - 可组装的JavaScript和JSX检查工具',
        url: 'https://eslint.org/',
        description: 'ESLint是一个可组装的JavaScript和JSX检查工具。ESLint完全可配置，可以自定义规则，并且提供了丰富的插件生态系统。它可以帮助你发现代码中的问题，并保持代码风格的一致性。',
        date: '2024-01-07',
        category: '代码质量'
      },
      {
        id: 10,
        title: 'Prettier - 固执的代码格式化工具',
        url: 'https://prettier.io/',
        description: 'Prettier是一个固执的代码格式化工具。它通过解析你的代码并重新打印它，用自己的规则来考虑最大行长，必要时换行代码，从而强制执行一致的代码风格。支持多种语言，包括JavaScript、TypeScript、CSS、HTML等。',
        date: '2024-01-06',
        category: '代码格式化'
      },
      {
        id: 11,
        title: 'MongoDB - 最受欢迎的NoSQL数据库',
        url: 'https://www.mongodb.com/',
        description: 'MongoDB是一个基于分布式文件存储的数据库。由C++语言编写。旨在为WEB应用提供可扩展的高性能数据存储解决方案。MongoDB是一个介于关系数据库和非关系数据库之间的产品。',
        date: '2024-01-05',
        category: '数据库'
      },
      {
        id: 12,
        title: 'Docker - 容器化平台与工具',
        url: 'https://www.docker.com/',
        description: 'Docker是一个开源的应用容器引擎，让开发者可以打包他们的应用以及依赖包到一个可移植的容器中，然后发布到任何流行的Linux机器上，也可以实现虚拟化。容器是完全使用沙箱机制，相互之间不会有任何接口。',
        date: '2024-01-04',
        category: '容器化'
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

    const onSearch = (values) => {
      handleSearch()
    }

    const onSearchFailed = (errorInfo) => {
      console.log('Search failed:', errorInfo)
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
      goToSearch,
      onSearch,
      onSearchFailed
    }
  }
})
</script>

<style scoped>
.search-results {
  min-height: 100vh;
  background: #ffffff;
  position: relative;
  padding-bottom: 100px;
}

.results-content {
  max-width: 1200px;
  margin: 0 auto;
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
  gap: 20px;
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
  height: 44px;
  line-height: 44px;
}

.logo:hover {
  color: #40a9ff;
}

.search-form-container {
  flex: 1;
  margin-bottom: 0;
}

.search-form {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 0;
  width: 100%;
  max-width: 700px;
}

.search-form .ant-form-item {
  margin-bottom: 0;
}

.search-input {
  width: 500px;
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

.results-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 20px 80px;
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
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.result-item:hover {
  background: #fafafa;
  border-color: #e0e0e0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.result-title {
  font-size: 20px;
  font-weight: 500;
  color: #4a90e2;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: color 0.3s ease;
}

.result-item:hover .result-title {
  color: #357abd;
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
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: transparent;
  padding: 0;
  border-radius: 0;
  box-shadow: none;
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
  
  .search-form {
    width: 100%;
    max-width: 600px;
  }
  
  .search-input {
    width: 100%;
    max-width: 600px;
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
  
  .logo {
    height: auto;
    line-height: normal;
  }
}

@media (max-width: 480px) {
  .search-bar-container {
    flex-direction: column;
    gap: 15px;
  }
  
  .logo {
    font-size: 1.5rem;
    height: auto;
    line-height: normal;
  }
  
  .search-content {
    top: 60px;
  }
  
  .search-title {
    font-size: 2.2rem;
  }
  
  .search-subtitle {
    font-size: 0.9rem;
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
  
  .results-stats {
    font-size: 12px;
  }
  
  .result-description {
    font-size: 13px;
  }
}
</style>