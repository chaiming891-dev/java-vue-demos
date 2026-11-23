<script setup>
import { computed, onMounted, ref } from 'vue'
import { api } from './api/http'
import CapabilityView from './views/CapabilityView.vue'
import RequestView from './views/RequestView.vue'

const views = {
  capabilities: CapabilityView,
  requests: RequestView
}

const current = ref('capabilities')
const toast = ref('')
const health = ref({ state: 'pending', text: '正在连接后端…' })

const currentView = computed(() => views[current.value])

const titles = {
  capabilities: {
    heading: '能力目录',
    desc: '登记虚构的内部服务能力。以下数据均为演示，可筛选、新建与编辑。'
  },
  requests: {
    heading: '开通申请',
    desc: '针对某项能力提交开通申请，并演示待审核 / 通过 / 驳回流程。'
  }
}

function notify(message) {
  toast.value = message
  window.setTimeout(() => {
    if (toast.value === message) {
      toast.value = ''
    }
  }, 3200)
}

async function ping() {
  try {
    const data = await api.health()
    health.value = {
      state: 'ok',
      text: data.demo ? '后端已连接 · 演示模式' : '后端已连接'
    }
  } catch {
    health.value = {
      state: 'bad',
      text: '后端未连接，请先启动 :8080'
    }
  }
}

onMounted(ping)
</script>

<template>
  <div class="shell">
    <aside class="sidebar">
      <div class="brand">
        <span class="demo-pill">DEMO</span>
        <h1>流光工作台</h1>
        <p class="subtitle">Lumen Desk · 公开作品集演示<br />虚构数据，非生产系统</p>
      </div>
      <nav class="nav">
        <button type="button" :class="{ active: current === 'capabilities' }" @click="current = 'capabilities'">
          能力目录
        </button>
        <button type="button" :class="{ active: current === 'requests' }" @click="current = 'requests'">
          开通申请
        </button>
      </nav>
      <div class="sidebar-foot">
        柴铭 · chaiming891-dev<br />
        MIT License © 2026
      </div>
    </aside>

    <main class="main">
      <div class="topbar">
        <div>
          <h2>{{ titles[current].heading }}</h2>
          <p>{{ titles[current].desc }}</p>
        </div>
        <div class="health">
          <span class="dot" :class="health.state === 'ok' ? 'ok' : health.state === 'bad' ? 'bad' : ''"></span>
          {{ health.text }}
        </div>
      </div>
      <component :is="currentView" @notify="notify" />
    </main>
  </div>
  <div v-if="toast" class="toast">{{ toast }}</div>
</template>
