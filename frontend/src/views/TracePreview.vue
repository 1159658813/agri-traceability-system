<template>
  <div class="trace-page"><header class="trace-top"><div class="trace-brand"><div class="trace-mark">田</div><span>田野档案<small>农产品产地溯源</small></span></div><el-button type="text" icon="el-icon-back" @click="$router.push('/')">返回管理后台</el-button></header><main class="trace-body"><div class="trace-kicker">溯源查询</div><div class="trace-emblem"><i class="el-icon-grape"></i></div><div class="trace-border"><div class="dot top-left"></div><div class="dot top-right"></div><div class="dot bottom-left"></div><div class="dot bottom-right"></div><span>从田间，到餐桌</span><h1>追溯每一份<br>土地的馈赠</h1><p>生产批次、产地信息、农事记录和检测资料将在此汇集。</p><div class="input-caption">溯源编号查询</div><el-input value="" disabled placeholder="批次查询接口尚未接入" prefix-icon="el-icon-search"/><el-button disabled type="primary" class="query-button">查询溯源记录</el-button><div class="trace-notice"><i class="el-icon-info"></i> 页面为消费者端视觉预览，当前不提供真实查询结果。</div></div><div class="trace-steps"><div><span>01</span><b>产地档案</b><small>基地来源</small></div><div><span>02</span><b>生产记录</b><small>农事与采收</small></div><div><span>03</span><b>品质溯源</b><small>质检与流通</small></div></div></main><footer class="trace-footer">农产品产地溯源信息管理系统 · 页面预览</footer></div>
</template>
<script>export default { name:'TracePreview' };</script>
<style scoped>
.trace-page{background:#f5f5ed;min-height:100vh;color:#244838}.trace-top{max-width:1060px;margin:auto;padding:25px 23px;display:flex;align-items:center;justify-content:space-between}.trace-brand{display:flex;align-items:center;gap:10px;font-weight:750;letter-spacing:1px}.trace-mark{display:grid;place-items:center;width:38px;height:38px;background:#294e3b;color:#e4d2a3;font-size:18px;border-radius:10px}.trace-brand span{display:flex;flex-direction:column}.trace-brand small{letter-spacing:1px;font-size:8px;color:#95a898;margin-top:3px}.trace-body{max-width:590px;margin:30px auto 0;text-align:center;padding:0 20px}.trace-kicker{letter-spacing:2px;font-size:10px;color:#95a99a}.trace-emblem{font-size:44px;color:#6e976a;margin-top:21px}.trace-border{border:1px solid #d9e4d7;border-radius:180px 180px 16px 16px;position:relative;padding:50px 48px 39px;background:#fff;box-shadow:0 13px 43px rgba(52,86,60,.055);margin-top:15px}.trace-border>span{font-size:12px;letter-spacing:4px;color:#99aa91}.trace-border h1{font-family:Georgia,'Songti SC','SimSun',serif;font-size:37px;line-height:1.5;margin:25px 0 13px;color:#274d3b}.trace-border p{color:#9baca1;font-size:12px;line-height:2;margin:0 auto 31px;max-width:280px}.input-caption{font-size:11px;color:#6e8c77;text-align:left;margin-bottom:11px}.query-button{width:100%;margin-top:12px}.trace-notice{font-size:11px;color:#a6b4a8;margin-top:24px;line-height:1.8}.trace-steps{display:grid;grid-template-columns:repeat(3,1fr);margin:36px 0 60px;gap:12px}.trace-steps>div{display:flex;flex-direction:column;gap:6px}.trace-steps span{font:17px Georgia,serif;color:#c9ab79}.trace-steps b{font-size:12px;color:#3f5f49}.trace-steps small{font-size:10px;color:#9aab9d}.trace-footer{border-top:1px solid #e2e8de;padding:20px;text-align:center;color:#acbaaa;font-size:10px}@media(max-width:500px){.trace-body{margin-top:5px}.trace-border{padding:47px 24px 31px}.trace-border h1{font-size:31px}.trace-brand small{font-size:7px}}

/* V1.1 可读性调整：加大常用字号，保留现有页面布局 */

.trace-brand small { font-size: 12px; letter-spacing: .2px; }
.trace-kicker { font-size: 13px; letter-spacing: .4px; }
.trace-border > span { font-size: 14px; letter-spacing: 1.5px; }
.trace-border p { font-size: 14px; line-height: 1.85; max-width: 355px; }
.input-caption { font-size: 14px; }
.trace-notice { font-size: 13px; }
.trace-steps b { font-size: 14px; }
.trace-steps small { font-size: 12px; }
.trace-footer { font-size: 12px; }
@media (max-width: 500px) {
  .trace-brand small { font-size: 11px; }
}

</style>
