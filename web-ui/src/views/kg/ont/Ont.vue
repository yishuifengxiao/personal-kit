<template>
	<div class="ont-container">
		<!-- 上部搜索条件区域 -->
		<div class="search-header">
			<a-form layout="inline" name="basic" autocomplete="off" :model="formState" @finish="handleFinish" class="search-form">
				<a-form-item label="名称" name="name">
					<a-input v-model:value="formState.name" placeholder="请输入本体名称" allowClear class="search-input"> </a-input>
				</a-form-item>
				<a-form-item>
					<a-button type="primary" html-type="submit" class="search-btn"> 搜索 </a-button>
				</a-form-item>
				<a-form-item>
					<a-button @click="resetForm" class="reset-btn"> 重置 </a-button>
				</a-form-item>
			</a-form>
			<a-button type="primary" @click="addOnt" class="add-btn-float"> 添加本体 </a-button>
		</div>
		<!-- 中间内容区域 -->
		<div class="content-area-compact">
			<a-row :gutter="[12, 12]" class="ont-grid-compact">
				<a-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-for="item in tableData" :key="item.id">
					<a-card hoverable class="ont-card-compact" @mouseenter="showDescription(item, $event)" @mouseleave="hideDescription">
						<template #cover>
							<div class="card-cover-compact">
								<img alt="本体封面" :src="card_bg_url" class="cover-image-compact" />
								<!-- 操作按钮覆盖层 -->
								<div class="card-actions-overlay">
									<a-button type="primary" size="small" @click="doEdit(item)" class="overlay-btn">
										<edit-outlined /> 编辑
									</a-button>
									<a-popconfirm
										title="确定要删除这个本体吗？"
										@confirm="doDelete(item)"
										okText="确定"
										cancelText="取消">
										<a-button type="primary" danger size="small" class="overlay-btn">
											<delete-outlined /> 删除
										</a-button>
									</a-popconfirm>
								</div>
							</div>
						</template>

						<a-card-meta class="card-meta-compact">
							<template #title>
								<div class="card-title-compact" :title="item.ontologyName">{{ item.ontologyName }}</div>
							</template>
							<template #description>
								<div class="card-description-compact">
									<div class="create-time-compact" :title="item.description">{{ formatTime(item.createTime) }}</div>
								</div>
							</template>
							<template #avatar>
								<div class="card-avatar-compact">
									<a-avatar :size="24" class="avatar-icon-compact">
										<span class="avatar-text-compact">{{ getInitials(item.ontologyName) }}</span>
									</a-avatar>
								</div>
							</template>
						</a-card-meta>
					</a-card>
				</a-col>
			</a-row>
		</div>

		<!-- 分页区 -->
		<div class="pagination-container-compact">
			<a-pagination 
				v-model:current="result.num" 
				:total="result.total" 
				:show-total="(total) => `共 ${total} 条数据`"
				@change="onPaginationChange"
				:size="'small'"
				class="pagination-compact" />
		</div>
		
		<!-- 悬停提示框 -->
		<div v-if="hoveredItem && hoveredItem.description" class="description-tooltip" :style="tooltipStyle">
			{{ hoveredItem.description }}
		</div>
	</div>
</template>

<script>
import { reactive, defineComponent } from 'vue'
import { EditOutlined, SettingOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import card_bg_url from '@/assets/images/graph/card_bg.png'

export default defineComponent({
  data() {
    const formState = reactive({
      name: ''
    })

    const result = reactive({})
    const hoveredItem = reactive({})
    const mousePosition = reactive({ x: 0, y: 0 })
    return { 
      formState, 
      card_bg_url, 
      result,
      hoveredItem,
      mousePosition
    }
  },
  computed: {
    tableData: function () {
      return this.result.data
    },
    tooltipStyle: function() {
      if (!this.mousePosition) return {}
      return {
        position: 'fixed',
        top: this.mousePosition.y + 10 + 'px',
        left: this.mousePosition.x + 10 + 'px',
        zIndex: 9999,
        backgroundColor: 'rgba(0, 0, 0, 0.8)',
        color: 'white',
        padding: '8px 12px',
        borderRadius: '4px',
        fontSize: '12px',
        maxWidth: '300px',
        wordBreak: 'break-all',
        boxShadow: '0 2px 8px rgba(0, 0, 0, 0.2)'
      }
    }
  },
  methods: {
    handleFinish(val) {
      this.query()
    },
    resetForm() {
      this.formState.name = ''
      this.query()
    },
    addOnt() {
      sessionStorage.setItem('ontId', '')
      this.$router.push({ name: 'ontology_detail', query: { isAdd: true } })
    },
    query() {
      this.$http
        .request({
          url: '/personkit/graph/ont/page',
          data: {
            num: this.result.num || 1,
            query: {
              name: this.formState.name
            },
            size: 10
          }
        })
        .then((res) => {
          this.result = reactive(res)
        })
        .catch((err) => console.log(err))
    },
    // 添加成功
    onAddOk() {
      this.query()
    },

    //分页导航栏发生变化
    onPaginationChange(page, pageSize) {
      this.pagination.current = page
      this.query()
    },
    //触发编辑操作
    doEdit(val) {
      const id = val.id
      sessionStorage.setItem('ontId', id)

      this.$router.push({ name: 'ontology_detail', query: { isAdd: false, id: id } })
    },
    /**
     * 显示本体描述
     */
    showDescription(item, event) {
      this.hoveredItem = item
      this.updateMousePosition(event)
    },
    /**
     * 隐藏本体描述
     */
    hideDescription() {
      this.hoveredItem = null
      this.mousePosition = { x: 0, y: 0 }
    },
    /**
     * 更新鼠标位置
     */
    updateMousePosition(event) {
      if (event) {
        this.mousePosition = { x: event.clientX, y: event.clientY }
      }
    },
    /**
     * 格式化时间
     */
    formatTime(timeStr) {
      if (!timeStr) return ''
      return timeStr.replace('T', ' ').substring(0, 16)
    },
    /**
     * 获取本体名称首字母
     */
    getInitials(name) {
      if (!name) return 'O'
      return name.charAt(0).toUpperCase()
    },
    //删除本体
    doDelete(item) {
      this.$http
        .request({
          url: '/personkit/graph/ont/delete',
          data: {
            id: item.id
          }
        })
        .then((res) => {
          message.success('操作成功')
          this.query();
        })
        .catch((err) => console.log(err))
    }
  },
  mounted() {
    console.log('-------------- ont')
    this.query()
  },
  components: {
    EditOutlined,
    SettingOutlined,
    DeleteOutlined
  },
  setup() {}
})
</script>

<style lang="less" scoped>
.ont-container {
  padding: 0;
  background: #f5f5f5;
  min-height: 100vh;
  overflow: hidden;
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 12px 16px;
  border-radius: 0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 0;
}

.search-form {
  background: transparent;
  padding: 0;
  border-radius: 0;
  box-shadow: none;
  margin-bottom: 0;
}

.search-input {
  width: 200px;
}

.search-btn {
  min-width: 80px;
}

.reset-btn {
  min-width: 80px;
}

.add-btn-float {
  min-width: 100px;
}

.content-area-compact {
  background: white;
  padding: 12px;
  border-radius: 0;
  box-shadow: none;
  min-height: calc(100vh - 140px);
  overflow-y: auto;
  overflow-x: hidden;
}

.ont-grid-compact {
  margin: 0 !important;
}

.ont-card-compact {
  width: 100%;
  height: 160px;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.2s ease;
  cursor: pointer;
  border: 1px solid #e8e8e8;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border-color: #1890ff;
  }
}

.card-cover-compact {
  height: 80px;
  overflow: hidden;
  position: relative;
}

.card-actions-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.2s ease;
  
  .ont-card-compact:hover & {
    opacity: 1;
  }
}

.overlay-btn {
  min-width: 60px;
  height: 28px;
  font-size: 12px;
  padding: 0 8px;
}

.cover-image-compact {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.2s ease;
  
  .ont-card-compact:hover & {
    transform: scale(1.03);
  }
}

.card-meta-compact {
  padding: 8px;
}

.card-title-compact {
  font-size: 13px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.card-description-compact {
  font-size: 12px;
  color: #8c8c8c;
  line-height: 1.3;
}

.create-time-compact {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-avatar-compact {
  display: flex;
  align-items: center;
}

.avatar-icon-compact {
  background: #1890ff;
  color: white;
  font-size: 12px;
  font-weight: 500;
}

.avatar-text-compact {
  font-size: 10px;
}

.pagination-container-compact {
  background: white;
  padding: 12px 16px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #f0f0f0;
}

.pagination-compant {
  margin: 0;
}

/* 滚动条样式优化 */
.content-area-compact::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.content-area-compact::-webkit-scrollbar-track {
  background: transparent;
}

.content-area-compact::-webkit-scrollbar-thumb {
  background: #d9d9d9;
  border-radius: 3px;
}

.content-area-compact::-webkit-scrollbar-thumb:hover {
  background: #bfbfbf;
}

/* 悬停提示框样式 */
.description-tooltip {
  position: fixed;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 12px;
  max-width: 300px;
  word-break: break-all;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  z-index: 9999;
  pointer-events: none;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .search-header {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .search-form {
    flex-direction: column;
  }
  
  .search-input {
    width: 100%;
  }
  
  .add-btn-float {
    width: 100%;
  }
}
</style>
