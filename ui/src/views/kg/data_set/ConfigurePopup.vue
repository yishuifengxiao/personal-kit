<template>
	<div>
		<a-drawer :width="768" :title="title" :placement="placement" :open="open" @close="onClose" :closable="false">
			<template #extra>
				<a-button style="margin-right: 8px" @click="onClose">取消</a-button>
				<a-button type="primary" @click="onOk">确认</a-button>
			</template>

			<a-form ref="formRef" :model="formState" name="basic" autocomplete="off">
				<a-form-item label="数据集名称" name="name" :rules="[{ required: true, message: '请输入数据集名称' }]">
					<a-input v-model:value="formState.name" placeholder="数据集名称" allowClear />
				</a-form-item>

				<a-form-item label="&nbsp;&nbsp;&nbsp;数据集描述" name="description">
					<a-textarea v-model:value="formState.description" placeholder="数据集描述" :rows="4" allowClear />
				</a-form-item>

				<a-form-item label="选择数据源" name="diskFiles" :rules="[{ required: true, message: '选择数据源' }]">
					<a-select allowClear v-model:value="formState.diskFiles" show-search placeholder="输入关键字搜索数据源"
						mode="multiple" :default-active-first-option="false" :show-arrow="false" :filter-option="false"
						:not-found-content="null" :options="selectOptionSource" @search="handleSearch"></a-select>
				</a-form-item>
			</a-form>
			{{formState}}
		</a-drawer>
	</div>
</template>
<script>
	import {
		ref,
		defineComponent,
		reactive
	} from 'vue'

	export default defineComponent({
		data() {
			return {

			}
		},
		computed: {
			selectOptionSource: function() {
				return this.selectOptions.map((v) => {
					return {
						value: v.id,
						label: v.fileName
					}
				})
			}
		},
		methods: {
			handleSearch(val) {
				this.$http
					.request({
						url: '/personkit/data/center/upload/page',
						data: {
							num: 1,
							query: {
								fileName: val
							},
							size: 10
						}
					})
					.then((res) => {
						this.selectOptions = reactive(res.data)
					})
					.catch((err) => console.log(err))
			},
			// 确认按钮，触发表单提交
			onOk() {
				this.$refs.formRef
					.validate()
					.then((res) => {
						console.info('-=-=-=-=通过' + JSON.stringify(res))
						this.addDataSet(res)
					})
					.catch((err) => {
						console.info('不通过', err)
					})
			},
			addDataSet(val) {
				const that = this
				this.$http
					.request({
						url: '/personkit/data/dataSet/save',
						data: val
					})
					.then((res) => {
						that.open = false
						that.$emit('ok')
						that.$msg.success('创建成功')
					})
					.catch((err) => console.log(err))
			},
			// 加载数据
			load() {
				this.$http
					.request({
						url: '/personkit/data/dataSet/detail',
						data: {
							id: this.formState.id
						}
					})
					.then((res) => {

						this.formState = reactive(
							res
						)
						this.open = true
					})
					.catch((err) => console.log(err))
			},
			showDrawer(param) {
				this.title = typeof param === 'undefined' ? '添加数据集' : '编辑数据集'
				if (typeof param != 'undefined') {
					this.formState.id = param.id

					console.log('-----------------------' + JSON.stringify(param))
					this.load()
				} else {
					this.formState = reactive({
						description: '',
						name: '',
						diskFiles: []
					})
					this.open = true
				}
			}
		},
		mounted() {
			this.handleSearch('')
		},
		setup() {
			const placement = ref('left')

			const open = ref(false)
			let title = ref('添加数据集')
			let formState = reactive({
				description: '',
				name: '',
				diskFiles: []
			})

			const onClose = () => {
				open.value = false
			}


			const selectOptions = ref([])

			return {
				placement,
				open,
				onClose,
				title,
				selectOptions,
				formState
			}
		}
	})
</script>