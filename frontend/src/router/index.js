import Vue from 'vue';
import VueRouter from 'vue-router';
import AppShell from '../components/AppShell.vue';
import Dashboard from '../views/Dashboard.vue';
import Bases from '../views/Bases.vue';
import ModulePending from '../views/ModulePending.vue';
import TracePreview from '../views/TracePreview.vue';

Vue.use(VueRouter);

export default new VueRouter({
  mode: 'history',
  routes: [
    {
      path: '/',
      component: AppShell,
      children: [
        { path: '', name: 'dashboard', component: Dashboard },
        { path: 'bases', name: 'bases', component: Bases },
        { path: 'module/:section', name: 'module', component: ModulePending, props: true }
      ]
    },
    { path: '/trace', name: 'trace-preview', component: TracePreview },
    { path: '*', redirect: '/' }
  ],
  scrollBehavior() { return { x: 0, y: 0 }; }
});
