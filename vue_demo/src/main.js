import { createApp } from 'vue';
import App from './App.vue';
import router from './router'; // 引入路由配置
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'   // 中文包


import 'element-plus/theme-chalk/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'  // 必须有这一行！
import '@/assets/css/dark-mode.css'                  // 你的自定义

// 导入需要的具体图标 fa-sign-out
import {faHospital, faStore, faUniversity, faSignOutAlt,faAngleDoubleLeft ,faAngleDoubleRight,faUser,faTrash,faClock,
    faThList,faSynagogue,faStoreAlt,faHospitalAlt,faFileAlt,faBookReader,faCat,faProjectDiagram,faDatabase,faChartLine,faLaptopCode,faUsers,faBook
    ,faSearch,faStar,faCalendarAlt,faPlusCircle,faEraser,faSnowman,faOilCan,faFlag,faFileUpload,faTimesCircle}
    from '@fortawesome/free-solid-svg-icons'

import { faTwitter, faVuejs } from '@fortawesome/free-brands-svg-icons'


const app = createApp(App); // 创建应用实例 file-upload

// 将图标添加到库中
library.add(faHospital,faStore,faUniversity,faSignOutAlt,faAngleDoubleLeft,
    faAngleDoubleRight,faUser,faTrash,faClock,faThList,faSynagogue,faStoreAlt,faHospitalAlt,faFileAlt,faBookReader,faCat,
    faProjectDiagram,faDatabase,faChartLine,faLaptopCode,faUsers,faBook,faSearch,faStar,faCalendarAlt,faPlusCircle,faEraser,faSnowman,faOilCan,
    faFlag,faFileUpload,faTimesCircle)

import HomeDashboardView from '@/components/haosenproject/homechart/HomeDashboardView.vue'
import HospitalDataView from '@/components/haosenproject/maindataview/HospitalDataView.vue'
import DrugStoreDataView from '@/components/haosenproject/maindataview/DrugStoreDataView.vue'
import CompanyDataView from '@/components/haosenproject/maindataview/CompanyDataView.vue'
import AppealDataView from '@/components/haosenproject/appealdataview/AppealDataView.vue'
import ImportAppealDataView from '@/components/haosenproject/appealdataview/ImportAppealDataView.vue'
import ImportCleanDataView from '@/components/haosenproject/cleandataview/ImportCleanDataView.vue'
import ImportUpdateDataView from '@/components/haosenproject/updatedataView/ImportUpdateDataView.vue'
import SelectCleanDataView from '@/components/haosenproject/cleandataview/SelectCleanDataView.vue'



app.component('HomeDashboardView', HomeDashboardView)
app.component('HospitalDataView', HospitalDataView)
app.component('DrugStoreDataView', DrugStoreDataView)
app.component('CompanyDataView', CompanyDataView)
app.component('AppealDataView', AppealDataView)
app.component('ImportAppealDataView', ImportAppealDataView)
app.component('ImportCleanDataView', ImportCleanDataView)
app.component('ImportUpdateDataView', ImportUpdateDataView)
app.component('SelectCleanDataView', SelectCleanDataView)



// 全局注册组件
app.component('font-awesome-icon', FontAwesomeIcon)

app.use(router); // 注册路由 times-circle

app.use(ElementPlus, { locale: zhCn })   // 关键一行

app.mount('#app'); // 挂载到 #app


