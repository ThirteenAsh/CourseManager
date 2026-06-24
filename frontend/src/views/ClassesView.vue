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
      <h2>班级列表</h2>
      <button type="button" class="primary">新增班级</button>
    </div>

    <div class="section-grid">
      <div class="panel">
        <div class="panel-header">
          <h3>班级数据</h3>
        </div>
        <div class="panel-body">
          <div class="filter-bar">
            <div class="field">
              <label>年级</label>
              <select>
                <option value="">全部年级</option>
                <option v-for="g in gradeList" :key="g.gradeId" :value="g.gradeId">
                  {{ g.gradeName }}
                </option>
              </select>
            </div>
            <div class="field">
              <label>关键词</label>
              <input type="text" placeholder="班级名称或班主任" />
            </div>
            <button type="button">查询</button>
          </div>

          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>班级编号</th>
                  <th>班级名称</th>
                  <th>年级</th>
                  <th>班主任</th>
                  <th>教室</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td colspan="6" class="empty-cell">暂无数据</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header">
          <h3>班级信息</h3>
        </div>
        <div class="panel-body">
          <div class="form-grid">
            <div class="field">
              <label>班级编号</label>
              <input type="text" placeholder="如：202401" />
            </div>
            <div class="field">
              <label>班级名称</label>
              <input type="text" placeholder="如：高一(1)班" />
            </div>
            <div class="field">
              <label>所属年级</label>
              <select>
                <option value="">请选择年级</option>
                <option v-for="g in gradeList" :key="g.gradeId" :value="g.gradeId">
                  {{ g.gradeName }}
                </option>
              </select>
            </div>
            <div class="field">
              <label>固定教室</label>
              <input type="text" placeholder="如：A-301" />
            </div>
            <div class="field full">
              <label>班主任</label>
              <select><option>请选择教师</option></select>
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
