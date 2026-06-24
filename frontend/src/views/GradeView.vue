<script setup>
import { inject, ref, onMounted } from 'vue'
import { getGradeList, addGrade, updateGrade, deleteGrade } from '../api/grade'

// 列表
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const searchName = ref('')

// 弹窗
const showDialog = ref(false)
const formGradeName = ref('')
const editingId = ref(null) // null=新增
const notify = inject('notify', () => {})
const confirmAction = inject('confirmAction', async () => false)

function showToast(message, type = 'success') {
  notify(message, type)
}

function loadData() {
  return getGradeList({
    page: page.value,
    pageSize: pageSize.value,
    gradeName: searchName.value || undefined,
  }).then((data) => {
    tableData.value = data.records || []
    total.value = data.total || 0
    calcPages()
  }).catch((err) => {
    showToast('加载失败：' + err.message, 'error')
  })
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

// 新增
function openAdd() {
  editingId.value = null
  formGradeName.value = ''
  showDialog.value = true
}

// 编辑
function openEdit(row) {
  editingId.value = row.gradeId
  formGradeName.value = row.gradeName
  showDialog.value = true
}

function closeDialog() {
  showDialog.value = false
}

function handleSave() {
  const name = formGradeName.value.trim()
  if (!name) {
    showToast('请输入年级名称', 'error')
    return
  }
  const successMessage = editingId.value ? '修改年级成功' : '新增年级成功'
  const fn = editingId.value
    ? updateGrade(editingId.value, { gradeName: name })
    : addGrade({ gradeName: name })

  fn.then(() => {
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
    title: '删除年级',
    message: `确认删除「${row.gradeName}」？删除后不可恢复。`,
    confirmText: '删除',
    type: 'danger',
  })
  if (!confirmed) return
  deleteGrade(row.gradeId).then(() => {
    load()
    showToast('删除年级成功')
  }).catch((err) => {
    showToast(err.message, 'error')
  })
}

// 页码数组（最多显示5个页码）
const pageNumbers = ref([])

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

// 包装 loadData 同时计算页码
function load() {
  return loadData()
}

onMounted(load)
</script>

<template>
  <section class="page-section">
    <div class="page-title">
      <h2>年级管理</h2>
      <button type="button" class="primary" @click="openAdd">新增年级</button>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3>年级列表</h3>
        <div class="search-bar">
          <input
            v-model="searchName"
            type="text"
            placeholder="输入年级名称搜索"
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
              <th>年级名称</th>
              <th style="width: 140px">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="tableData.length === 0">
              <td colspan="3" class="empty-cell">暂无数据</td>
            </tr>
            <tr v-for="(row, idx) in tableData" :key="row.gradeId">
              <td>{{ (page - 1) * pageSize + idx + 1 }}</td>
              <td>{{ row.gradeName }}</td>
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
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <Transition name="modal-fade">
      <div v-if="showDialog" class="modal-mask" @click.self="closeDialog">
        <div class="modal-box">
          <div class="modal-header">
            <h3>{{ editingId ? '编辑年级' : '新增年级' }}</h3>
            <button type="button" class="link" @click="closeDialog">✕</button>
          </div>
          <div class="modal-body">
            <div class="field">
              <label>年级名称</label>
              <input
                v-model="formGradeName"
                type="text"
                placeholder="请输入年级名称"
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
  width: 180px;
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
  width: 360px;
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
  padding: 16px 14px;
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

