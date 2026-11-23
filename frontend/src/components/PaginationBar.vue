<script setup>
const props = defineProps({
  page: { type: Number, required: true },
  size: { type: Number, required: true },
  total: { type: Number, required: true },
  totalPages: { type: Number, required: true }
})

const emit = defineEmits(['change'])

function prev() {
  if (props.page > 0) {
    emit('change', props.page - 1)
  }
}

function next() {
  if (props.page + 1 < props.totalPages) {
    emit('change', props.page + 1)
  }
}
</script>

<template>
  <div class="pager">
    <span>共 {{ total }} 条 · 第 {{ total === 0 ? 0 : page + 1 }} / {{ totalPages || 1 }} 页</span>
    <div class="pager-actions">
      <button class="btn secondary" type="button" :disabled="page <= 0" @click="prev">上一页</button>
      <button class="btn secondary" type="button" :disabled="page + 1 >= totalPages" @click="next">下一页</button>
    </div>
  </div>
</template>
