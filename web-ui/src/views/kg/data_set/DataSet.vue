<template>
	<div class="dataset-container">
		<!-- 上部搜索条件区域 -->
		<div class="search-header">
			<a-form layout="inline" name="basic" autocomplete="off" :model="formState" @finish="handleFinish" class="search-form">
				<a-form-item label="名称" name="name">
					<a-input v-model:value="formState.name" placeholder="请输入数据集名称" allowClear class="search-input"> </a-input>
				</a-form-item>
				<a-form-item label="创建时间" name="dateRange">
					<a-range-picker 
						v-model:value="formState.dateRange" 
						allowClear 
						:placeholder="['开始时间', '结束时间']"
						format="YYYY-MM-DD"
						class="date-range-input"
					/>
				</a-form-item>
				<a-form-item>
					<a-button type="primary" html-type="submit" class="search-btn"> 搜索 </a-button>
				</a-form-item>
				<a-form-item>
					<a-button @click="resetForm" class="reset-btn"> 重置 </a-button>
				</a-form-item>
			</a-form>
			<a-button type="primary" @click="addDataSet" class="add-btn-float"> 添加数据集 </a-button>
		</div>
		<!-- 中间内容区域 -->
		<div class="content-area-compact">
			<a-row :gutter="[12, 12]" class="dataset-grid-compact">
				<a-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-for="item in tableData" :key="item.id">
					<a-card hoverable class="dataset-card-compact" @mouseenter="showDescription(item, $event)" @mouseleave="hideDescription">
						<template #cover>
							<div class="card-cover-compact">
								<img alt="数据集封面" :src="card_bg_url" class="cover-image-compact" />
								<!-- 操作按钮覆盖层 -->
								<div class="card-actions-overlay">
									<a-button type="primary" size="small" @click="doEdit(item)" class="overlay-btn">
										<edit-outlined /> 编辑
									</a-button>
									<a-popconfirm
										title="确定要删除这个数据集吗？"
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
								<div class="card-title-compact" :title="item.name">{{ item.name }}</div>
							</template>
							<template #description>
								<div class="card-description-compact">
									<div class="create-time-compact" :title="item.description">{{ formatTime(item.createTime) }}</div>
								</div>
							</template>
							<template #avatar>
								<div class="card-avatar-compact">
									<a-avatar :size="24" class="avatar-icon-compact">
										<span class="avatar-text-compact">{{ getInitials(item.name) }}</span>
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
		<!-- 弹窗区域 -->
		<ConfigurePopup ref="ConfigurePopup" @ok="onAddOk"></ConfigurePopup>
		
		<!-- 悬停提示框 -->
		<div v-if="hoveredItem && hoveredItem.description" class="description-tooltip" :style="tooltipStyle">
			{{ hoveredItem.description }}
		</div>
	</div>
</template>

<script>
	import {
		reactive,
		defineComponent
	} from 'vue'
	import {
		EditOutlined,
		SettingOutlined,
		DeleteOutlined
	} from '@ant-design/icons-vue'
	import { message } from 'ant-design-vue'
	import { RangePicker } from 'ant-design-vue'

	import card_bg_url from '@/assets/images/graph/card_bg.png'
	import ConfigurePopup from './ConfigurePopup.vue'
	export default defineComponent({
		data() {
			const formState = reactive({
				name: '',
				dateRange: []
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
			tableData: function() {
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
				this.formState.dateRange = []
				this.query()
			},
			addDataSet() {
				this.$refs.ConfigurePopup.showDrawer()
			},
			query() {
				const queryData = {
					name: this.formState.name
				}
				
				// 添加时间范围筛选
				if (this.formState.dateRange && this.formState.dateRange.length === 2) {
					queryData.startTime = this.formState.dateRange[0].format('YYYY-MM-DD') + ' 00:00:00'
					queryData.endTime = this.formState.dateRange[1].format('YYYY-MM-DD') + ' 23:59:59'
				}
				
				this.$http
					.request({
						url: '/personkit/data/dataSet/page',
						data: {
							num: this.result.num || 1,
							query: queryData,
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
			this.$refs.ConfigurePopup.showDrawer(val)
		},
		/**
		 * 显示数据集描述
		 */
		showDescription(item) {
			this.hoveredItem = item
			this.updateMousePosition(event)
		},
		/**
		 * 隐藏数据集描述
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
		 * 获取数据集名称首字母
		 */
		getInitials(name) {
			if (!name) return 'D'
			return name.charAt(0).toUpperCase()
		},
			doDelete(val) {
				this.$http
					.request({
						url: '/personkit/data/dataSet/delete',
						data: {
							id: val.id
						}
					})
					.then((res) => {
						message.success('删除成功')
						this.query()
					})
					.catch((err) => console.log(err))
			}
		},
		mounted() {
			this.query()
		},
		components: {
			EditOutlined,
			SettingOutlined,
			DeleteOutlined,
			ConfigurePopup
		},
		setup() {}
	})
</script>

<style lang="less" scoped>
.dataset-container {
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

.date-range-input {
  width: 240px;
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

.dataset-grid-compact {
  margin: 0 !important;
}

.dataset-card-compact {
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
  
  .dataset-card-compact:hover & {
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
  
  .dataset-card-compact:hover & {
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
  font-size: 11px;
  color: #8c8c8c;
}

.create-time-compact {
  font-size: 10px;
  color: #bfbfbf;
}

.card-avatar-compact {
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-icon-compact {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border: 1px solid white;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.avatar-text-compact {
  color: white;
  font-weight: 600;
  font-size: 12px;
}

.action-btn-compact {
  font-size: 14px;
  color: #595959;
  transition: all 0.2s ease;
  
  &:hover {
    color: #1890ff;
    transform: scale(1.05);
  }
}

.delete-btn:hover {
  color: #ff4d4f !important;
}

.pagination-container-compact {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
  padding: 8px 16px;
  background: white;
  border-radius: 0;
  box-shadow: none;
}

.pagination-compact {
  margin: 0;
}

/* 隐藏滚动条但保持功能 */
.content-area-compact {
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE 10+ */
}

.content-area-compact::-webkit-scrollbar {
  display: none; /* Chrome Safari */
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
  .search-input {
    width: 150px;
  }
  
  .date-range-input {
    width: 200px;
  }
  
  .dataset-card-compact {
    height: 140px;
  }
  
  .card-cover-compact {
    height: 70px;
  }
  
  .search-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .add-btn-float {
    align-self: flex-end;
  }
}
</style>