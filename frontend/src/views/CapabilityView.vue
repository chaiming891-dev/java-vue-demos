<script setup>
import { onMounted, reactive, ref } from 'vue'
import { api } from '../api/http'
import StatusBadge from '../components/StatusBadge.vue'
import PaginationBar from '../components/PaginationBar.vue'

const emit = defineEmits(['notify'])

const filters = reactive({
  keyword: '',
  status: '',
  page: 0,
  size: 8
})

const list = ref({ items: [], page: 0, size: 8, total: 0, totalPages: 0 })
const loading = ref(false)
const error = ref('')
const modalOpen = ref(false)
const saving = ref(false)
const editing = ref(null)
const form = reactive(emptyForm())
const fieldErrors = ref({})

function emptyForm() {
  return {
    code: '',
    name: '',
    version: '1.0.0',
    owner: '演示账号-',
    status: 'DRAFT',
    description: ''
  }
}

function formatTime(value) {
  if (!value) {
    return '—'
  }
  return new Intl.DateTimeFormat('zh-CN', {
    timeZone: 'Asia/Shanghai',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date(value))
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    list.value = await api.listCapabilities({
      keyword: filters.keyword,
      status: filters.status,
      page: filters.page,
      size: filters.size
    })
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

function search() {
  filters.page = 0
  load()
}

function openCreate() {
  editing.value = null
  Object.assign(form, emptyForm())
  fieldErrors.value = {}
  modalOpen.value = true
}

function openEdit(item) {
  editing.value = item
  Object.assign(form, {
    code: item.code,
    name: item.name,
    version: item.version,
    owner: item.owner,
    status: item.status,
    description: item.description || ''
  })
  fieldErrors.value = {}
  modalOpen.value = true
}

async function save() {
  saving.value = true
  fieldErrors.value = {}
  try {
    if (editing.value) {
      const { code, ...body } = form
      await api.updateCapability(editing.value.id, body)
      emit('notify', '已更新演示能力「' + form.name + '」')
    } else {
      await api.createCapability(form)
      emit('notify', '已新增演示能力「' + form.name + '」')
    }
    modalOpen.value = false
    await load()
  } catch (err) {
    fieldErrors.value = err.fields || {}
    emit('notify', err.message)
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<template>
  <section class="panel">
    <div class="toolbar">
      <input v-model="filters.keyword" type="search" placeholder="搜索编码、名称或负责人" @keyup.enter="search" />
      <select v-model="filters.status">
        <option value="">全部状态</option>
        <option value="DRAFT">草稿</option>
        <option value="PUBLISHED">已发布</option>
        <option value="DEPRECATED">已废弃</option>
      </select>
      <button class="btn secondary" type="button" @click="search">筛选</button>
      <span class="grow"></span>
      <button class="btn" type="button" @click="openCreate">新建能力</button>
    </div>

    <div v-if="loading" class="loading">正在加载演示数据…</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="list.items.length === 0" class="empty">没有符合条件的演示记录。</div>
    <table v-else>
      <thead>
        <tr>
          <th>能力</th>
          <th>版本</th>
          <th>负责人</th>
          <th>状态</th>
          <th>更新时间</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list.items" :key="item.id">
          <td>
            <div class="cell-title">
              <strong>{{ item.name }}</strong>
              <span class="muted mono">{{ item.code }}</span>
            </div>
          </td>
          <td class="mono">{{ item.version }}</td>
          <td>{{ item.owner }}</td>
          <td><StatusBadge :status="item.status" /></td>
          <td class="muted">{{ formatTime(item.updatedAt) }}</td>
          <td>
            <button class="btn ghost" type="button" @click="openEdit(item)">编辑</button>
          </td>
        </tr>
      </tbody>
    </table>

    <PaginationBar
      :page="list.page"
      :size="list.size"
      :total="list.total"
      :total-pages="list.totalPages"
      @change="(page) => { filters.page = page; load() }"
    />
  </section>

  <div v-if="modalOpen" class="overlay" @click.self="modalOpen = false">
    <div class="modal">
      <h3>{{ editing ? '编辑能力' : '新建能力' }}</h3>
      <p class="hint">保存的是演示数据，不会影响任何真实业务系统。</p>
      <form class="form-grid" @submit.prevent="save">
        <div class="field">
          <label for="code">编码</label>
          <input id="code" v-model="form.code" :disabled="!!editing" maxlength="32" placeholder="例如 BOOK-SEARCH" />
          <span v-if="fieldErrors.code" class="error-text">{{ fieldErrors.code }}</span>
        </div>
        <div class="field">
          <label for="version">版本</label>
          <input id="version" v-model="form.version" maxlength="20" />
          <span v-if="fieldErrors.version" class="error-text">{{ fieldErrors.version }}</span>
        </div>
        <div class="field">
          <label for="name">名称</label>
          <input id="name" v-model="form.name" maxlength="80" placeholder="建议带上「演示」字样" />
          <span v-if="fieldErrors.name" class="error-text">{{ fieldErrors.name }}</span>
        </div>
        <div class="field">
          <label for="owner">负责人</label>
          <input id="owner" v-model="form.owner" maxlength="40" />
          <span v-if="fieldErrors.owner" class="error-text">{{ fieldErrors.owner }}</span>
        </div>
        <div class="field">
          <label for="status">状态</label>
          <select id="status" v-model="form.status">
            <option value="DRAFT">草稿</option>
            <option value="PUBLISHED">已发布</option>
            <option value="DEPRECATED">已废弃</option>
          </select>
        </div>
        <div class="field span-2">
          <label for="description">说明</label>
          <textarea id="description" v-model="form.description" rows="3" maxlength="500" placeholder="用一两句话说明这是演示能力。"></textarea>
          <span v-if="fieldErrors.description" class="error-text">{{ fieldErrors.description }}</span>
        </div>
        <div class="modal-actions span-2">
          <button class="btn secondary" type="button" @click="modalOpen = false">取消</button>
          <button class="btn" type="submit" :disabled="saving">{{ saving ? '保存中…' : '保存' }}</button>
        </div>
      </form>
    </div>
  </div>
</template>
