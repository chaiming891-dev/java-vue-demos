<script setup>
import { onMounted, reactive, ref } from 'vue'
import { api } from '../api/http'
import StatusBadge from '../components/StatusBadge.vue'
import PaginationBar from '../components/PaginationBar.vue'

const emit = defineEmits(['notify'])

const filters = reactive({
  capabilityId: '',
  status: '',
  page: 0,
  size: 8
})

const list = ref({ items: [], page: 0, size: 8, total: 0, totalPages: 0 })
const capabilities = ref([])
const loading = ref(false)
const error = ref('')
const modalOpen = ref(false)
const reviewOpen = ref(false)
const saving = ref(false)
const current = ref(null)
const form = reactive({
  capabilityId: '',
  applicant: '演示账号-',
  team: '演示组-',
  purpose: ''
})
const review = reactive({
  status: 'APPROVED',
  remark: '演示通过'
})
const fieldErrors = ref({})

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

async function loadCapabilities() {
  const data = await api.listCapabilities({ page: 0, size: 100, sort: 'name,asc' })
  capabilities.value = data.items || []
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    list.value = await api.listRequests({
      capabilityId: filters.capabilityId,
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
  fieldErrors.value = {}
  form.capabilityId = filters.capabilityId || (capabilities.value[0] && capabilities.value[0].id) || ''
  form.applicant = '演示账号-'
  form.team = '演示组-'
  form.purpose = '这是一条演示申请，仅用于作品集联调。'
  modalOpen.value = true
}

function openReview(item, status) {
  current.value = item
  review.status = status
  review.remark = status === 'APPROVED' ? '演示通过' : '演示驳回'
  reviewOpen.value = true
}

async function save() {
  saving.value = true
  fieldErrors.value = {}
  try {
    await api.createRequest({
      capabilityId: Number(form.capabilityId),
      applicant: form.applicant,
      team: form.team,
      purpose: form.purpose
    })
    modalOpen.value = false
    emit('notify', '已提交演示开通申请')
    await load()
  } catch (err) {
    fieldErrors.value = err.fields || {}
    emit('notify', err.message)
  } finally {
    saving.value = false
  }
}

async function submitReview() {
  saving.value = true
  try {
    await api.reviewRequest(current.value.id, {
      status: review.status,
      remark: review.remark
    })
    reviewOpen.value = false
    emit('notify', review.status === 'APPROVED' ? '已通过该演示申请' : '已驳回该演示申请')
    await load()
  } catch (err) {
    emit('notify', err.message)
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  try {
    await loadCapabilities()
  } catch (err) {
    error.value = err.message
  }
  await load()
})
</script>

<template>
  <section class="panel">
    <div class="toolbar">
      <select v-model="filters.capabilityId">
        <option value="">全部能力</option>
        <option v-for="item in capabilities" :key="item.id" :value="item.id">
          {{ item.name }}
        </option>
      </select>
      <select v-model="filters.status">
        <option value="">全部状态</option>
        <option value="PENDING">待审核</option>
        <option value="APPROVED">已通过</option>
        <option value="REJECTED">已驳回</option>
      </select>
      <button class="btn secondary" type="button" @click="search">筛选</button>
      <span class="grow"></span>
      <button class="btn" type="button" @click="openCreate">新建申请</button>
    </div>

    <div v-if="loading" class="loading">正在加载演示数据…</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="list.items.length === 0" class="empty">没有符合条件的演示申请。</div>
    <table v-else>
      <thead>
        <tr>
          <th>单号</th>
          <th>能力</th>
          <th>申请人 / 团队</th>
          <th>说明</th>
          <th>状态</th>
          <th>提交时间</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in list.items" :key="item.id">
          <td class="mono">{{ item.requestNo }}</td>
          <td>
            <div class="cell-title">
              <strong>{{ item.capabilityName }}</strong>
              <span class="muted mono">{{ item.capabilityCode }}</span>
            </div>
          </td>
          <td>
            <div class="cell-title">
              <span>{{ item.applicant }}</span>
              <span class="muted">{{ item.team }}</span>
            </div>
          </td>
          <td>
            <div class="cell-title">
              <span>{{ item.purpose }}</span>
              <span v-if="item.remark" class="muted">备注：{{ item.remark }}</span>
            </div>
          </td>
          <td><StatusBadge :status="item.status" /></td>
          <td class="muted">{{ formatTime(item.createdAt) }}</td>
          <td>
            <div v-if="item.status === 'PENDING'" class="actions">
              <button class="btn ghost" type="button" @click="openReview(item, 'APPROVED')">通过</button>
              <button class="btn ghost danger" type="button" @click="openReview(item, 'REJECTED')">驳回</button>
            </div>
            <span v-else class="muted">已处理</span>
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
      <h3>新建开通申请</h3>
      <p class="hint">申请人与团队请使用「演示账号 / 演示组」前缀，避免被误认为真实人员。</p>
      <form class="form-grid" @submit.prevent="save">
        <div class="field span-2">
          <label for="capabilityId">能力</label>
          <select id="capabilityId" v-model="form.capabilityId">
            <option disabled value="">请选择</option>
            <option v-for="item in capabilities" :key="item.id" :value="item.id">
              {{ item.name }}（{{ item.code }}）
            </option>
          </select>
          <span v-if="fieldErrors.capabilityId" class="error-text">{{ fieldErrors.capabilityId }}</span>
        </div>
        <div class="field">
          <label for="applicant">申请人</label>
          <input id="applicant" v-model="form.applicant" maxlength="40" />
          <span v-if="fieldErrors.applicant" class="error-text">{{ fieldErrors.applicant }}</span>
        </div>
        <div class="field">
          <label for="team">团队</label>
          <input id="team" v-model="form.team" maxlength="40" />
          <span v-if="fieldErrors.team" class="error-text">{{ fieldErrors.team }}</span>
        </div>
        <div class="field span-2">
          <label for="purpose">申请说明</label>
          <textarea id="purpose" v-model="form.purpose" rows="3" maxlength="300"></textarea>
          <span v-if="fieldErrors.purpose" class="error-text">{{ fieldErrors.purpose }}</span>
        </div>
        <div class="modal-actions span-2">
          <button class="btn secondary" type="button" @click="modalOpen = false">取消</button>
          <button class="btn" type="submit" :disabled="saving">{{ saving ? '提交中…' : '提交申请' }}</button>
        </div>
      </form>
    </div>
  </div>

  <div v-if="reviewOpen" class="overlay" @click.self="reviewOpen = false">
    <div class="modal">
      <h3>{{ review.status === 'APPROVED' ? '通过申请' : '驳回申请' }}</h3>
      <p class="hint" v-if="current">
        单号 {{ current.requestNo }} · {{ current.capabilityName }}（演示审核，可反复用种子数据验证流程）
      </p>
      <form class="form-grid" @submit.prevent="submitReview">
        <div class="field span-2">
          <label for="remark">审核备注</label>
          <textarea id="remark" v-model="review.remark" rows="3" maxlength="300"></textarea>
        </div>
        <div class="modal-actions span-2">
          <button class="btn secondary" type="button" @click="reviewOpen = false">取消</button>
          <button
            class="btn"
            :class="{ danger: review.status === 'REJECTED' }"
            type="submit"
            :disabled="saving"
          >
            {{ saving ? '提交中…' : review.status === 'APPROVED' ? '确认通过' : '确认驳回' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
