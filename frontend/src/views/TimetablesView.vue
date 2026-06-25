<script setup>
import { computed, inject, onMounted, ref } from 'vue'
import { getClassList } from '../api/schoolClass'
import { getCourseList } from '../api/course'
import { getTeacherList } from '../api/teacher'
import { getSemesterList } from '../api/semester'
import { getPeriodList } from '../api/period'
import { getClassCourseTeacherList } from '../api/classCourseTeacher'
import { getTimetableList, addTimetable, updateTimetable, deleteTimetable } from '../api/timetable'

const weekdays = [
  { value: 1, label: '星期一' },
  { value: 2, label: '星期二' },
  { value: 3, label: '星期三' },
  { value: 4, label: '星期四' },
  { value: 5, label: '星期五' },
  { value: 6, label: '星期六' },
  { value: 7, label: '星期日' },
]

// 列表
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const searchSemesterId = ref('')
const searchClassId = ref('')
const searchTeacherId = ref('')
const searchCourseId = ref('')
const searchWeekday = ref('')
const searchPeriodId = ref('')

// 下拉数据
const semesterList = ref([])
const classList = ref([])
const courseList = ref([])
const teacherList = ref([])
const periodList = ref([])
const cctList = ref([])

// 弹窗
const showDialog = ref(false)
const editingId = ref(null) // null=新增
const form = ref({
  cctId: '',
  weekday: '',
  periodId: '',
  classroom: '',
  remark: '正常排课',
})

// 页码数组（最多显示5个页码）
const pageNumbers = ref([])
const notify = inject('notify', () => {})
const confirmAction = inject('confirmAction', async () => false)

const filteredCctList = computed(() => {
  return cctList.value.filter((item) => {
    if (searchSemesterId.value && item.semesterId !== Number(searchSemesterId.value)) return false
    if (searchClassId.value && item.classId !== Number(searchClassId.value)) return false
    if (searchCourseId.value && item.courseId !== Number(searchCourseId.value)) return false
    if (searchTeacherId.value && item.teacherId !== Number(searchTeacherId.value)) return false
    return true
  })
})

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
  return formatSemester(semester)
}

function getWeekdayName(value) {
  return weekdays.find((item) => item.value === Number(value))?.label || '-'
}

function formatTime(value) {
  if (!value) return '-'
  return String(value).slice(0, 5)
}

function formatPeriod(period) {
  const timeText = period.startTime && period.endTime
    ? ` ${formatTime(period.startTime)}-${formatTime(period.endTime)}`
    : ''
  return `第${period.periodNo}节${timeText}`
}

function formatCct(item) {
  return `${formatSemester(item)} / ${item.className} / ${item.courseName} / ${item.teacherName}`
}

function loadOptions() {
  return Promise.all([
    getSemesterList({ page: 1, pageSize: 100 }),
    getClassList({ page: 1, pageSize: 200 }),
    getCourseList({ page: 1, pageSize: 200 }),
    getTeacherList({ page: 1, pageSize: 200 }),
    getPeriodList(),
    getClassCourseTeacherList({ page: 1, pageSize: 1000 }),
  ]).then(([semesterData, classData, courseData, teacherData, periodData, cctData]) => {
    semesterList.value = semesterData.records || []
    classList.value = classData.records || []
    courseList.value = courseData.records || []
    teacherList.value = teacherData.records || []
    periodList.value = periodData || []
    cctList.value = cctData.records || []
  }).catch((err) => {
    showToast('下拉数据加载失败：' + err.message, 'error')
  })
}

function loadData() {
  return getTimetableList({
    page: page.value,
    pageSize: pageSize.value,
    semesterId: normalizeId(searchSemesterId.value),
    classId: normalizeId(searchClassId.value),
    teacherId: normalizeId(searchTeacherId.value),
    courseId: normalizeId(searchCourseId.value),
    weekday: normalizeId(searchWeekday.value),
    periodId: normalizeId(searchPeriodId.value),
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
    cctId: '',
    weekday: '',
    periodId: '',
    classroom: '',
    remark: '正常排课',
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
  editingId.value = row.timetableId
  form.value = {
    cctId: row.cctId || '',
    weekday: row.weekday || '',
    periodId: row.periodId || '',
    classroom: row.classroom || '',
    remark: row.remark || '',
  }
  showDialog.value = true
}

function closeDialog() {
  showDialog.value = false
}

function handleSave() {
  const cctId = Number(form.value.cctId)
  const weekday = Number(form.value.weekday)
  const periodId = Number(form.value.periodId)
  const classroom = form.value.classroom.trim()
  const remark = form.value.remark.trim()

  if (!form.value.cctId) {
    showToast('请选择任课关系', 'error')
    return
  }
  if (weekday < 1 || weekday > 7) {
    showToast('请选择星期', 'error')
    return
  }
  if (!form.value.periodId) {
    showToast('请选择节次', 'error')
    return
  }
  if (!classroom) {
    showToast('请输入教室', 'error')
    return
  }
  if (!remark) {
    showToast('请输入备注', 'error')
    return
  }

  const payload = { cctId, weekday, periodId, classroom, remark }
  const successMessage = editingId.value ? '修改排课记录成功' : '新增排课记录成功'
  const request = editingId.value
    ? updateTimetable(editingId.value, payload)
    : addTimetable(payload)

  request.then((data) => {
    closeDialog()
    load()
    showToast(data?.message || successMessage)
  }).catch((err) => {
    showToast(err.message, 'error')
  })
}

// 删除
async function handleDelete(row) {
  const confirmed = await confirmAction({
    title: '删除排课记录',
    message: `确认删除「${row.className} ${row.courseName} ${row.weekdayName}第${row.periodNo}节」？删除后不可恢复。`,
    confirmText: '删除',
    type: 'danger',
  })
  if (!confirmed) return
  deleteTimetable(row.timetableId).then(() => {
    load()
    showToast('删除排课记录成功')
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
      <h2>排课管理</h2>
      <button type="button" class="primary" @click="openAdd">新增排课</button>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3>排课记录</h3>
        <div class="search-bar">
          <select v-model="searchSemesterId" class="semester-filter" @change="handleSearch">
            <option value="">全部学期</option>
            <option v-for="s in semesterList" :key="s.semesterId" :value="s.semesterId">
              {{ getSemesterLabel(s) }}
            </option>
          </select>
          <select v-model="searchClassId" class="normal-filter" @change="handleSearch">
            <option value="">全部班级</option>
            <option v-for="c in classList" :key="c.classId" :value="c.classId">
              {{ c.className }}
            </option>
          </select>
          <select v-model="searchCourseId" class="normal-filter" @change="handleSearch">
            <option value="">全部课程</option>
            <option v-for="c in courseList" :key="c.courseId" :value="c.courseId">
              {{ c.courseName }}
            </option>
          </select>
          <select v-model="searchTeacherId" class="normal-filter" @change="handleSearch">
            <option value="">全部教师</option>
            <option v-for="t in teacherList" :key="t.teacherId" :value="t.teacherId">
              {{ t.teacherName }}
            </option>
          </select>
          <select v-model="searchWeekday" class="small-filter" @change="handleSearch">
            <option value="">全部星期</option>
            <option v-for="w in weekdays" :key="w.value" :value="w.value">
              {{ w.label }}
            </option>
          </select>
          <select v-model="searchPeriodId" class="small-filter" @change="handleSearch">
            <option value="">全部节次</option>
            <option v-for="p in periodList" :key="p.periodId" :value="p.periodId">
              第{{ p.periodNo }}节
            </option>
          </select>
          <button type="button" @click="handleSearch">搜索</button>
        </div>
      </div>
      <div class="panel-body">
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th style="width: 60px">序号</th>
                <th style="width: 150px">学期</th>
                <th>班级</th>
                <th>课程</th>
                <th>教师</th>
                <th style="width: 90px">星期</th>
                <th style="width: 90px">节次</th>
                <th style="width: 120px">时间</th>
                <th style="width: 120px">教室</th>
                <th>备注</th>
                <th style="width: 140px">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="tableData.length === 0">
                <td colspan="11" class="empty-cell">暂无数据</td>
              </tr>
              <tr v-for="(row, idx) in tableData" :key="row.timetableId">
                <td>{{ (page - 1) * pageSize + idx + 1 }}</td>
                <td>{{ formatSemester(row) }}</td>
                <td>{{ row.className }}</td>
                <td>{{ row.courseName }}</td>
                <td>{{ row.teacherName }}</td>
                <td>{{ row.weekdayName || getWeekdayName(row.weekday) }}</td>
                <td>第{{ row.periodNo }}节</td>
                <td>{{ formatTime(row.startTime) }}-{{ formatTime(row.endTime) }}</td>
                <td>{{ row.classroom }}</td>
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
        </div>

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
            <h3>{{ editingId ? '编辑排课' : '新增排课' }}</h3>
            <button type="button" class="link" @click="closeDialog">✕</button>
          </div>
          <div class="modal-body">
            <div class="field">
              <label>任课关系</label>
              <select v-model="form.cctId">
                <option value="">请选择任课关系</option>
                <option v-for="item in filteredCctList" :key="item.cctId" :value="item.cctId">
                  {{ formatCct(item) }}
                </option>
              </select>
            </div>
            <div class="field-row">
              <div class="field">
                <label>星期</label>
                <select v-model="form.weekday">
                  <option value="">请选择星期</option>
                  <option v-for="w in weekdays" :key="w.value" :value="w.value">
                    {{ w.label }}
                  </option>
                </select>
              </div>
              <div class="field">
                <label>节次</label>
                <select v-model="form.periodId">
                  <option value="">请选择节次</option>
                  <option v-for="p in periodList" :key="p.periodId" :value="p.periodId">
                    {{ formatPeriod(p) }}
                  </option>
                </select>
              </div>
            </div>
            <div class="field-row">
              <div class="field">
                <label>教室</label>
                <input
                  v-model="form.classroom"
                  type="text"
                  placeholder="如：教学楼A101"
                  @keyup.enter="handleSave"
                />
              </div>
              <div class="field">
                <label>备注</label>
                <input
                  v-model="form.remark"
                  type="text"
                  placeholder="如：正常排课"
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
  flex-wrap: wrap;
  justify-content: flex-end;
}

.semester-filter {
  width: 150px;
}

.normal-filter {
  width: 110px;
}

.small-filter {
  width: 100px;
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
  width: 560px;
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
