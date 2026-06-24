<template>
  <section class="page-section">
    <div class="stat-grid">
      <div class="stat-item">
        <div class="stat-label">班级数量</div>
        <div class="stat-value">--</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">课程数量</div>
        <div class="stat-value">--</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">教师数量</div>
        <div class="stat-value">--</div>
      </div>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3>排课流程</h3>
      </div>
      <div class="panel-body">
        <div class="workflow">
          <div class="workflow-step">
            <span>1</span>
            <h3>基础信息</h3>
            <p>录入班级、课程、学生、教师</p>
          </div>
          <div class="workflow-step">
            <span>2</span>
            <h3>任课安排</h3>
            <p>为班级指定课程和任课教师</p>
          </div>
          <div class="workflow-step">
            <span>3</span>
            <h3>生成课表</h3>
            <p>调用存储过程生成排课方案</p>
          </div>
          <div class="workflow-step">
            <span>4</span>
            <h3>查看课表</h3>
            <p>按班级或教师查看最终课表</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
