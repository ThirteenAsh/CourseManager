<script setup>
import { computed, onBeforeUnmount, onMounted, provide, ref } from 'vue'
import WelcomeView from './views/WelcomeView.vue'
import GradeView from './views/GradeView.vue'
import ClassesView from './views/ClassesView.vue'
import CoursesView from './views/CoursesView.vue'
import SemestersView from './views/SemestersView.vue'
import PeriodsView from './views/PeriodsView.vue'
import StudentsView from './views/StudentsView.vue'
import TeachersView from './views/TeachersView.vue'
import ClassCoursesView from './views/ClassCoursesView.vue'
import TimetablesView from './views/TimetablesView.vue'
import ProcedureToolsView from './views/ProcedureToolsView.vue'

const pages = [
  { key: 'welcome', label: '欢迎页', component: WelcomeView },
  { key: 'grades', label: '年级管理', component: GradeView },
  { key: 'classes', label: '班级管理', component: ClassesView },
  { key: 'courses', label: '课程管理', component: CoursesView },
  { key: 'teachers', label: '教师管理', component: TeachersView },
  { key: 'students', label: '学生管理', component: StudentsView },
  { key: 'semesters', label: '学期管理', component: SemestersView },
  { key: 'periods', label: '节次管理', component: PeriodsView },
  { key: 'class-courses', label: '任课安排', component: ClassCoursesView },
  { key: 'timetables', label: '排课管理', component: TimetablesView },
  { key: 'procedures', label: '课表生成', component: ProcedureToolsView },
]

const defaultPageKey = 'welcome'
const basicPageKeys = ['grades', 'classes', 'courses', 'teachers', 'students']
const notifications = ref([])
const confirmDialog = ref({
  visible: false,
  title: '',
  message: '',
  confirmText: '确定',
  cancelText: '取消',
  type: 'danger',
})
let notificationId = 0
let confirmResolver = null

function isBasicPageKey(key) {
  return basicPageKeys.includes(key)
}

function isValidPageKey(key) {
  return pages.some((page) => page.key === key)
}

function getRawPageKeyFromHash() {
  return window.location.hash.replace(/^#\/?/, '')
}

function getPageKeyFromHash() {
  const key = getRawPageKeyFromHash()
  return isValidPageKey(key) ? key : defaultPageKey
}

function syncPageFromUrl() {
  activeKey.value = getPageKeyFromHash()
  ensureValidHash()
  if (isBasicPageKey(activeKey.value)) {
    basicOpen.value = true
  }
}

function ensureValidHash() {
  if (!window.location.hash || !isValidPageKey(getRawPageKeyFromHash())) {
    window.history.replaceState(null, '', `#/${activeKey.value}`)
  }
}

function navigateTo(key) {
  if (!isValidPageKey(key)) return
  activeKey.value = key
  if (isBasicPageKey(key)) {
    basicOpen.value = true
  }
  if (window.location.hash !== `#/${key}`) {
    window.location.hash = `/${key}`
  }
}

function toggleBasicGroup() {
  basicOpen.value = !basicOpen.value
}

function notify(message, type = 'success') {
  const id = ++notificationId
  notifications.value.push({ id, message, type })
  window.setTimeout(() => {
    notifications.value = notifications.value.filter((item) => item.id !== id)
  }, 2600)
}

function confirmAction(options) {
  confirmDialog.value = {
    visible: true,
    title: options.title || '确认操作',
    message: options.message || '',
    confirmText: options.confirmText || '确定',
    cancelText: options.cancelText || '取消',
    type: options.type || 'danger',
  }

  return new Promise((resolve) => {
    confirmResolver = resolve
  })
}

function closeConfirm(result) {
  confirmDialog.value.visible = false
  if (confirmResolver) {
    confirmResolver(result)
    confirmResolver = null
  }
}

provide('notify', notify)
provide('confirmAction', confirmAction)

const activeKey = ref(getPageKeyFromHash())
const basicOpen = ref(true)
const activePage = computed(() => pages.find((p) => p.key === activeKey.value) ?? pages[0])
const welcomePage = computed(() => pages.find((page) => page.key === defaultPageKey))
const basicPages = computed(() => (
  basicPageKeys.map((key) => pages.find((page) => page.key === key)).filter(Boolean)
))
const otherPages = computed(() => pages.filter((page) => (
  page.key !== defaultPageKey && !isBasicPageKey(page.key)
)))
const basicGroupActive = computed(() => isBasicPageKey(activeKey.value))

onMounted(() => {
  ensureValidHash()
  window.addEventListener('hashchange', syncPageFromUrl)
})

onBeforeUnmount(() => {
  closeConfirm(false)
  window.removeEventListener('hashchange', syncPageFromUrl)
})
</script>

<template>
  <div class="app-shell">
    <aside class="sidebar">
      <button class="brand" type="button" @click="navigateTo(defaultPageKey)">
        排课管理系统
        <small>陕西省洛南中学</small>
      </button>

      <nav class="nav-group">
        <button
          v-if="welcomePage"
          type="button"
          :class="['nav-item', { active: welcomePage.key === activeKey }]"
          @click="navigateTo(welcomePage.key)"
        >
          {{ welcomePage.label }}
        </button>

        <div class="nav-section">
          <button
            type="button"
            :class="['nav-section-toggle', { active: basicGroupActive, open: basicOpen }]"
            :aria-expanded="basicOpen"
            @click="toggleBasicGroup"
          >
            <span>基础信息管理</span>
            <span class="nav-caret"></span>
          </button>
          <Transition name="nav-collapse">
            <div v-if="basicOpen" class="nav-subgroup">
              <button
                v-for="page in basicPages"
                :key="page.key"
                type="button"
                :class="['nav-item', 'nav-subitem', { active: page.key === activeKey }]"
                @click="navigateTo(page.key)"
              >
                {{ page.label }}
              </button>
            </div>
          </Transition>
        </div>

        <button
          v-for="page in otherPages"
          :key="page.key"
          type="button"
          :class="['nav-item', { active: page.key === activeKey }]"
          @click="navigateTo(page.key)"
        >
          {{ page.label }}
        </button>
      </nav>

      <div class="sidebar-footer">最牛逼的课表管理系统 v1.0</div>
    </aside>

    <main class="main">
      <Transition name="page-fade" mode="out-in">
        <component :is="activePage.component" :key="activePage.key" />
      </Transition>
    </main>

    <TransitionGroup name="toast" tag="div" class="toast-stack">
      <div
        v-for="item in notifications"
        :key="item.id"
        :class="['toast-message', `toast-${item.type}`]"
      >
        <span class="toast-dot"></span>
        <span>{{ item.message }}</span>
      </div>
    </TransitionGroup>

    <Transition name="confirm-fade">
      <div v-if="confirmDialog.visible" class="confirm-mask" @click.self="closeConfirm(false)">
        <div class="confirm-box">
          <div class="confirm-header">
            <h3>{{ confirmDialog.title }}</h3>
          </div>
          <div class="confirm-body">
            {{ confirmDialog.message }}
          </div>
          <div class="confirm-footer">
            <button type="button" @click="closeConfirm(false)">
              {{ confirmDialog.cancelText }}
            </button>
            <button
              type="button"
              :class="['confirm-action', `confirm-${confirmDialog.type}`]"
              @click="closeConfirm(true)"
            >
              {{ confirmDialog.confirmText }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>
