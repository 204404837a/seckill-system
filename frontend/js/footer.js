Vue.component("footBar", {
  template: `
    <nav class="foot" aria-label="主导航">
    <a class="foot-box" href="/" :class="{active: activeBtn === 1}" :aria-current="activeBtn === 1 ? 'page' : null">
      <div class="foot-view"><i class="el-icon-s-home"></i></div>
      <div class="foot-text">首页</div>
    </a>
    <span class="foot-box foot-unavailable" aria-disabled="true" title="课程示例未提供地图功能">
      <div class="foot-view"><i class="el-icon-map-location"></i></div>
      <div class="foot-text">地图</div>
    </span>
    <a class="foot-box" href="/blog-edit.html" aria-label="发布笔记">
      <img class="add-btn" src="/imgs/add.png" alt="">
    </a>
    <span class="foot-box foot-unavailable" aria-disabled="true" title="课程示例未提供消息功能">
      <div class="foot-view"><i class="el-icon-chat-dot-round"></i></div>
      <div class="foot-text">消息</div>
    </span>
    <a class="foot-box" href="/info.html" :class="{active: activeBtn === 4}" :aria-current="activeBtn === 4 ? 'page' : null">
      <div class="foot-view"><i class="el-icon-user"></i></div>
      <div class="foot-text">我的</div>
    </a>
  </nav>
  `,
  props: ['activeBtn']
})
