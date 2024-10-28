<template>
    <a-modal v-model:open="open" title="选择角色" width="50%" :footer="null" :closable="false">
        <div class="login_model"> <a-list item-layout="horizontal" :data-source="data">
                <template #renderItem="{ item }">
                    <a-list-item>
                        <template #actions>
                            <a key="list-loadmore-edit">选择</a>
                        </template>
                        <a-list-item-meta :description="item.description">
                            <template #title>
                                <a href="JavaScript:void(0)" @click="choose(item)">{{ item.name }}</a>
                            </template>
                        </a-list-item-meta>
                    </a-list-item>
                </template>
            </a-list></div>

    </a-modal>
</template>

<script>
import { ref, reactive, defineComponent } from 'vue'
export default defineComponent({
    data() {
        const data = reactive([]);
        return { data }
    },

    methods: {

        show(params) {
            this.data = reactive(params)
            this.open = ref(true)
        },
        choose(item) {
            this.$emit("action", item);
            this.open = ref(false)
        }
    },
    setup() {
        const open = ref(false)
        return { open }
    }


})
</script>

<style scoped>
.login_model {

    height: 50vh;
    vertical-align: middle;
}
</style>