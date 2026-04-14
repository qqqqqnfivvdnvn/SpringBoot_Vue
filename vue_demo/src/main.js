import { createApp } from 'vue';
import App from './App.vue';
import router from './router'; // 引入路由配置
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'   // 中文包

import 'element-plus/theme-chalk/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import '@/assets/css/dark-mode.css'
// 导入需要的具体图标 fa-sign-out
import {
    faHospital,
    faStore,
    faUniversity,
    faSignOutAlt,
    faSignInAlt,
    faAngleDoubleLeft,
    faAngleDoubleRight,
    faUser,
    faUserPlus,
    faUserShield,
    faCheckCircle,
    faExclamationCircle,
    faTimesCircle,
    faShieldAlt,
    faRocket,
    faDatabase,
    faTrash,
    faClock,
    faThList,
    faSynagogue,
    faStoreAlt,
    faHospitalAlt,
    faFileAlt,
    faBookReader,
    faCat,
    faProjectDiagram,
    faChartLine,
    faLaptopCode,
    faUsers,
    faBook,
    faSearch,
    faStar,
    faCalendarAlt,
    faPlusCircle,
    faEraser,
    faSnowman,
    faOilCan,
    faFlag,
    faFileUpload,
    faPaw,
    faFeather,
    faPaperPlane,
    faLeaf,
    faPlus,
    faHome,
    faChevronRight,
    faArrowLeft,
    faChevronDown,
    faTimes,
    faBriefcase,
    faTable,
    faLayerGroup,
    faLink,
    faList,
    faUpload,
    faExchangeAlt,
    faMapMarkedAlt
}
    from '@fortawesome/free-solid-svg-icons'

// import { faTwitter, faVuejs } from '@fortawesome/free-brands-svg-icons'


const app = createApp(App); // 创建应用实例 file-upload

// 将图标添加到库中
library.add(faHospital,faStore,faUniversity,faSignOutAlt,faSignInAlt,faAngleDoubleLeft,
    faAngleDoubleRight,faUser,faUserPlus,faUserShield,faCheckCircle,faExclamationCircle,faTimesCircle,faShieldAlt,faRocket,faDatabase,faTrash,faClock,faThList,faSynagogue,faStoreAlt,faHospitalAlt,faFileAlt,faBookReader,faCat,
    faProjectDiagram,faChartLine,faLaptopCode,faUsers,faBook,faSearch,faStar,faCalendarAlt,faPlusCircle,faEraser,faSnowman,faOilCan,
    faFlag,faFileUpload,faPaw,faFeather,faPaperPlane,faLeaf,faPlus,faHome,faChevronRight,faArrowLeft,faChevronDown,faTimes,faBriefcase,
    faTable,faLayerGroup,faLink,faList,faUpload,faExchangeAlt,faMapMarkedAlt)

import HomeDashboardView from '@/components/haosenproject/homechartview/HomeDashboardView.vue'
import HospitalDataView from '@/components/haosenproject/maindataview/HospitalDataView.vue'
import DrugStoreDataView from '@/components/haosenproject/maindataview/DrugStoreDataView.vue'
import CompanyDataView from '@/components/haosenproject/maindataview/CompanyDataView.vue'
import AppealDataView from '@/components/haosenproject/appealdataview/AppealDataView.vue'
import ImportAppealDataView from '@/components/haosenproject/appealdataview/ImportAppealDataView.vue'
import ImportCleanDataView from '@/components/haosenproject/cleandataview/ImportCleanDataView.vue'
import ImportUpdateDataView from '@/components/haosenproject/updatedataView/ImportUpdateDataView.vue'
import SelectCleanDataView from '@/components/haosenproject/cleandataview/SelectCleanDataView.vue'

import GetRepeatDataView from "@/components/haosenproject/repeatdataview/GetRepeatDataView.vue";

import GetExportHSDataView from "@/components/haosenproject/repeatdataview/GetExportHSDataView.vue";
import ImportExportHSDataView from "@/components/haosenproject/repeatdataview/ImportExportHSDataView.vue";

import HrBatchDataView from "@/components/hengruiproject/batchdataview/HrBatchDataView.vue"
import HrImportBatchDataView from "@/components/hengruiproject/batchdataview/HrImportBatchDataView.vue"
import HrMonitoringDataView from "@/components/hengruiproject/monitoringdataview/HrMonitoringDataView.vue"
import HrOrgRelationView from "@/components/hengruiproject/orgrelationview/HrOrgRelationView.vue"
import HrImportOrgRelationView from "@/components/hengruiproject/orgrelationview/HrImportOrgRelationView.vue"

import MdLocationBatchView from "@/components/maindataproject/locationview/MdLocationBatchView.vue"
import MdLocationUploadView from "@/components/maindataproject/locationview/MdLocationUploadView.vue"
import MdLocationView from "@/components/maindataproject/locationview/MdLocationView.vue"

import MdFuzzyBatchView from "@/components/maindataproject/fuzzyview/MdFuzzyBatchView.vue"
import MdFuzzyUploadView from "@/components/maindataproject/fuzzyview/MdFuzzyUploadView.vue"
import MdFuzzySummaryView from "@/components/maindataproject/fuzzyview/MdFuzzySummaryView.vue"

import PermissionManager from "@/components/layout/PermissionManager.vue"


app.component('HomeDashboardView', HomeDashboardView)
app.component('HospitalDataView', HospitalDataView)
app.component('DrugStoreDataView', DrugStoreDataView)
app.component('CompanyDataView', CompanyDataView)
app.component('AppealDataView', AppealDataView)
app.component('ImportAppealDataView', ImportAppealDataView)
app.component('ImportCleanDataView', ImportCleanDataView)
app.component('ImportUpdateDataView', ImportUpdateDataView)
app.component('SelectCleanDataView', SelectCleanDataView)

app.component('GetRepeatDataView', GetRepeatDataView)
app.component('GetExportHSDataView', GetExportHSDataView)
app.component('ImportExportHSDataView', ImportExportHSDataView)

app.component('HrBatchDataView', HrBatchDataView)
app.component('HrImportBatchDataView', HrImportBatchDataView)
app.component('HrMonitoringDataView', HrMonitoringDataView)
app.component('HrOrgRelationView', HrOrgRelationView)
app.component('HrImportOrgRelationView', HrImportOrgRelationView)

app.component('MdLocationBatchView', MdLocationBatchView)
app.component('MdLocationUploadView', MdLocationUploadView)
app.component('MdLocationView', MdLocationView)

app.component('MdFuzzyBatchView', MdFuzzyBatchView)
app.component('MdFuzzyUploadView', MdFuzzyUploadView)
app.component('MdFuzzySummaryView', MdFuzzySummaryView)

app.component('PermissionManager', PermissionManager)

// 全局注册组件
app.component('font-awesome-icon', FontAwesomeIcon)

app.use(router); // 注册路由 times-circle

app.use(ElementPlus, { locale: zhCn })   // 关键一行

app.mount('#app'); // 挂载到 #app


