<template>
	<div style="height: 100%;">
		<a-steps type="navigation" :current="0" :items="steps"></a-steps>
		<a-row style="height: 89%;">
			<!-- 左侧图谱区域 -->
			<a-col :span="12" style="height: 100%;">
				<!-- 参见 https://www.relation-graph.com/#/docs/start-vue3 -->
				<div id="mountNode" ref="myPage" style="border: gray dashed thin; height: 100%;">
					<RelationGraph ref="graphRef" :options="graphOptions" :on-node-click="onNodeClick">
						<template #node="{ node }">
							<div>
								<div class="c-my-rg-node"></div>
								<div style="
                    color: forestgreen;
                    font-size: 16px;
                    position: absolute;
                    width: 160px;
                    height: 25px;
                    line-height: 25px;
                    margin-top: 5px;
                    margin-left: -48px;
                    text-align: center;
                    background-color: rgba(66, 187, 66, 0.2);
                  ">
									{{ node.text }}
									<!-- {{ node.data.myicon }} -->
								</div>
							</div>
						</template>
					</RelationGraph>
				</div>
			</a-col>
			<!-- 左侧图谱区域 -->
			<!-- 右侧配置区域 -->
			<a-col :span="12">
				<a-row>
					<a-col :span="20" :offset="2">
						<div>
							选择本体: <a-select ref="select" placeholder="选择本体" @search="handleSearch" show-search
								v-model:value="value1" style="width: 320px" :options="ontOptions"
								@change="onOntValChange"></a-select>
						</div>
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
		reactive
	} from 'vue'

	export default defineComponent({
		data() {
			return {
				ontOptions: reactive([])
			}
		},
		methods: {
			//选择本体
			onOntValChange(val) {
				debugger;
			},
			//搜索本体
			handleSearch(val) {
				this.$http
					.request({
						url: '/personkit/graph/ont/page',
						data: {
							num: 1,
							query: {
								ontologyName: val
							},
							size: 10
						}
					})
					.then((res) => {
						const opts = res.data.map(v => {
							return {
								"label": v.ontologyName,
								"value": v.id
							}
						});
						this.ontOptions = reactive(opts);
					})
					.catch((err) => console.log(err))
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
			const graphOptions = {
				backgroundImageNoRepeat: true,
				backgrounImageNoRepeat: true,
				isMoveByParentNode: true,
				defaultExpandHolderPosition: 'top',
				defaultLineShape: 3,
				defaultJunctionPoint: 'ltrb',
				placeOtherGroup: true,
				allowSwitchLineShape: true,
				allowSwitchJunctionPoint: true,
				moveToCenterWhenRefresh: true,
				layouts: [{
					label: '中心',
					layoutName: 'center',
					distance_coefficient: 1
				}]
			}

			return {
				steps,
				graphOptions
			}
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