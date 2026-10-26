import Vue from 'vue';
import Button from 'element-ui/lib/button';
import Input from 'element-ui/lib/input';
import Table from 'element-ui/lib/table';
import TableColumn from 'element-ui/lib/table-column';
import Pagination from 'element-ui/lib/pagination';
import Drawer from 'element-ui/lib/drawer';
import Loading from 'element-ui/lib/loading';

import 'element-ui/lib/theme-chalk/base.css';
import 'element-ui/lib/theme-chalk/icon.css';
import 'element-ui/lib/theme-chalk/button.css';
import 'element-ui/lib/theme-chalk/input.css';
import 'element-ui/lib/theme-chalk/table.css';
import 'element-ui/lib/theme-chalk/table-column.css';
import 'element-ui/lib/theme-chalk/pagination.css';
import 'element-ui/lib/theme-chalk/drawer.css';
import 'element-ui/lib/theme-chalk/loading.css';
import 'element-ui/lib/theme-chalk/scrollbar.css';
import 'element-ui/lib/theme-chalk/tooltip.css';
import './assets/styles/theme.css';

import App from './App.vue';
import router from './router';

Vue.prototype.$ELEMENT = { size: 'small' };
[Button, Input, Table, TableColumn, Pagination, Drawer, Loading].forEach(component => {
  Vue.use(component);
});
Vue.config.productionTip = false;

new Vue({
  router,
  render: h => h(App)
}).$mount('#app');
