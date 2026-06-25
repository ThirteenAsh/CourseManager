<script setup>
import { inject, onMounted, ref } from 'vue'
import { getClassList } from '../api/schoolClass'
import { getCourseList } from '../api/course'
import { getTeacherList } from '../api/teacher'
import { getSemesterList } from '../api/semester'
import {
  getClassCourseTeacherList,
  addClassCourseTeacher,
  updateClassCourseTeacher,
  deleteClassCourseTeacher,
} from '../api/classCourseTeacher'

// 列表
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const searchSemesterId = ref('')
const searchClassId = ref('')
const searchCourseId = ref('')
const searchTeacherId = ref('')

// 下拉数据
const semesterList = ref([])
const classList = ref([])
const courseList = ref([])
const teacherList = ref([])

// 弹窗
const showDialog = ref(false)
const editingId = ref(null) // null=新增
const form = ref({
  semesterId: '',
  classId: '',
  courseId: '',
  teacherId: '',
  weeklyHours: '',
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

function formatSemester(row) {
  const schoolYear = row.schoolYear || ''
  const termName = row.termName || (row.term === 1 ? '上学期' : row.term === 2 ? '下学期' : '')
  return `${schoolYear}${termName ? ` ${termName}` : ''}` || '-'
}

function getSemesterLabel(semester) {
  const termName = semester.termName || (semester.term === 1 ? '上学期' : semester.term === 2 ? '下学期' : '')
  return `${semester.schoolYear || ''}${termName ? ` ${termName}` : ''}`
}

function loadOptions() {
  return Promise.all([
    getSemesterList({ page: 1, pageSize: 100 }),
    getClassList({ page: 1, pageSize: 200 }),
    getCourseList({ page: 1, pageSize: 200 }),
    getTeacherList({ page: 1, pageSize: 200 }),
  ]).then(([semesterData, classData, courseData, teacherData]) => {
    semesterList.value = semesterData.records || []
    classList.value = classData.records || []
    courseList.value = courseData.records || []
    teacherList.value = teacherData.records || []
  }).catch((err) => {
    showToast('下拉数据加载失败：' + err.message, 'error')
  })
}

function loadData() {
  return getClassCourseTeacherList({
    page: page.value,
    pageSize: pageSize.value,
    semesterId: normalizeId(searchSemesterId.value),
    classId: normalizeId(searchClassId.value),
    courseId: normalizeId(searchCourseId.value),
    teacherId: normalizeId(searchTeacherId.value),
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
    semesterId: '',
    classId: '',
    courseId: '',
    teacherId: '',
    weeklyHours: '',
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
  editingId.value = row.cctId
  form.value = {
    semesterId: row.semesterId || '',
    classId: row.classId || '',
    courseId: row.courseId || '',
    teacherId: row.teacherId || '',
    weeklyHours: row.weeklyHours || '',
  }
  showDialog.value = true
}

function closeDialog() {
  showDialog.value = false
}

function handleSave() {
  const semesterId = Number(form.value.semesterId)
  const classId = Number(form.value.classId)
  const courseId = Number(form.value.courseId)
  const teacherId = Number(form.value.teacherId)
  const weeklyHours = Number(form.value.weeklyHours)

  if (!form.value.semesterId) {
    showToast('请选择学期', 'error')
    return
  }
  if (!form.value.classId) {
    showToast('请选择班级', 'error')
    return
  }
  if (!form.value.courseId) {
    showToast('请选择课程', 'error')
    return
  }
  if (!form.value.teacherId) {
    showToast('请选择任课教师', 'error')
    return
  }
  if (!Number.isInteger(weeklyHours) || weeklyHours <= 0) {
    showToast('请输入大于0的周课时', 'error')
    return
  }

  const payload = { semesterId, classId, courseId, teacherId, weeklyHours }
  const successMessage = editingId.value ? '修改任课关系成功' : '新增任课关系成功'
  const request = editingId.value
    ? updateClassCourseTeacher(editingId.value, payload)
    : addClassCourseTeacher(payload)

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
    title: '删除任课关系',
    message: `确认删除「${row.className} - ${row.courseName}」的任课关系？删除后不可恢复。`,
    confirmText: '删除',
    type: 'danger',
  })
  if (!confirmed) return
  deleteClassCourseTeacher(row.cctId).then(() => {
    load()
    showToast('删除任课关系成功')
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
      <h2>班级课程任课管理</h2>
      <button type="button" class="primary" @click="openAdd">新增任课关系</button>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3>任课关系列表</h3>
        <div class="search-bar">
          <select v-model="searchSemesterId" class="semester-filter" @change="handleSearch">
            <option value="">全部学期</option>
            <option v-for="s in semesterList" :key="s.semesterId" :value="s.semesterId">
              {{ getSemesterLabel(s) }}
            </option>
          </select>
          <select v-model="searchClassId" class="class-filter" @change="handleSearch">
            <option value="">全部班级</option>
            <option v-for="c in classList" :key="c.classId" :value="c.classId">
              {{ c.className }}
            </option>
          </select>
          <select v-model="searchCourseId" class="course-filter" @change="handleSearch">
            <option value="">全部课程</option>
            <option v-for="c in courseList" :key="c.courseId" :value="c.courseId">
              {{ c.courseName }}
            </option>
          </select>
          <select v-model="searchTeacherId" class="teacher-filter" @change="handleSearch">
            <option value="">全部教师</option>
            <option v-for="t in teacherList" :key="t.teacherId" :value="t.teacherId">
              {{ t.teacherName }}
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
              <th style="width: 160px">学期</th>
              <th>班级</th>
              <th>课程</th>
              <th>任课教师</th>
              <th style="width: 90px">周课时</th>
              <th style="width: 140px">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="tableData.length === 0">
              <td colspan="7" class="empty-cell">暂无数据</td>
            </tr>
            <tr v-for="(row, idx) in tableData" :key="row.cctId">
              <td>{{ (page - 1) * pageSize + idx + 1 }}</td>
              <td>{{ formatSemester(row) }}</td>
              <td>{{ row.className }}</td>
              <td>
                <span>{{ row.courseName }}</span>
                <span v-if="row.courseCode" class="muted-code">{{ row.courseCode }}</span>
              </td>
              <td>
                <span>{{ row.teacherName }}</span>
                <span v-if="row.teacherNo" class="muted-code">{{ row.teacherNo }}</span>
              </td>
              <td>{{ row.weeklyHours }}</td>
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
            <h3>{{ editingId ? '编辑任课关系' : '新增任课关系' }}</h3>
            <button type="button" class="link" @click="closeDialog">✕</button>
          </div>
          <div class="modal-body">
            <div class="field">
              <label>学期</label>
              <select v-model="form.semesterId">
                <option value="">请选择学期</option>
                <option v-for="s in semesterList" :key="s.semesterId" :value="s.semesterId">
                  {{ getSemesterLabel(s) }}
                </option>
              </select>
            </div>
            <div class="field-row">
              <div class="field">
                <label>班级</label>
                <select v-model="form.classId">
                  <option value="">请选择班级</option>
                  <option v-for="c in classList" :key="c.classId" :value="c.classId">
                    {{ c.className }}
                  </option>
                </select>
              </div>
              <div class="field">
                <label>课程</label>
                <select v-model="form.courseId">
                  <option value="">请选择课程</option>
                  <option v-for="c in courseList" :key="c.courseId" :value="c.courseId">
                    {{ c.courseName }}
                  </option>
                </select>
              </div>
            </div>
            <div class="field-row">
              <div class="field">
                <label>任课教师</label>
                <select v-model="form.teacherId">
                  <option value="">请选择教师</option>
                  <option v-for="t in teacherList" :key="t.teacherId" :value="t.teacherId">
                    {{ t.teacherName }}
                  </option>
                </select>
              </div>
              <div class="field">
                <label>周课时</label>
                <input
                  v-model="form.weeklyHours"
                  type="number"
                  min="1"
                  step="1"
                  placeholder="如：5"
                  @keyup.enter="handleSave"
                />
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

.semester-filter {
  width: 160px;
}

.class-filter,
.course-filter,
.teacher-filter {
  width: 120px;
}

.muted-code {
  display: inline-block;
  margin-left: 6px;
  font-size: 12px;
  color: var(--text-light);
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
  width: 520px;
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
