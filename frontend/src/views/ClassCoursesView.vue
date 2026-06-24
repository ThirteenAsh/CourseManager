<template>
  <section class="page-section">
    <div class="page-title">
      <h2>任课安排</h2>
      <button type="button" class="primary">新增安排</button>
    </div>

    <div class="section-grid">
      <div class="panel">
        <div class="panel-header">
          <h3>任课数据</h3>
        </div>
        <div class="panel-body">
          <div class="filter-bar">
            <div class="field">
              <label>班级</label>
              <select><option>全部班级</option></select>
            </div>
            <div class="field">
              <label>课程</label>
              <select><option>全部课程</option></select>
            </div>
            <button type="button">查询</button>
          </div>

          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>班级</th>
                  <th>课程</th>
                  <th>任课教师</th>
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
          <h3>安排信息</h3>
        </div>
        <div class="panel-body">
          <div class="form-grid">
            <div class="field">
              <label>班级</label>
              <select><option>请选择班级</option></select>
            </div>
            <div class="field">
              <label>课程</label>
              <select><option>请选择课程</option></select>
            </div>
            <div class="field">
              <label>任课教师</label>
              <select><option>请选择教师</option></select>
            </div>
            <div class="field">
              <label>周课时</label>
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
