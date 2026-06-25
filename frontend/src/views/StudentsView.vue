<script setup>
import { inject, onMounted, ref } from 'vue'
import { getClassList } from '../api/schoolClass'
import { getStudentList, addStudent, updateStudent, deleteStudent } from '../api/student'

// 列表
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const searchStudentNo = ref('')
const searchStudentName = ref('')
const searchGender = ref('')
const searchClassId = ref('')

// 下拉数据
const classList = ref([])

// 弹窗
const showDialog = ref(false)
const editingId = ref(null) // null=新增
const form = ref({
  studentNo: '',
  studentName: '',
  gender: '',
  classId: '',
})

// 页码数组（最多显示5个页码）
const pageNumbers = ref([])
const notify = inject('notify', () => {})
const confirmAction = inject('confirmAction', async () => false)

function showToast(message, type = 'success') {
  notify(message, type)
}

function normalizeId(value) {
  return value === '' ? undefined : Number(value)
}

function loadOptions() {
  return getClassList({ page: 1, pageSize: 100 }).then((data) => {
    classList.value = data.records || []
  }).catch((err) => {
    showToast('班级数据加载失败：' + err.message, 'error')
  })
}

function loadData() {
  return getStudentList({
    page: page.value,
    pageSize: pageSize.value,
    studentNo: searchStudentNo.value || undefined,
    studentName: searchStudentName.value || undefined,
    gender: searchGender.value || undefined,
    classId: normalizeId(searchClassId.value),
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
    studentNo: '',
    studentName: '',
    gender: '',
    classId: '',
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
  editingId.value = row.studentId
  form.value = {
    studentNo: row.studentNo || '',
    studentName: row.studentName || '',
    gender: row.gender || '',
    classId: row.classId || '',
  }
  showDialog.value = true
}

function closeDialog() {
  showDialog.value = false
}

function handleSave() {
  const studentNo = form.value.studentNo.trim()
  const studentName = form.value.studentName.trim()
  const gender = form.value.gender
  const classId = Number(form.value.classId)

  if (!studentNo) {
    showToast('请输入学号', 'error')
    return
  }
  if (!studentName) {
    showToast('请输入学生姓名', 'error')
    return
  }
  if (!gender) {
    showToast('请选择性别', 'error')
    return
  }
  if (!form.value.classId) {
    showToast('请选择所属班级', 'error')
    return
  }

  const payload = { studentNo, studentName, gender, classId }
  const successMessage = editingId.value ? '修改学生成功' : '新增学生成功'
  const request = editingId.value
    ? updateStudent(editingId.value, payload)
    : addStudent(payload)

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
    title: '删除学生',
    message: `确认删除「${row.studentName}」？删除后不可恢复。`,
    confirmText: '删除',
    type: 'danger',
  })
  if (!confirmed) return
  deleteStudent(row.studentId).then(() => {
    load()
    showToast('删除学生成功')
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

onMounted(() => {
  loadOptions()
  load()
})
</script>

<template>
  <section class="page-section">
    <div class="page-title">
      <h2>学生管理</h2>
      <button type="button" class="primary" @click="openAdd">新增学生</button>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3>学生列表</h3>
        <div class="search-bar">
          <input
            v-model="searchStudentNo"
            type="text"
            placeholder="输入学号搜索"
            @keyup.enter="handleSearch"
          />
          <input
            v-model="searchStudentName"
            type="text"
            placeholder="输入姓名搜索"
            @keyup.enter="handleSearch"
          />
          <select v-model="searchGender" class="gender-filter" @change="handleSearch">
            <option value="">全部性别</option>
            <option value="男">男</option>
            <option value="女">女</option>
          </select>
          <select v-model="searchClassId" class="class-filter" @change="handleSearch">
            <option value="">全部班级</option>
            <option v-for="c in classList" :key="c.classId" :value="c.classId">
              {{ c.className }}
            </option>
          </select>
          <button type="button" @click="handleSearch">搜索</button>
        </div>
      </div>
      <div class="panel-body">
        <table>
          <thead>
            <tr>
              <th style="width: 60px">序号</th>
              <th>学号</th>
              <th>姓名</th>
              <th style="width: 100px">性别</th>
              <th>班级</th>
              <th style="width: 140px">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="tableData.length === 0">
              <td colspan="6" class="empty-cell">暂无数据</td>
            </tr>
            <tr v-for="(row, idx) in tableData" :key="row.studentId">
              <td>{{ (page - 1) * pageSize + idx + 1 }}</td>
              <td>{{ row.studentNo }}</td>
              <td>{{ row.studentName }}</td>
              <td>{{ row.gender }}</td>
              <td>{{ row.className }}</td>
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
            <h3>{{ editingId ? '编辑学生' : '新增学生' }}</h3>
            <button type="button" class="link" @click="closeDialog">✕</button>
          </div>
          <div class="modal-body">
            <div class="field">
              <label>学号</label>
              <input
                v-model="form.studentNo"
                type="text"
                placeholder="请输入学号"
                @keyup.enter="handleSave"
              />
            </div>
            <div class="field">
              <label>姓名</label>
              <input
                v-model="form.studentName"
                type="text"
                placeholder="请输入姓名"
                @keyup.enter="handleSave"
              />
            </div>
            <div class="field">
              <label>性别</label>
              <select v-model="form.gender">
                <option value="">请选择性别</option>
                <option value="男">男</option>
                <option value="女">女</option>
              </select>
            </div>
            <div class="field">
              <label>所属班级</label>
              <select v-model="form.classId">
                <option value="">请选择班级</option>
                <option v-for="c in classList" :key="c.classId" :value="c.classId">
                  {{ c.className }}
                </option>
              </select>
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
  width: 150px;
}

.gender-filter {
  width: 100px;
}

.class-filter {
  width: 140px;
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
  width: 400px;
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
