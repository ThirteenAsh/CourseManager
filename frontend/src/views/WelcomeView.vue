<template>
  <section class="page-section welcome-page">
    <div class="welcome-panel">
      <p class="welcome-kicker">Welcome</p>
      <h2>欢迎使用排课管理系统</h2>
      <p class="welcome-text">请从左侧菜单选择需要管理的模块。</p>
    </div>
  </section>
</template>

<style scoped>
.welcome-page {
  min-height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.welcome-panel {
  width: min(680px, 100%);
  padding: 0 24px;
  text-align: center;
}

.welcome-kicker {
  margin: 0 0 12px;
  color: var(--primary);
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 0;
}

.welcome-panel h2 {
  margin: 0;
  color: #1f2d3d;
  font-size: 28px;
  font-weight: 700;
}

.welcome-text {
  margin: 16px 0 0;
  color: #697386;
  font-size: 15px;
}

@media (max-width: 720px) {
  .welcome-panel {
    padding: 0 20px;
  }

  .welcome-panel h2 {
    font-size: 24px;
  }
}
</style>
