import { createApp } from 'vue';
import App from './App.vue';
import router from './router'; // 引入路由配置

const app = createApp(App); // 创建应用实例

app.use(router); // 注册路由
app.mount('#app'); // 挂载到 #app