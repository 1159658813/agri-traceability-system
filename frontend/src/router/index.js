import Vue from 'vue';
import VueRouter from 'vue-router';
import PublicLayout from '../layouts/PublicLayout.vue';
import Home from '../views/public/Home.vue';
import { checkManageAccess } from './authGuard';
const Search = () => import(/* webpackChunkName: "public-pages" */ '../views/public/Search.vue');
const Notices = () => import(/* webpackChunkName: "public-pages" */ '../views/public/Notices.vue');
const ComingSoon = () => import(/* webpackChunkName: "public-pages" */ '../views/public/ComingSoon.vue');
const AppShell = () => import(/* webpackChunkName: "manage-pages" */ '../components/AppShell.vue');
const Dashboard = () => import(/* webpackChunkName: "manage-pages" */ '../views/Dashboard.vue');
const Bases = () => import(/* webpackChunkName: "manage-pages" */ '../views/Bases.vue');
const ModulePending = () => import(/* webpackChunkName: "manage-pages" */ '../views/ModulePending.vue');
const Login = () => import('../views/public/Login.vue');
const FarmerReview = () => import(
    /* webpackChunkName: "manage-pages" */
    '../views/FarmerReview.vue'
    );
const FarmerApply = () => import(
    /* webpackChunkName: "public-pages" */
    '../views/public/FarmerApply.vue'
    );
const FarmerWorkspace = () => import(
    /* webpackChunkName: "farmer-pages" */
    '../views/FarmerWorkspace.vue'
    );

Vue.use(VueRouter);

const router = new VueRouter({
  mode:'history',
  routes:[
    {
      path: '/farmer',
      name: 'FarmerWorkspace',
      component: FarmerWorkspace
    },
    {path:'/',component:PublicLayout,children:[
      {path:'',name:'public-home',component:Home},
      {path:'search',name:'public-search',component:Search},
      {path:'notices',name:'notices',component:Notices},
      {path:'notices/:id',name:'notice-detail',component:Notices},
      {path:'farmer-apply',name:'farmer-apply',component:FarmerApply},
      {path:'login',name:'login',component:Login}
    ]},
    {path:'/manage',component:AppShell,meta:{devOnly:true},children:[
      {path:'',name:'dashboard',component:Dashboard},
      {path:'bases',name:'bases',component:Bases},
      {path:'module/:section',name:'module',component:ModulePending,props:true},
        {
          path: 'farmer-applications',
          name: 'FarmerApplications',
          component: FarmerReview
        },
    ]},
    {path:'/bases',redirect:'/manage/bases'},
    {path:'/trace',redirect:'/search'},
    {path:'*',redirect:'/'}
  ],
  scrollBehavior(){return{x:0,y:0};}
});

// 不将开发后台发布为生产环境的无鉴权管理入口。
// 后续接入 Spring Security 后，应把这里改成按后端身份做的路由守卫。
router.beforeEach(checkManageAccess);

export default router;
