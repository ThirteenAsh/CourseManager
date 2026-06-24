<script setup>
import { computed, ref } from 'vue'
import DashboardView from './views/DashboardView.vue'
import GradeView from './views/GradeView.vue'
import ClassesView from './views/ClassesView.vue'
import CoursesView from './views/CoursesView.vue'
import StudentsView from './views/StudentsView.vue'
import TeachersView from './views/TeachersView.vue'
import ClassCoursesView from './views/ClassCoursesView.vue'
import ProcedureToolsView from './views/ProcedureToolsView.vue'

const pages = [
  { key: 'dashboard', label: '系统概览', component: DashboardView },
  { key: 'grades', label: '年级管理', component: GradeView },
  { key: 'classes', label: '班级管理', component: ClassesView },
  { key: 'courses', label: '课程管理', component: CoursesView },
  { key: 'students', label: '学生管理', component: StudentsView },
  { key: 'teachers', label: '教师管理', component: TeachersView },
  { key: 'class-courses', label: '任课安排', component: ClassCoursesView },
  { key: 'procedures', label: '课表与检测', component: ProcedureToolsView },
]

const activeKey = ref('dashboard')
const activePage = computed(() => pages.find((p) => p.key === activeKey.value) ?? pages[0])
</script>

<template>
  <div class="app-shell">
    <aside class="sidebar">
      <button class="brand" type="button" @click="activeKey = 'dashboard'">
        排课管理系统
        <small>某中学课程设计</small>
      </button>

      <nav class="nav-group">
        <button
          v-for="page in pages"
          :key="page.key"
          type="button"
          :class="['nav-item', { active: page.key === activeKey }]"
          @click="activeKey = page.key"
        >
          {{ page.label }}
        </button>
      </nav>

      <div class="sidebar-footer">静态原型 v1.0</div>
    </aside>

    <main class="main">
      <Transition name="page-fade" mode="out-in">
        <component :is="activePage.component" :key="activePage.key" />
      </Transition>
    </main>
  </div>
</template>
