import { createApp } from 'vue';
import App from './App.vue';
import router from './router'; // 引入路由配置
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
// 导入需要的具体图标 fa-sign-out
import {faHospital, faStore, faUniversity, faSignOutAlt,faAngleDoubleLeft ,faAngleDoubleRight,faUser,faTrash,faClock} from '@fortawesome/free-solid-svg-icons'
import { faTwitter, faVuejs } from '@fortawesome/free-brands-svg-icons'

const app = createApp(App); // 创建应用实例

// 将图标添加到库中
library.add(faHospital,faStore,faUniversity,faSignOutAlt,faAngleDoubleLeft,faAngleDoubleRight,faUser,faTrash,faClock)

// 全局注册组件
app.component('font-awesome-icon', FontAwesomeIcon)

app.use(router); // 注册路由
app.mount('#app'); // 挂载到 #app