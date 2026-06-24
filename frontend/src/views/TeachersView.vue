<template>
  <section class="page-section">
    <div class="page-title">
      <h2>教师列表</h2>
      <button type="button" class="primary">新增教师</button>
    </div>

    <div class="section-grid">
      <div class="panel">
        <div class="panel-header">
          <h3>教师数据</h3>
        </div>
        <div class="panel-body">
          <div class="filter-bar">
            <div class="field">
              <label>关键词</label>
              <input type="text" placeholder="姓名或工号" />
            </div>
            <button type="button">查询</button>
          </div>

          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>工号</th>
                  <th>姓名</th>
                  <th>主授学科</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td colspan="4" class="empty-cell">暂无数据</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header">
          <h3>教师信息</h3>
        </div>
        <div class="panel-body">
          <div class="form-grid">
            <div class="field">
              <label>工号</label>
              <input type="text" placeholder="如：T001" />
            </div>
            <div class="field">
              <label>姓名</label>
              <input type="text" placeholder="请输入姓名" />
            </div>
            <div class="field full">
              <label>主授学科</label>
              <select><option>请选择学科</option></select>
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
