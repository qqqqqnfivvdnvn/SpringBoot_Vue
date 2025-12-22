import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// FontAwesome 相关
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import {
    faHospital, faStore, faUniversity, faSignOutAlt,
    faAngleDoubleLeft, faAngleDoubleRight, faUser, faTrash,
    faClock, faThList, faSynagogue, faStoreAlt, faHospitalAlt,
    faFileAlt, faBookReader, faCat, faProjectDiagram, faDatabase,
    faChartLine, faLaptopCode, faUsers, faBook, faSearch, faStar,
    faCalendarAlt, faPlusCircle, faEraser, faSnowman, faOilCan,
    faFlag, faFileUpload, faTimesCircle
} from '@fortawesome/free-solid-svg-icons'
import { faTwitter, faVuejs } from '@fortawesome/free-brands-svg-icons'

// Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'  // 中文语言包

// 添加图标到库
library.add(
    faHospital, faStore, faUniversity, faSignOutAlt,
    faAngleDoubleLeft, faAngleDoubleRight, faUser, faTrash,
    faClock, faThList, faSynagogue, faStoreAlt, faHospitalAlt,
    faFileAlt, faBookReader, faCat, faProjectDiagram, faDatabase,
    faChartLine, faLaptopCode, faUsers, faBook, faSearch, faStar,
    faCalendarAlt, faPlusCircle, faEraser, faSnowman, faOilCan,
    faFlag, faFileUpload, faTimesCircle
)

const app = createApp(App)

// 全局注册 FontAwesome 组件
app.component('font-awesome-icon', FontAwesomeIcon)

// 必须在 mount 之前注册插件！！！
app.use(ElementPlus, { locale: zhCn })  // ← 先注册 ElementPlus
app.use(router)                         // ← 再注册路由

// 全局注册动态使用的子组件（关键！）
import HomeDashboardView from '@/components/haosenproject/homechart/HomeDashboardView.vue'
import HospitalDataView from '@/components/haosenproject/maindataview/HospitalDataView.vue'
import DrugStoreDataView from '@/components/haosenproject/maindataview/DrugStoreDataView.vue'
import CompanyDataView from '@/components/haosenproject/maindataview/CompanyDataView.vue'
import AppealDataView from '@/components/haosenproject/appealdataview/AppealDataView.vue'
import ImportAppealDataView from '@/components/haosenproject/appealdataview/ImportAppealDataView.vue'
import ImportCleanDataView from '@/components/haosenproject/cleandataview/ImportCleanDataView.vue'
import ImportUpdateDataView from '@/components/haosenproject/updatedataView/ImportUpdateDataView.vue'

app.component('HomeDashboardView', HomeDashboardView)
app.component('HospitalDataView', HospitalDataView)
app.component('DrugStoreDataView', DrugStoreDataView)
app.component('CompanyDataView', CompanyDataView)
app.component('AppealDataView', AppealDataView)
app.component('ImportAppealDataView', ImportAppealDataView)
app.component('ImportCleanDataView', ImportCleanDataView)
app.component('ImportUpdateDataView', ImportUpdateDataView)

// 最后才挂载
app.mount('#app')