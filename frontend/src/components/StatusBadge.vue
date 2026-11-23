<script setup>
const labels = {
  DRAFT: '草稿',
  PUBLISHED: '已发布',
  DEPRECATED: '已废弃',
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已驳回'
}

defineProps({
  status: { type: String, required: true }
})
</script>

<template>
  <span class="badge" :class="status">{{ labels[status] || status }}</span>
</template>
