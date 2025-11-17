// 工具箱页面测试
import { mount } from '@vue/test-utils'
import ToolBox from '@/views/tool_box/Index.vue'

describe('ToolBox.vue', () => {
  it('renders tool selection dropdown', () => {
    const wrapper = mount(ToolBox)
    expect(wrapper.find('a-select').exists()).toBe(true)
  })

  it('shows placeholder when no tool is selected', () => {
    const wrapper = mount(ToolBox)
    expect(wrapper.find('.tool-placeholder').exists()).toBe(true)
  })

  it('displays correct tool when selected', async () => {
    const wrapper = mount(ToolBox)
    await wrapper.setData({ selectedTool: 'json-format' })
    expect(wrapper.find('.tool-panel').exists()).toBe(true)
  })
})