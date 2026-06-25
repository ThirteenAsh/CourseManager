<script setup>
import { inject, onMounted, ref } from 'vue'
import { getSemesterList, addSemester, updateSemester, deleteSemester } from '../api/semester'

// 列表
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const searchSchoolYear = ref('')
const searchTerm = ref('')
const searchIsCurrent = ref('')

// 弹窗
const showDialog = ref(false)
const editingId = ref(null) // null=新增
const form = ref({
  schoolYear: '',
  term: 1,
  startDate: '',
  endDate: '',
  isCurrent: 0,
})

// 页码数组（最多显示5个页码）
const pageNumbers = ref([])
const notify = inject('notify', () => {})
const confirmAction = inject('confirmAction', async () => false)

function showToast(message, type = 'success') {
  notify(message, type)
}

function normalizeNumber(value) {
  return value === '' ? undefined : Number(value)
}

function formatTerm(row) {
  if (row.termName) return row.termName
  if (row.term === 1) return '上学期'
  if (row.term === 2) return '下学期'
  return '-'
}

function formatCurrent(isCurrent) {
  return isCurrent === 1 ? '是' : '否'
}

function loadData() {
  return getSemesterList({
    page: page.value,
    pageSize: pageSize.value,
    schoolYear: searchSchoolYear.value.trim() || undefined,
    term: normalizeNumber(searchTerm.value),
    isCurrent: normalizeNumber(searchIsCurrent.value),
  }).then((data) => {
    tableData.value = data.records || []
    total.value = data.total || 0
    calcPages()
  }).catch((err) => {
    showToast('加载失败：' + err.message, 'error')
  })
}

function load() {
  return loadData()
}

function handleSearch() {
  page.value = 1
  load()
}

function handlePageChange(newPage) {
  page.value = newPage
  load()
}

function handleSizeChange(e) {
  pageSize.value = Number(e.target.value)
  page.value = 1
  load()
}

function resetForm() {
  form.value = {
    schoolYear: '',
    term: 1,
    startDate: '',
    endDate: '',
    isCurrent: 0,
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
  editingId.value = row.semesterId
  form.value = {
    schoolYear: row.schoolYear || '',
    term: row.term || 1,
    startDate: row.startDate || '',
    endDate: row.endDate || '',
    isCurrent: row.isCurrent ?? 0,
  }
  showDialog.value = true
}

function closeDialog() {
  showDialog.value = false
}

function handleSave() {
  const schoolYear = form.value.schoolYear.trim()
  const term = Number(form.value.term)
  const startDate = form.value.startDate
  const endDate = form.value.endDate
  const isCurrent = Number(form.value.isCurrent)

  if (!schoolYear) {
    showToast('请输入学年', 'error')
    return
  }
  if (term !== 1 && term !== 2) {
    showToast('请选择学期', 'error')
    return
  }
  if (!startDate) {
    showToast('请选择开始日期', 'error')
    return
  }
  if (!endDate) {
    showToast('请选择结束日期', 'error')
    return
  }
  if (endDate < startDate) {
    showToast('结束日期不能早于开始日期', 'error')
    return
  }

  const payload = { schoolYear, term, startDate, endDate, isCurrent }
  const successMessage = editingId.value ? '修改学期成功' : '新增学期成功'
  const request = editingId.value
    ? updateSemester(editingId.value, payload)
    : addSemester(payload)

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
    title: '删除学期',
    message: `确认删除「${row.schoolYear}${formatTerm(row)}」？删除后不可恢复。`,
    confirmText: '删除',
    type: 'danger',
  })
  if (!confirmed) return
  deleteSemester(row.semesterId).then(() => {
    load()
    showToast('删除学期成功')
  }).catch((err) => {
    showToast(err.message, 'error')
  })
}

function calcPages() {
  const totalPages = Math.ceil(total.value / pageSize.value)
  const cur = page.value
  let start = Math.max(1, cur - 2)
  let end = Math.min(totalPages, start + 4)
  if (end - start < 4) {
    start = Math.max(1, end - 4)
  }
  const arr = []
  for (let i = start; i <= end; i++) arr.push(i)
  pageNumbers.value = arr
}

onMounted(load)
</script>

<template>
  <section class="page-section">
    <div class="page-title">
      <h2>学期管理</h2>
      <button type="button" class="primary" @click="openAdd">新增学期</button>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3>学期列表</h3>
        <div class="search-bar">
          <input
            v-model="searchSchoolYear"
            type="text"
            placeholder="输入学年搜索"
            @keyup.enter="handleSearch"
          />
          <select v-model="searchTerm" class="term-filter" @change="handleSearch">
            <option value="">全部学期</option>
            <option :value="1">上学期</option>
            <option :value="2">下学期</option>
          </select>
          <select v-model="searchIsCurrent" class="current-filter" @change="handleSearch">
            <option value="">全部状态</option>
            <option :value="1">当前学期</option>
            <option :value="0">非当前</option>
          </select>
          <button type="button" @click="handleSearch">搜索</button>
        </div>
      </div>
      <div class="panel-body">
        <table>
          <thead>
            <tr>
              <th style="width: 60px">序号</th>
              <th>学年</th>
              <th style="width: 120px">学期</th>
              <th style="width: 140px">开始日期</th>
              <th style="width: 140px">结束日期</th>
              <th style="width: 110px">当前学期</th>
              <th style="width: 140px">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="tableData.length === 0">
              <td colspan="7" class="empty-cell">暂无数据</td>
            </tr>
            <tr v-for="(row, idx) in tableData" :key="row.semesterId">
              <td>{{ (page - 1) * pageSize + idx + 1 }}</td>
              <td>{{ row.schoolYear }}</td>
              <td>{{ formatTerm(row) }}</td>
              <td>{{ row.startDate }}</td>
              <td>{{ row.endDate }}</td>
              <td>
                <span :class="['status-text', { active: row.isCurrent === 1 }]">
                  {{ formatCurrent(row.isCurrent) }}
                </span>
              </td>
              <td>
                <div class="op-btns">
                  <button type="button" class="link" @click="openEdit(row)">编辑</button>
                  <button type="button" class="link danger" @click="handleDelete(row)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 分页 -->
        <div class="pager" v-if="total > 0">
          <div class="pager-info">
            共 {{ total }} 条，第 {{ page }}/{{ Math.ceil(total / pageSize) }} 页
          </div>
          <div class="pager-size">
            每页
            <select :value="pageSize" @change="handleSizeChange">
              <option :value="10">10</option>
              <option :value="20">20</option>
              <option :value="50">50</option>
            </select>
            条
          </div>
          <div class="pager-btns">
            <button
              type="button"
              :disabled="page <= 1"
              @click="handlePageChange(1)"
            >首页</button>
            <button
              type="button"
              :disabled="page <= 1"
              @click="handlePageChange(page - 1)"
            >上一页</button>
            <button
              v-for="p in pageNumbers"
              :key="p"
              type="button"
              :class="{ active: p === page }"
              @click="handlePageChange(p)"
            >{{ p }}</button>
            <button
              type="button"
              :disabled="page >= Math.ceil(total / pageSize)"
              @click="handlePageChange(page + 1)"
            >下一页</button>
            <button
              type="button"
              :disabled="page >= Math.ceil(total / pageSize)"
              @click="handlePageChange(Math.ceil(total / pageSize))"
            >尾页</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <Transition name="modal-fade">
      <div v-if="showDialog" class="modal-mask" @click.self="closeDialog">
        <div class="modal-box">
          <div class="modal-header">
            <h3>{{ editingId ? '编辑学期' : '新增学期' }}</h3>
            <button type="button" class="link" @click="closeDialog">✕</button>
          </div>
          <div class="modal-body">
            <div class="field">
              <label>学年</label>
              <input
                v-model="form.schoolYear"
                type="text"
                placeholder="如：2025-2026"
                @keyup.enter="handleSave"
              />
            </div>
            <div class="field-row">
              <div class="field">
                <label>学期</label>
                <select v-model="form.term">
                  <option :value="1">上学期</option>
                  <option :value="2">下学期</option>
                </select>
              </div>
              <div class="field">
                <label>当前学期</label>
                <select v-model="form.isCurrent">
                  <option :value="0">否</option>
                  <option :value="1">是</option>
                </select>
              </div>
            </div>
            <div class="field-row">
              <div class="field">
                <label>开始日期</label>
                <input v-model="form.startDate" type="date" />
              </div>
              <div class="field">
                <label>结束日期</label>
                <input v-model="form.endDate" type="date" />
              </div>
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
  width: 170px;
}

.term-filter,
.current-filter {
  width: 110px;
}

.status-text {
  color: var(--text-sub);
}

.status-text.active {
  color: #2f9e44;
  font-weight: bold;
}

/* 分页 */
.pager {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #eee;
  font-size: 13px;
  color: #666;
}

.pager-size select {
  width: auto;
  display: inline-block;
  padding: 2px 6px;
  min-height: auto;
}

.pager-btns {
  display: flex;
  gap: 4px;
  margin-left: auto;
}

.pager-btns button {
  min-width: 32px;
  padding: 4px 8px;
  font-size: 13px;
  min-height: auto;
}

.pager-btns button.active {
  background: var(--primary);
  color: #fff;
  border-color: var(--primary);
}

.pager-btns button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
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
  width: 460px;
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
