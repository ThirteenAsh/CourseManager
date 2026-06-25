<script setup>
import { computed, inject, onMounted, ref } from 'vue'
import { getSemesterList } from '../api/semester'
import { getClassList } from '../api/schoolClass'
import { getTeacherList } from '../api/teacher'
import { getPeriodList } from '../api/period'
import { getClassTimetable, getTeacherTimetable } from '../api/timetable'

const weekdays = [
  { value: 1, label: '星期一', short: '周一' },
  { value: 2, label: '星期二', short: '周二' },
  { value: 3, label: '星期三', short: '周三' },
  { value: 4, label: '星期四', short: '周四' },
  { value: 5, label: '星期五', short: '周五' },
  { value: 6, label: '星期六', short: '周六' },
  { value: 7, label: '星期日', short: '周日' },
]

const mode = ref('class')
const loadedMode = ref('class')
const semesterId = ref('')
const classId = ref('')
const teacherId = ref('')
const semesterList = ref([])
const classList = ref([])
const teacherList = ref([])
const periodList = ref([])
const tableRecords = ref([])
const tableTitle = ref('课表预览')
const tableSubtitle = ref('')
const hasGenerated = ref(false)
const loading = ref(false)

const notify = inject('notify', () => {})

const periodRows = computed(() => {
  const map = new Map()
  tableRecords.value.forEach((record) => {
    if (!record.periodNo) return
    map.set(record.periodNo, {
      periodNo: record.periodNo,
      startTime: record.startTime,
      endTime: record.endTime,
    })
  })
  if (map.size === 0) {
    periodList.value.forEach((period) => {
      map.set(period.periodNo, {
        periodNo: period.periodNo,
        startTime: period.startTime,
        endTime: period.endTime,
      })
    })
  }
  return Array.from(map.values()).sort((a, b) => a.periodNo - b.periodNo)
})

function showToast(message, type = 'success') {
  notify(message, type)
}

function getSemesterLabel(semester) {
  const termName = semester.termName || (semester.term === 1 ? '上学期' : semester.term === 2 ? '下学期' : '')
  return `${semester.schoolYear || ''}${termName ? ` ${termName}` : ''}`
}

function formatTime(value) {
  if (!value) return ''
  return String(value).slice(0, 5)
}

function getRecord(weekday, periodNo) {
  return tableRecords.value.find((record) => {
    return Number(record.weekday) === weekday && Number(record.periodNo) === periodNo
  }) || null
}

function hasCellContent(record) {
  if (!record) return false
  return Boolean(record.courseName || record.teacherName || record.className || record.classroom || record.remark)
}

function getCellSecondary(record) {
  if (!record) return ''
  return loadedMode.value === 'class' ? record.teacherName : record.className
}

function getSelectedSemesterName() {
  const semester = semesterList.value.find((item) => item.semesterId === Number(semesterId.value))
  return semester ? getSemesterLabel(semester) : ''
}

function loadOptions() {
  return Promise.all([
    getSemesterList({ page: 1, pageSize: 100 }),
    getClassList({ page: 1, pageSize: 200 }),
    getTeacherList({ page: 1, pageSize: 200 }),
    getPeriodList(),
  ]).then(([semesterData, classData, teacherData, periodData]) => {
    semesterList.value = semesterData.records || []
    classList.value = classData.records || []
    teacherList.value = teacherData.records || []
    periodList.value = periodData || []

    const currentSemester = semesterList.value.find((item) => item.isCurrent === 1)
    semesterId.value = currentSemester?.semesterId || semesterList.value[0]?.semesterId || ''
    classId.value = classList.value[0]?.classId || ''
    teacherId.value = teacherList.value[0]?.teacherId || ''
  }).catch((err) => {
    showToast('基础数据加载失败：' + err.message, 'error')
  })
}

function switchMode(nextMode) {
  if (mode.value === nextMode) return
  mode.value = nextMode
  if ((nextMode === 'class' && classId.value) || (nextMode === 'teacher' && teacherId.value)) {
    generateTable(true)
  }
}

function generateTable(silent = false) {
  if (!semesterId.value) {
    showToast('请选择学期', 'error')
    return Promise.resolve()
  }
  if (mode.value === 'class' && !classId.value) {
    showToast('请选择班级', 'error')
    return Promise.resolve()
  }
  if (mode.value === 'teacher' && !teacherId.value) {
    showToast('请选择教师', 'error')
    return Promise.resolve()
  }

  loading.value = true
  const params = { semesterId: Number(semesterId.value) }
  const request = mode.value === 'class'
    ? getClassTimetable({ ...params, classId: Number(classId.value) })
    : getTeacherTimetable({ ...params, teacherId: Number(teacherId.value) })

  return request.then((data) => {
    loadedMode.value = mode.value
    tableRecords.value = data.records || []
    hasGenerated.value = true

    const semesterName = getSelectedSemesterName()
    if (mode.value === 'class') {
      tableTitle.value = data.className || '班级课表'
      tableSubtitle.value = semesterName
    } else {
      tableTitle.value = data.teacherName || '教师课表'
      tableSubtitle.value = semesterName
    }
    if (!silent) {
      showToast(mode.value === 'class' ? '班级课表生成成功' : '教师课表生成成功')
    }
  }).catch((err) => {
    showToast(err.message, 'error')
  }).finally(() => {
    loading.value = false
  })
}

function handleGenerate() {
  generateTable(false)
}

onMounted(() => {
  loadOptions().then(() => {
    if (semesterId.value && classId.value) {
      generateTable(true)
    }
  })
})
</script>

<template>
  <section class="page-section">
    <div class="page-title">
      <div>
        <h2>课表生成</h2>
        <p>{{ tableSubtitle || '班级课表与教师课表' }}</p>
      </div>
      <button type="button" class="primary" :disabled="loading" @click="handleGenerate">
        {{ loading ? '生成中' : '生成课表' }}
      </button>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3>课表条件</h3>
        <div class="mode-tabs">
          <button
            type="button"
            :class="{ active: mode === 'class' }"
            @click="switchMode('class')"
          >
            班级课表
          </button>
          <button
            type="button"
            :class="{ active: mode === 'teacher' }"
            @click="switchMode('teacher')"
          >
            教师课表
          </button>
        </div>
      </div>
      <div class="panel-body">
        <div class="toolbar-grid">
          <div class="field">
            <label>学期</label>
            <select v-model="semesterId">
              <option value="">请选择学期</option>
              <option v-for="s in semesterList" :key="s.semesterId" :value="s.semesterId">
                {{ getSemesterLabel(s) }}
              </option>
            </select>
          </div>
          <div v-if="mode === 'class'" class="field">
            <label>班级</label>
            <select v-model="classId">
              <option value="">请选择班级</option>
              <option v-for="c in classList" :key="c.classId" :value="c.classId">
                {{ c.className }}
              </option>
            </select>
          </div>
          <div v-else class="field">
            <label>教师</label>
            <select v-model="teacherId">
              <option value="">请选择教师</option>
              <option v-for="t in teacherList" :key="t.teacherId" :value="t.teacherId">
                {{ t.teacherName }}
              </option>
            </select>
          </div>
          <div class="toolbar-actions">
            <button type="button" class="primary" :disabled="loading" @click="handleGenerate">
              {{ loading ? '生成中' : '生成课表' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="panel">
      <div class="panel-header">
        <div>
          <h3>{{ tableTitle }}</h3>
          <span class="muted">{{ loadedMode === 'class' ? '班级视图' : '教师视图' }}</span>
        </div>
      </div>
      <div class="panel-body">
        <div v-if="!hasGenerated" class="placeholder">暂无课表</div>
        <div v-else class="table-wrap schedule-wrap">
          <table class="visual-schedule-table">
            <thead>
              <tr>
                <th class="period-column">节次</th>
                <th v-for="weekday in weekdays" :key="weekday.value">
                  {{ weekday.short }}
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="period in periodRows" :key="period.periodNo">
                <td class="period-column">
                  <strong>第{{ period.periodNo }}节</strong>
                  <span v-if="period.startTime && period.endTime">
                    {{ formatTime(period.startTime) }}-{{ formatTime(period.endTime) }}
                  </span>
                </td>
                <td v-for="weekday in weekdays" :key="weekday.value">
                  <div
                    :class="[
                      'schedule-card',
                      { empty: !hasCellContent(getRecord(weekday.value, period.periodNo)) },
                    ]"
                  >
                    <template v-if="hasCellContent(getRecord(weekday.value, period.periodNo))">
                      <strong>{{ getRecord(weekday.value, period.periodNo).courseName }}</strong>
                      <span>{{ getCellSecondary(getRecord(weekday.value, period.periodNo)) }}</span>
                      <span>{{ getRecord(weekday.value, period.periodNo).classroom }}</span>
                      <small v-if="getRecord(weekday.value, period.periodNo).remark">
                        {{ getRecord(weekday.value, period.periodNo).remark }}
                      </small>
                    </template>
                    <template v-else>
                      <span>--</span>
                    </template>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.mode-tabs {
  display: flex;
  gap: 6px;
}

.mode-tabs button {
  min-width: 86px;
  padding: 5px 12px;
  min-height: auto;
}

.mode-tabs button.active {
  border-color: var(--primary);
  background: var(--primary);
  color: #fff;
}

.toolbar-grid {
  display: grid;
  grid-template-columns: minmax(180px, 260px) minmax(180px, 260px) auto;
  gap: 12px;
  align-items: flex-end;
}

.toolbar-actions {
  display: flex;
  justify-content: flex-start;
}

.schedule-wrap {
  border: 1px solid #eee;
}

.visual-schedule-table {
  min-width: 980px;
  table-layout: fixed;
}

.visual-schedule-table th {
  text-align: center;
}

.visual-schedule-table td {
  height: 104px;
  vertical-align: top;
  padding: 6px;
}

.visual-schedule-table .period-column {
  width: 112px;
  text-align: center;
  background: #fafafa;
}

.period-column strong,
.period-column span {
  display: block;
}

.period-column span {
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-light);
}

.schedule-card {
  min-height: 88px;
  height: 100%;
  padding: 8px;
  border: 1px solid #d8e6f7;
  background: #f7fbff;
  color: var(--text);
  overflow: hidden;
}

.schedule-card strong,
.schedule-card span,
.schedule-card small {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.schedule-card strong {
  font-size: 14px;
  margin-bottom: 4px;
}

.schedule-card span {
  font-size: 12px;
  color: var(--text-sub);
}

.schedule-card small {
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-light);
}

.schedule-card.empty {
  display: flex;
  align-items: center;
  justify-content: center;
  border-color: #eee;
  background: #fff;
  color: var(--text-light);
}

@media (max-width: 768px) {
  .toolbar-grid {
    grid-template-columns: 1fr;
  }
}
</style>
