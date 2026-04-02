const VueModule = require('vue');
const AppModule = require('./App.vue');

const Vue = VueModule.default || VueModule;
const App = AppModule.default || AppModule;

Vue.config.productionTip = false;
App.mpType = 'app';

const app = new Vue(App);
app.$mount();
