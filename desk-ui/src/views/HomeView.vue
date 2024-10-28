<template>
	<a-layout class="root_view">
		<!-- 顶部导航栏 -->
		<a-layout-header class="header" style="vertical-align: middle; display: flex; float: right; font-size: 1rem">
			<div class="logo" style="width: 10vw" />
			<div :style="{ lineHeight: '64px', width: '70vw', 'font-size': '1rem !important' }">
				<!-- 顶部菜单 -->
				<a-menu v-model:selectedKeys="selectedTopKeysSource" theme="dark" mode="horizontal"
					@select="onTopMenuSelect" :style="{ lineHeight: '64px', 'font-size': '1.3rem !important' }">
					<a-menu-item v-for="item in menu.topMenus" :key="item.routerName">{{
            item.name
          }}</a-menu-item>
				</a-menu>
				<!-- 顶部菜单 -->
			</div>

			<!-- 头部菜单最右侧 -->
			<div :style="{
          lineHeight: '64px',
          color: 'white',
          float: 'right',
          width: '20vw',
          display: 'inline-block',
          'text-align': 'right',
          'vertical-align': 'middle',
          'padding-right': '-1vw'
        }">
				<span>{{ user.nickname }}</span>
				<a-dropdown>
					<a class="ant-dropdown-link" @click.prevent>
						<a-avatar size="large" style="position: relative; top: -1vh">
							<template #icon>
								<UserOutlined />
							</template>
						</a-avatar>
						<DownOutlined />
					</a>
					<template #overlay>
						<a-menu>
							<a-menu-item>
								<a href="javascript:;">基本信息</a>
							</a-menu-item>
							<a-menu-item>
								<a href="javascript:;">修改密码</a>
							</a-menu-item>
							<a-menu-item>
								<a href="javascript:;" @click="exit">退出</a>
							</a-menu-item>
						</a-menu>
					</template>
				</a-dropdown>
			</div>
			<!-- 头部菜单最右侧 -->
		</a-layout-header>
		<!-- 顶部导航栏 -->
		<a-layout style="overflow-x: hidden;">
			<a-layout-sider width="200" style="background: #fff">
				<!-- 左侧菜单 -->
				<a-menu v-model:selectedKeys="selectedLeftKeysSource" v-model:openKeys="openKeysSource" mode="inline"
					:items="leftMenuSource" @select="onLeftMenuSelect" :style="{ height: '100%', borderRight: 0 }">
				</a-menu>
				<!-- 左侧菜单 -->
			</a-layout-sider>
			<a-layout style="padding: 0 24px 24px">
				<!-- 面包屑导航 -->
				<a-breadcrumb style="margin: 16px 0">
					<a-breadcrumb-item v-for="item in breadcrumbName" :key="item">{{ item }}</a-breadcrumb-item>

				</a-breadcrumb>
				<!-- 面包屑导航 -->
				<a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
					<!-- 内容区 -->
					<RouterView />
					<!-- 内容区 -->
				</a-layout-content>
			</a-layout>
		</a-layout>
	</a-layout>
</template>
<script>
	import {
		ref,
		reactive,
		defineComponent
	} from 'vue'
	import {
		mapState,
		mapActions
	} from 'pinia'
	import {
		useUserStore
	} from '@/stores/user'
	import {
		UserOutlined,
		LaptopOutlined,
		NotificationOutlined,
		DownOutlined
	} from '@ant-design/icons-vue'
	export default defineComponent({
		data() {
			const user = reactive({})
			const menu = reactive({
				leftMenus: [],
				topMenus: []
			})
			const selectedTopKeys = 'knowledge_graph'
			const selectedLeftKeys = ''
			const openKeys = ''
			return {
				user,
				menu,
				selectedTopKeys,
				selectedLeftKeys,
				openKeys
			}
		},
		computed: {
			...mapState(useUserStore, [
				'currentUserId',
				'currentRoleId',
				'currentTopMenuId',
				'currentLeftMenuId'
			]),

			breadcrumbName: function() {
				return this.$route.meta.breadcrumbName
			},
			// 上部选中的菜单
			selectedTopKeysSource: {
				get() {

					if (this.currentTopMenuId.length > 0) {
						return [this.currentTopMenuId]
					}
					if (this.selectedTopKeys.length > 0) {
						return [this.selectedTopKeys]
					}

					const routerName = this.menu.topMenus[0].routerName

					return [routerName]
				},
				// setter
				set(newValue) {
					this.selectedTopKeys = newValue
				}
			},

			/**
			 * 左侧的菜单数据
			 */
			leftMenuSource: function() {
				return this.menu.leftMenus.map((v) => {
					const item = {
						key: v.routerName,
						label: v.name,
						title: v.name
					}
					if (typeof v.childrens !== 'undefined' && v.childrens.length > 0) {
						// 存在子菜单
						item.children = v.childrens.map((m) => {
							return {
								key: m.routerName,
								label: m.name,
								title: m.name
							}
						})
					}
					return item
				})
			},
			//左侧选中的菜单
			selectedLeftKeysSource: {
				// getter
				get() {
					if (this.selectedLeftKeys.length > 0) {
						return [this.selectedLeftKeys]
					}
					if (this.currentLeftMenuId.length > 0) {
						return [this.currentLeftMenuId]
					}

					const leftOne = this.menu.leftMenus[0]
					if (typeof leftOne === 'undefined') {
						return []
					}
					if (typeof leftOne.childrens !== 'undefined' && leftOne.childrens.length > 0) {
						const routerName = leftOne.childrens[0].routerName

						return [routerName]
					}
					return [leftOne.routerName]
				},
				// setter
				set(newValue) {
					this.selectedLeftKeys = newValue
				}
			},
			// 左侧展开的菜单
			openKeysSource: {
				get() {
					if (this.openKeys.length > 0) {
						return [this.openKeys]
					}

					return this.menu.leftMenus
						.filter((v) => {
							return typeof v.childrens !== 'undefined' && v.childrens.length > 0
						})
						.map((v) => v.routerName)
				},
				// setter
				set(newValue) {
					this.openKeys = newValue
				}
			}
		},
		methods: {
			...mapActions(useUserStore, ['setRole', 'setUser', 'setTopMenuId', 'setLeftMenuId']),
			/**
			 * 加载用户基本信息
			 */
			async loadUserInfo() {
				this.$http
					.request({
						url: '/personkit/sys/user/info/' + this.currentUserId,
						method: "get"
					})
					.then((res) => {
						this.user = reactive(res)

						this.setUser(res)
						if (res.roles.length > 1) {
							//多于一个角色
						} else {
							//一个角色
							const role = res.roles[0]

							this.setRole(role)
						}
						//查询菜单
						this.findRoleMenu()
					})
					.catch((err) => console.log(err))
			},

			/**
			 * 加载用户基本菜单
			 */
			findRoleMenu() {
				this.$http
					.request({
						url: '/personkit/sys/menu/findRoleMenu',
						method: 'post',
						data: {
							roleId: this.currentRoleId,
							topMenuId: this.currentTopMenuId
						}
					})
					.then((res) => {
						this.menu = reactive(res)
					})
					.catch((err) => console.log(err))
			},
			/**
			 * 选择顶部菜单
			 * @param {*} param0
			 */
			onTopMenuSelect({
				key
			}) {
				this.setTopMenuId(key)
				this.selectedTopKeys = ref([key])
				//查询菜单
				this.findRoleMenu()

				// 重置左侧选择
				this.onLeftMenuSelect({
					key: ''
				})
			},
			/**
			 * 选择左侧菜单
			 * @param {*} param0
			 */
			onLeftMenuSelect({
				key
			}) {
				this.selectedLeftKeys = key
				this.setLeftMenuId(key)
				//路由跳转
				this.$router.push({
					name: key
				})
			},
			/**
			 * 退出登陆
			 */
			exit() {
				this.$router.push({
					name: 'login'
				})
			}
		},
		updated() {
			this.loadUserInfo()
		},
		created() {
			this.loadUserInfo()
		},

		components: {
			UserOutlined,
			LaptopOutlined,
			NotificationOutlined,
			DownOutlined
		}
	})
</script>
<style scoped>
	#components-layout-demo-top-side-2 .logo {
		float: left;
		width: 120px;
		height: 31px;
		margin: 16px 24px 16px 0;
		background: rgba(255, 255, 255, 0.3);
	}

	.ant-row-rtl #components-layout-demo-top-side-2 .logo {
		float: right;
		margin: 16px 0 16px 24px;
	}

	.site-layout-background {
		background: #fff;
	}

	.root_view {
		width: 100vw;
	}
</style>