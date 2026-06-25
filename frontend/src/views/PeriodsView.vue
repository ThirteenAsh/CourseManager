<script setup>
import { computed, inject, onMounted, ref } from 'vue'
import { getPeriodList, addPeriod, updatePeriod, deletePeriod } from '../api/period'

// 列表
const tableData = ref([])
const searchKeyword = ref('')

// 弹窗
const showDialog = ref(false)
const editingId = ref(null) // null=新增
const form = ref({
  periodNo: '',
  startTime: '',
  endTime: '',
  remark: '',
})

const notify = inject('notify', () => {})
const confirmAction = inject('confirmAction', async () => false)

const filteredData = computed(() => {
  const keyword = searchKeyword.value.trim()
  if (!keyword) return tableData.value
  return tableData.value.filter((row) => {
    return String(row.periodNo).includes(keyword) || (row.remark || '').includes(keyword)
  })
})

function showToast(message, type = 'success') {
  notify(message, type)
}

function formatTime(value) {
  if (!value) return '-'
  return String(value).slice(0, 5)
}

function toTimePayload(value) {
  if (!value) return ''
  return value.length === 5 ? `${value}:00` : value
}

function loadData() {
  return getPeriodList().then((data) => {
    tableData.value = data || []
  }).catch((err) => {
    showToast('加载失败：' + err.message, 'error')
  })
}

function load() {
  return loadData()
}

function handleSearch() {
  searchKeyword.value = searchKeyword.value.trim()
}

function resetForm() {
  form.value = {
    periodNo: '',
    startTime: '',
    endTime: '',
    remark: '',
  }
}

// 新增
function openAdd() {
  editingId.value = null
  resetForm()
  showDialog.value = true
}

// 编辑
function openEdit(row) {
  editingId.value = row.periodId
  form.value = {
    periodNo: row.periodNo || '',
    startTime: formatTime(row.startTime) === '-' ? '' : formatTime(row.startTime),
    endTime: formatTime(row.endTime) === '-' ? '' : formatTime(row.endTime),
    remark: row.remark || '',
  }
  showDialog.value = true
}

function closeDialog() {
  showDialog.value = false
}

function handleSave() {
  const periodNo = Number(form.value.periodNo)
  const startTime = form.value.startTime
  const endTime = form.value.endTime
  const remark = form.value.remark.trim()

  if (!Number.isInteger(periodNo) || periodNo <= 0) {
    showToast('请输入大于0的节次序号', 'error')
    return
  }
  if (!startTime) {
    showToast('请选择上课时间', 'error')
    return
  }
  if (!endTime) {
    showToast('请选择下课时间', 'error')
    return
  }
  if (endTime <= startTime) {
    showToast('下课时间必须晚于上课时间', 'error')
    return
  }
  if (!remark) {
    showToast('请输入备注', 'error')
    return
  }

  const payload = {
    periodNo,
    startTime: toTimePayload(startTime),
    endTime: toTimePayload(endTime),
    remark,
  }
  const successMessage = editingId.value ? '修改节次成功' : '新增节次成功'
  const request = editingId.value
    ? updatePeriod(editingId.value, payload)
    : addPeriod(payload)

  request.then(() => {
    closeDialog()
    load()
    showToast(successMessage)
  }).catch((err) => {
    showToast(err.message, 'error')
  })
}

// 删除
async function handleDelete(row) {
  const confirmed = await confirmAction({
    title: '删除节次',
    message: `确认删除「第${row.periodNo}节」？删除后不可恢复。`,
    confirmText: '删除',
    type: 'danger',
  })
  if (!confirmed) return
  deletePeriod(row.periodId).then(() => {
    load()
    showToast('删除节次成功')
  }).catch((err) => {
    showToast(err.message, 'error')
  })
}

onMounted(load)
</script>

<template>
  <section class="page-section">
    <div class="page-title">
      <h2>节次管理</h2>
      <button type="button" class="primary" @click="openAdd">新增节次</button>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3>节次列表</h3>
        <div class="search-bar">
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="输入节次或备注搜索"
            @keyup.enter="handleSearch"
          />
          <button type="button" @click="handleSearch">搜索</button>
        </div>
      </div>
      <div class="panel-body">
        <table>
          <thead>
            <tr>
              <th style="width: 60px">序号</th>
              <th style="width: 120px">节次</th>
              <th style="width: 150px">上课时间</th>
              <th style="width: 150px">下课时间</th>
              <th>备注</th>
              <th style="width: 140px">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="filteredData.length === 0">
              <td colspan="6" class="empty-cell">暂无数据</td>
            </tr>
            <tr v-for="(row, idx) in filteredData" :key="row.periodId">
              <td>{{ idx + 1 }}</td>
              <td>第{{ row.periodNo }}节</td>
              <td>{{ formatTime(row.startTime) }}</td>
              <td>{{ formatTime(row.endTime) }}</td>
              <td>{{ row.remark }}</td>
              <td>
                <div class="op-btns">
                  <button type="button" class="link" @click="openEdit(row)">编辑</button>
                  <button type="button" class="link danger" @click="handleDelete(row)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <div class="list-summary" v-if="tableData.length > 0">
          共 {{ filteredData.length }} 条
        </div>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <Transition name="modal-fade">
      <div v-if="showDialog" class="modal-mask" @click.self="closeDialog">
        <div class="modal-box">
          <div class="modal-header">
            <h3>{{ editingId ? '编辑节次' : '新增节次' }}</h3>
            <button type="button" class="link" @click="closeDialog">✕</button>
          </div>
          <div class="modal-body">
            <div class="field">
              <label>节次序号</label>
              <input
                v-model="form.periodNo"
                type="number"
                min="1"
                step="1"
                placeholder="请输入节次序号"
                @keyup.enter="handleSave"
              />
            </div>
            <div class="field-row">
              <div class="field">
                <label>上课时间</label>
                <input v-model="form.startTime" type="time" />
              </div>
              <div class="field">
                <label>下课时间</label>
                <input v-model="form.endTime" type="time" />
              </div>
            </div>
            <div class="field">
              <label>备注</label>
              <input
                v-model="form.remark"
                type="text"
                placeholder="如：上午第1节"
                @keyup.enter="handleSave"
              />
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" @click="closeDialog">取消</button>
            <button type="button" class="primary" @click="handleSave">确定</button>
          </div>
        </div>
      </div>
    </Transition>
  </section>
</template>

<style scoped>
.search-bar {
  display: flex;
  gap: 6px;
}

.search-bar input {
  width: 190px;
}

.list-summary {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #eee;
  font-size: 13px;
  color: #666;
}

/* 弹窗 */
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-box {
  width: 420px;
  background: #fff;
  border: 1px solid #ddd;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  border-bottom: 1px solid #ddd;
}

.modal-header h3 {
  font-size: 15px;
}

.modal-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px 14px;
}

.field-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 10px 14px;
  border-top: 1px solid #eee;
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.18s ease;
}

.modal-fade-enter-active .modal-box,
.modal-fade-leave-active .modal-box {
  transition: opacity 0.18s ease, transform 0.18s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-from .modal-box,
.modal-fade-leave-to .modal-box {
  opacity: 0;
  transform: translateY(8px) scale(0.98);
}
</style>
