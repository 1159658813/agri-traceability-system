import Vue from 'vue';
import VueRouter from 'vue-router';
import PublicLayout from '../layouts/PublicLayout.vue';
import Home from '../views/public/Home.vue';
import Search from '../views/public/Search.vue';
import Notices from '../views/public/Notices.vue';
import ComingSoon from '../views/public/ComingSoon.vue';

// 保留之前的后台页面，仅用于本地开发；正式上线前必须由真实认证保护。
import AppShell from '../components/AppShell.vue';
import Dashboard from '../views/Dashboard.vue';
import Bases from '../views/Bases.vue';
import ModulePending from '../views/ModulePending.vue';

Vue.use(VueRouter);

const router = new VueRouter({
  mode:'history',
  routes:[
    {path:'/',component:PublicLayout,children:[
      {path:'',name:'public-home',component:Home},
      {path:'search',name:'public-search',component:Search},
      {path:'notices',name:'notices',component:Notices},
      {path:'notices/:id',name:'notice-detail',component:Notices},
      {path:'farmer-apply',name:'farmer-apply',component:ComingSoon},
      {path:'login',name:'login',component:ComingSoon}
    ]},
    {path:'/manage',component:AppShell,meta:{devOnly:true},children:[
      {path:'',name:'dashboard',component:Dashboard},
      {path:'bases',name:'bases',component:Bases},
      {path:'module/:section',name:'module',component:ModulePending,props:true}
    ]},
    {path:'/bases',redirect:'/manage/bases'},
    {path:'/trace',redirect:'/search'},
    {path:'*',redirect:'/'}
  ],
  scrollBehavior(){return{x:0,y:0};}
});

// 不将开发后台发布为生产环境的无鉴权管理入口。
// 后续接入 Spring Security 后，应把这里改成按后端身份做的路由守卫。
router.beforeEach((to,from,next)=>{
  if(process.env.NODE_ENV==='production' && to.matched.some(item=>item.meta.devOnly)) next('/login');
  else next();
});

export default router;
