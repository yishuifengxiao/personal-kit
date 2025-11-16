<script setup>
import { RouterView } from 'vue-router'
</script>

<template>
  <RouterView />
</template>

<style >
html,
body,
#app {
  width: 100vw !important;
  height: 100vh !important;
  max-width: 100vw !important;
  max-height: 100vh !important;
  margin: 0px;
  padding: 0px;
  overflow-x: hidden;
}

#app {
  display: flex;
  justify-content: center;
  align-items: flex-start;
}
</style>
