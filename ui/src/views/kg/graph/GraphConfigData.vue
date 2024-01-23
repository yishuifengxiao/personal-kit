<template>
	<div style="height: 100%;">
		<a-steps type="navigation" :current="1" :items="steps"></a-steps>
		<a-divider></a-divider>
		<a-row style="height: 89%;">
			<!-- 左侧图谱区域 -->
			<a-col :span="12" style="height: 100%;">
				<a-row>
					<a-col :span="20" :offset="2">
						<div>
							选择数据源: <a-select ref="select" placeholder="选择数据源" @search="handleSearch" show-search
								v-model:value="ontId" style="width: 320px" :options="ontOptions"
								@change="onOntValChange"></a-select>
						</div>
					</a-col>
				</a-row>
				<a-divider></a-divider>
				<a-tabs v-model:activeKey="activeKey">
					<a-tab-pane key="1" tab="结构化数据抽取">Content of Tab Pane 1</a-tab-pane>
					<a-tab-pane key="2" tab="非结构化数据抽取" force-render>Content of Tab Pane 2</a-tab-pane>
				</a-tabs>
			</a-col>
			<!-- 左侧图谱区域 -->
			<!-- 右侧配置区域 -->
			<a-col :span="12">



				<a-row style="margin-top: 3rem;">
					<a-col :span="20" :offset="2">
						<a-button type="primary" @click="nextStep">保存配置</a-button>
					</a-col>
				</a-row>

			</a-col>
			<!-- 右侧配置区域 -->
		</a-row>
	</div>
</template>

<script>
	import {
		defineComponent,
		reactive,
		ref
	} from 'vue'
	export default defineComponent({
		data() {
			return {
				datasetId: ref(""),
				ontOptions: reactive([]),
				graphOptions: reactive({}),
				activeKey: ref('1')
			}
		},
		methods: {
			//选择本体
			onOntValChange(val) {
				this.datasetId = ref(val)

				this.$http
					.request({
						url: '/personkit/data/dataSet/detail',
						data: {
							id: val
						}
					})
					.then((res) => {
						debugger;
					})
					.catch((err) => console.log(err))
			},
			//搜索本体
			handleSearch(val) {
				this.$http
					.request({
						url: '/personkit/data/dataSet/page',
						data: {
							num: 1,
							query: {
								name: val
							},
							size: 10
						}
					})
					.then((res) => {
						const opts = res.data.map(v => {
							return {
								"label": v.name,
								"value": v.id
							}
						});
						this.ontOptions = reactive(opts);
					})
					.catch((err) => console.log(err))
			},
			//选择节点
			onNodeClick(val) {
				debugger
			},

			//下一步
			nextStep() {

				if (this.ontId.value.length === 0) {
					this.$msg.error("请先配置本体");
					return false
				}
				this.$router.push({
					name: "graph_config_data",
					query: {
						ontId: this.ontId
					}
				})
			}
		},
		mounted() {
			this.handleSearch('')
		},
		setup() {
			const steps = [{
					"title": '配置本体',
					"description": "",
				},
				{
					"title": '配置数据源',
					"description": "",
					"subTitle": '配置数据抽取',
				},
				{
					"title": '配置映射',
					"description": "",
				},
				{
					"title": '配置融合',
					"description": "可选",
				},
				{
					"title": '生成图谱',
					"description": "",
				},
			]

			return {
				steps
			}
		},
		components: {

		}
	})
</script>

<style>
</style>
<style lang="css" scoped>
	.c-my-rg-node {
		height: 80px;
		line-height: 80px;
		border-radius: 50%;
		cursor: pointer;
		display: flex;
		place-items: center;
		justify-content: center;
	}

	.c-node-menu-item {
		line-height: 30px;
		padding-left: 10px;
		cursor: pointer;
		color: #444444;
		font-size: 14px;
		border-top: #efefef solid 1px;
	}

	.c-node-menu-item:hover {
		background-color: rgba(66, 187, 66, 0.2);
	}
</style>