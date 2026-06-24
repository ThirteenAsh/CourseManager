<script setup>
import { ref, onMounted } from 'vue'
import { getGradeList } from '../api/grade'

const gradeList = ref([])

onMounted(() => {
  getGradeList({ pageSize: 100 }).then((data) => {
    gradeList.value = data.records || []
  })
})
</script>

<template>
  <section class="page-section">
    <div class="page-title">
      <h2>课程列表</h2>
      <button type="button" class="primary">新增课程</button>
    </div>

    <div class="section-grid">
      <div class="panel">
        <div class="panel-header">
          <h3>课程数据</h3>
        </div>
        <div class="panel-body">
          <div class="filter-bar">
            <div class="field">
              <label>关键词</label>
              <input type="text" placeholder="课程编号或名称" />
            </div>
            <button type="button">查询</button>
          </div>

          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>课程编号</th>
                  <th>课程名称</th>
                  <th>适用年级</th>
                  <th>周课时</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td colspan="5" class="empty-cell">暂无数据</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header">
          <h3>课程信息</h3>
        </div>
        <div class="panel-body">
          <div class="form-grid">
            <div class="field">
              <label>课程编号</label>
              <input type="text" placeholder="如：MAT" />
            </div>
            <div class="field">
              <label>课程名称</label>
              <input type="text" placeholder="如：数学" />
            </div>
            <div class="field">
              <label>适用年级</label>
              <select>
                <option value="">请选择年级</option>
                <option v-for="g in gradeList" :key="g.gradeId" :value="g.gradeId">
                  {{ g.gradeName }}
                </option>
              </select>
            </div>
            <div class="field">
              <label>默认周课时</label>
              <input type="number" placeholder="如：5" />
            </div>
          </div>
          <div class="form-actions">
            <button type="button">重置</button>
            <button type="button" class="primary">保存</button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
