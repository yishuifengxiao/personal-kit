<template>
  <div>
    <!-- 上部搜索条件区域 -->
    <a-form layout="inline" name="basic" autocomplete="off" :model="formState" @finish="handleFinish"
      :label-col="labelCol">
      <a-form-item label="角色名称" name="name" class="input">
        <a-input allowClear v-model:value="formState.name" placeholder="角色名称，模糊查询"> </a-input>
      </a-form-item>

      <a-form-item label="角色状态" name="stat" class="input">
        <a-select 
          allowClear 
          style="width: 180px" 
          placeholder="请选择状态" 
          v-model:value="formState.stat"
          :options="statusOptions"
        ></a-select>
      </a-form-item>

      <a-form-item label="角色描述" name="description" class="input">
        <a-input allowClear v-model:value="formState.description" placeholder="角色描述，模糊查询"> </a-input>
      </a-form-item>

      <a-form-item label="包含菜单" name="menuName" class="input">
        <a-input allowClear v-model:value="formState.menuName" placeholder="包含菜单，模糊查询"> </a-input>
      </a-form-item>

      <a-space class="input">
        <a-button type="primary" html-type="submit"> 搜索 </a-button>
        <a-button type="primary" html-type="submit"> 创建角色 </a-button>
      </a-space>
    </a-form>

    <!-- 上部搜索条件区域 -->
    <a-divider dashed />
    <!-- 中间内容区域 -->
    <!-- 表格区 -->
    <a-table :columns="columns" :data-source="tableData" :pagination="false" :scroll="{ x: 1500 }">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <a-space>
            <a>删除</a> 
            <a-button type="link" @click="modifyMenus(record)">修改菜单</a-button> 
            <a-button type="link" @click="toggleStatus(record)">{{ record.stat === 1 ? '禁用' : '启用' }}</a-button>
          </a-space>
        </template>
        <template v-else-if="column.dataIndex === 'stat'">
          <a-tag :color="record.stat === 1 ? 'green' : 'red'">
            {{ record.stat === 1 ? '启用' : '禁用' }}
          </a-tag>
        </template>
      </template>
    </a-table>
    <!-- 表格区 -->

    <!-- 分页区 -->
    <div style="margin-top: 15px; float: right">
      <a-pagination v-model:current="pagination.current" :total="pagination.total"
        :show-total="(total) => `共 ${total} 条数据`" @change="onPaginationChange" />
    </div>
    <!-- 分页区 -->
    <!-- 中间内容区域 -->
  </div>
</template>

<script>
import { reactive, defineComponent, ref } from 'vue'
import { mapState } from 'pinia'
import { useUserStore } from '@/stores/user'
import { message } from 'ant-design-vue'

export default defineComponent({
  data() {
    const formState = reactive({
      menuName: '',
      name: '',
      description: '',
      stat: undefined
    })
    const data = reactive([])
    const roleSource = reactive([])
    return { formState, data, roleSource }
  },
  computed: {
    ...mapState(useUserStore, ['tokenVal']),
    tableData: function () {
      return this.data
    },
    headers: function () {
      return { Authorization: 'xtoken ' + this.tokenVal }
    },
    uploadUrl: function () {
      return this.$cfg.rootUrl() + '/personkit/disk/file/import'
    }
  },
  methods: {
    handleFinish() {
      this.query()
    },
    /**
     * 查询数据
     */
    query() {
      this.$http
        .request({
          url: '/personkit/sys/role/page',
          data: {
            num: this.pagination.current,
            query: this.formState,
            size: this.pagination.pageSize
          }
        })
        .then((res) => {
          this.pagination.current = res.num
          this.pagination.total = res.total
          this.data = reactive(res.data)
        })
        .catch((err) => console.log(err))
    },

    /**
     * 切换角色状态
     */
    toggleStatus(record) {
      const newStatus = record.stat === 1 ? 0 : 1
      const statusText = newStatus === 1 ? '启用' : '禁用'
      
      this.$http
        .request({
          url: '/personkit/sys/role/status',
          method: 'post',
          data: {
            roleId: record.id,
            stat: newStatus
          }
        })
        .then((res) => {
          if (res.code === 200) {
            message.success(`角色${statusText}成功`)
            // 更新本地数据
            record.stat = newStatus
          } else {
            message.error(res.message || `${statusText}失败`)
          }
        })
        .catch((err) => {
          console.error('切换状态失败:', err)
          message.error(`${statusText}失败`)
        })
    },

    handleChange(info) {
      if (info.file.status !== 'uploading') {
        console.log(info.file, info.fileList)
      }
      if (info.file.status === 'done') {
        this.$msg.success(`${info.file.name} file uploaded successfully`)
      } else if (info.file.status === 'error') {
        this.$msg.error(`${info.file.name} file upload failed.`)
      }
      this.query()
    },
    //导航栏发生变化
    onPaginationChange(page, pageSize) {
      this.pagination.current = page
      this.query()
    },
    //查看详情
    showDetail(record) {
      sessionStorage.setItem('current_view_file', JSON.stringify(record))

      this.$router.push({ name: 'data_source_detail', query: { record: record.id } })
    },
    /**
     * 修改菜单 - 跳转到角色菜单配置页面
     */
    modifyMenus(record) {
      // 将当前角色信息存储到sessionStorage，供角色菜单配置页面使用
      sessionStorage.setItem(
        'current_role_for_menu',
        JSON.stringify({
          id: record.id,
          name: record.name,
          description: record.description,
          stat: record.stat
        })
      )

      // 跳转到角色菜单配置页面
      this.$router.push({
        name: 'role_menu_management',
        query: { roleId: record.id }
      })
    }
  },
  mounted() {
    this.query()
  },
  setup() {
    const columns = [
      {
        title: '角色名称',
        dataIndex: 'name',
        key: 'name',
        align: 'center'
      },
      {
        title: '角色状态',
        dataIndex: 'stat',
        key: 'stat',
        align: 'center',
        width: 100
      },
      {
        title: '角色描述',
        dataIndex: 'description',
        key: 'description',
        ellipsis: true,
        align: 'center'
      },
      {
        title: '包含菜单',
        dataIndex: 'menuName',
        key: 'menuName',
        ellipsis: true,
        width: 150,
        align: 'center'
      },
      {
        title: '操作',
        dataIndex: 'action',
        key: 'action',
        width: 300,
        align: 'center',
        fixed: 'right'
      }
    ]
    const pagination = {
      current: 1,
      total: 0,
      pageSize: 10
    }
    const fileList = ref([])
    const labelCol = { style: { width: '80px' } }
    const wrapperCol = { span: 14 }
    const statusOptions = reactive([
      { label: '启用', value: 1 },
      { label: '禁用', value: 0 }
    ])
    return { columns, pagination, fileList, labelCol, wrapperCol, statusOptions }
  }
})
</script>

<style lang="less" scoped>
.input {
  margin: 5px 5px 10px 5px;
  padding-right: 10px;
}
</style>