<template>
  <section class="page-section">
    <!-- 存储过程工具 -->
    <div class="three-grid">
      <div class="panel">
        <div class="panel-header">
          <h3>教师节次检测</h3>
        </div>
        <div class="panel-body">
          <div class="form-grid">
            <div class="field full">
              <label>指定教师</label>
              <select><option>请选择教师</option></select>
            </div>
            <div class="field">
              <label>星期</label>
              <select>
                <option>请选择</option>
                <option>周一</option>
                <option>周二</option>
                <option>周三</option>
                <option>周四</option>
                <option>周五</option>
              </select>
            </div>
            <div class="field">
              <label>节次</label>
              <select>
                <option>请选择</option>
                <option>第1节</option>
                <option>第2节</option>
                <option>第3节</option>
                <option>第4节</option>
                <option>第5节</option>
                <option>第6节</option>
                <option>第7节</option>
                <option>第8节</option>
              </select>
            </div>
          </div>
          <div class="form-actions">
            <button type="button" class="primary">检测</button>
          </div>
          <div class="placeholder mt-14">检测结果将在此显示</div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header">
          <h3>班级课程表</h3>
        </div>
        <div class="panel-body">
          <div class="form-grid">
            <div class="field full">
              <label>选择班级</label>
              <select><option>请选择班级</option></select>
            </div>
          </div>
          <div class="form-actions">
            <button type="button" class="primary">生成课表</button>
          </div>
          <div class="placeholder mt-14">班级课表将在此显示</div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header">
          <h3>教师课程表</h3>
        </div>
        <div class="panel-body">
          <div class="form-grid">
            <div class="field full">
              <label>选择教师</label>
              <select><option>请选择教师</option></select>
            </div>
          </div>
          <div class="form-actions">
            <button type="button" class="primary">生成课表</button>
          </div>
          <div class="placeholder mt-14">教师课表将在此显示</div>
        </div>
      </div>
    </div>

    <!-- 课表预览 -->
    <div class="panel">
      <div class="panel-header">
        <h3>课表预览</h3>
      </div>
      <div class="panel-body">
        <div class="table-wrap">
          <table class="schedule-table">
            <thead>
              <tr>
                <th>节次</th>
                <th>周一</th>
                <th>周二</th>
                <th>周三</th>
                <th>周四</th>
                <th>周五</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="period in 8" :key="period">
                <td>第{{ period }}节</td>
                <td><div class="empty-slot">--</div></td>
                <td><div class="empty-slot">--</div></td>
                <td><div class="empty-slot">--</div></td>
                <td><div class="empty-slot">--</div></td>
                <td><div class="empty-slot">--</div></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </section>
</template>
