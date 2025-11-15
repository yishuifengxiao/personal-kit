<template>
	<div>
		<a-modal :title="title" :open="open" @ok="onOk" @cancel="onClose" okText="确认" cancelText="取消" :width="600">
			<a-form ref="formRef" :model="formState" name="basic" autocomplete="off" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
				<a-form-item label="数据集名称" name="name" :rules="[{ required: true, message: '请输入数据集名称' }]">
					<a-input v-model:value="formState.name" placeholder="请输入数据集名称" allowClear />
				</a-form-item>

				<a-form-item label="数据集描述" name="description">
					<a-textarea v-model:value="formState.description" placeholder="请输入数据集描述" :rows="3" allowClear />
				</a-form-item>

				<a-form-item label="选择数据源" name="diskFiles" :rules="[{ required: true, message: '请选择数据源' }]">
					<a-select 
						allowClear 
						v-model:value="formState.diskFiles" 
						show-search 
						placeholder="请输入关键字搜索数据源"
						mode="multiple" 
						:default-active-first-option="false" 
						:show-arrow="false" 
						:filter-option="false"
						:not-found-content="null" 
						:options="selectOptionSource" 
						@search="handleSearch">
					</a-select>
				</a-form-item>
			</a-form>
		</a-modal>
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
				isUpdate: false
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
						url: '/personkit/data/center/file/page',
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
	
						if (this.isUpdate) {
							this.updateData(Object.assign(this.formState, res));
						} else {
							this.addDataSet(Object.assign(this.formState, res))
						}

					})
					.catch((err) => {
						console.info('不通过', err)
					})
			},
			//更新数据
			updateData(val) {
				this.$http
					.request({
						url: '/personkit/data/dataSet/update',
						data: val
					})
					.then((res) => {
						this.open = false
						this.$emit('ok')
						this.$msg.success('更新成功')
					})
			},
			// 添加数据
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
				this.isUpdate = typeof param != 'undefined';
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
				open,
				onClose,
				title,
				selectOptions,
				formState
			}
		}
	})
</script>