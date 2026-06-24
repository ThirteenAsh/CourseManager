<template>
  <section class="page-section">
    <div class="page-title">
      <h2>学生列表</h2>
      <button type="button" class="primary">新增学生</button>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3>学生数据</h3>
      </div>
      <div class="panel-body">
        <div class="filter-bar">
          <div class="field">
            <label>所属班级</label>
            <select><option>全部班级</option></select>
          </div>
          <div class="field">
            <label>关键词</label>
            <input type="text" placeholder="姓名或学号" />
          </div>
          <button type="button">查询</button>
        </div>

        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>学号</th>
                <th>姓名</th>
                <th>性别</th>
                <th>班级</th>
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
        <h3>学生信息</h3>
      </div>
      <div class="panel-body">
        <div class="form-grid four">
          <div class="field">
            <label>学号</label>
            <input type="text" placeholder="如：20240101" />
          </div>
          <div class="field">
            <label>姓名</label>
            <input type="text" placeholder="请输入姓名" />
          </div>
          <div class="field">
            <label>性别</label>
            <select>
              <option>请选择</option>
              <option>男</option>
              <option>女</option>
            </select>
          </div>
          <div class="field">
            <label>所属班级</label>
            <select><option>请选择班级</option></select>
          </div>
        </div>
        <div class="form-actions">
          <button type="button">重置</button>
          <button type="button" class="primary">保存</button>
        </div>
      </div>
    </div>
  </section>
</template>
