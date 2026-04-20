import { createRouter, createWebHistory } from 'vue-router';
import RegisterView from '@/components/RegisterView.vue';
import LoginView from '@/components/LoginView.vue';
import HomeView from '@/components/HomeView.vue';
import HaoSenHome from '@/components/haosenproject/HaoSenHome.vue';
import BarData from '@/components/haosenproject/homechartview/CleanBarChart.vue';
import MainData from '@/components/haosenproject/homechartview/MainDataPieChart.vue';
import BranchBar from '@/components/haosenproject/homechartview/BranchBarChart.vue';
import HospitalDataView from '@/components/haosenproject/maindataview/HospitalDataView.vue';
import HomeDashboardView from '@/components/haosenproject/homechartview/HomeDashboardView.vue';
import DrugStoreDataView from '@/components/haosenproject/maindataview/DrugStoreDataView.vue';
import CompanyDataView from '@/components/haosenproject/maindataview/CompanyDataView.vue';
import AppealDataView from '@/components/haosenproject/appealdataview/AppealDataView.vue';
import ImportAppealDataView from '@/components/haosenproject/appealdataview/ImportAppealDataView.vue';
import ImportCleanDataView from '@/components/haosenproject/cleandataview/ImportCleanDataView.vue';
import ImportUpdateDataView from '@/components/haosenproject/updatedataView/ImportUpdateDataView.vue';
import SelectCleanDataView from '@/components/haosenproject/cleandataview/SelectCleanDataView.vue';
import GetRepeatDataView from '@/components/haosenproject/repeatdataview/GetRepeatDataView.vue';
import GetExportHSDataView from "@/components/haosenproject/repeatdataview/GetExportHSDataView.vue";
import ImportExportHSDataView from "@/components/haosenproject/repeatdataview/ImportExportHSDataView.vue";

// 主数据项目模糊匹配
import MdFuzzyBatchView from '@/components/maindataproject/fuzzyview/MdFuzzyBatchView.vue';
import MdFuzzyUploadView from '@/components/maindataproject/fuzzyview/MdFuzzyUploadView.vue';
import MdFuzzySummaryView from '@/components/maindataproject/fuzzyview/MdFuzzySummaryView.vue';

import  HengRuiHome from '@/components/hengruiproject/HengRuiHome.vue';
import HrBatchDataView from '@/components/hengruiproject/batchdataview/HrBatchDataView.vue';
import HrImportBatchDataView from '@/components/hengruiproject/batchdataview/HrImportBatchDataView.vue';
import HrMonitoringDataView from '@/components/hengruiproject/monitoringdataview/HrMonitoringDataView.vue';
import HrOrgRelationView from '@/components/hengruiproject/orgrelationview/HrOrgRelationView.vue';
import HrImportOrgRelationView from '@/components/hengruiproject/orgrelationview/HrImportOrgRelationView.vue';

// 主数据项目
import MainDataHome from '@/components/maindataproject/MainDataHome.vue';
import MdLocationBatchView from '@/components/maindataproject/locationview/MdLocationBatchView.vue';
import MdLocationUploadView from '@/components/maindataproject/locationview/MdLocationUploadView.vue';
import MdLocationView from '@/components/maindataproject/locationview/MdLocationView.vue';


const routes = [
    {
        path: '/',
        redirect: '/home' // 默认重定向到首页
    },
    {
        path: '/register',
        name: 'Register',
        component: RegisterView
    },
    {
        path: '/login',
        name: 'Login',
        component: LoginView,
        meta: { requiresGuest: true } // 只有未登录用户可访问
    },
    {
        path: '/home',
        name: 'Home',
        component: HomeView,
        meta: { requiresAuth: true } // 需要登录才能访问
    },
    // 项目跳转路由配置

    {
        path: '/projects',
        name: 'Projects',
        redirect: '/projects/1',
        children: [
            {
                path: 'project/haosen',
                name: 'HaoSenHome',
                component: HaoSenHome,
                meta: { requiresAuth: true },
                props: true
            },
            {
                path: 'project/hengrui',
                name: 'HengRuiHome',
                component: HengRuiHome,
                meta: { requiresAuth: true },
                props: true
            },
            {
                path: 'project/maindata',
                name: 'MainDataHome',
                component: MainDataHome,
                meta: { requiresAuth: true },
                props: true
            }
        ]
    },


    {
        path: '/haosen/getcleanbardata',
        name: 'getcleanbardata',
        component: BarData,
        meta: { requiresAuth: true } // 需要登录才能访问
    },
    {
        path: '/haosen/getmainpiedata',
        name: 'getmainpiedata',
        component: MainData,
        meta: {requiresAuth: true} // 需要登录才能访问
    },
    {
        path: '/haosen/getbranchbardata',
        name: 'getbranchbardata',
        component: BranchBar,
        meta: { requiresAuth: true } // 需要登录才能访问
    },

    {
        path: '/haosen/homedashboard',
        name: 'homedashboard',
        component: HomeDashboardView,
        meta: { requiresAuth: true }
    },

    {
        path: '/haosen/database',
        name: 'Database',
        children: [
            {
                path: 'hospital',
                name: 'hospitaldata',
                component: HospitalDataView,
                meta: { requiresAuth: true }
            },
            {
                path: 'drugstore',
                name: 'drugstoredata',
                component: DrugStoreDataView,
                meta: { requiresAuth: true }
            },
            {
                path: 'company',
                name: 'companydata',
                component: CompanyDataView,
                meta: { requiresAuth: true }
            }
        ]
    },

    {
        path: '/haosen/appealdata',
        name: 'AppealData',
        redirect: '/haosen/appealdata/list',
        children: [
            {
                path: 'list',
                name: 'appealdatalist',
                component: AppealDataView,
                meta: { requiresAuth: true }
            },
            {
                path: 'importappealdata',
                name: 'importappealdata',
                component: ImportAppealDataView,
                meta: { requiresAuth: true }
            }
        ]
    },

    {
        path: '/haosen/cleandata',
        name: 'CleanData',
        children: [
            {
                path: 'importcleandata',
                name: 'importcleandata',
                component: ImportCleanDataView,
                meta: { requiresAuth: true }
            },
            {
                path: 'selectcleandata',
                name: 'selectcleandata',
                component: SelectCleanDataView,
                meta: { requiresAuth: true }
            },

        ] // 需要登录才能访问
    },

    {
        path: '/haosen/updatedata',
        name: 'UpdateData',
        children: [
            {
                path: 'importupdatedata',
                name: 'importupdatedata',
                component: ImportUpdateDataView,
                meta: { requiresAuth: true } // 需要登录才能访问
            }

        ] // 需要登录才能访问
    },

    {
        path: '/haosen/repeatdata',
        name: 'RepeatData',
        children: [
           {
               path: 'getrepeatdata',
               name: 'getrepeatdata',
               component: GetRepeatDataView,
               meta: { requiresAuth: true }
           },
           {
               path: 'getexporthsdata',
               name: 'getexporthsdata',
               component: GetExportHSDataView,
               meta: { requiresAuth: true }
           },
           {
               path: 'importexporthsdata',
               name: 'importexporthsdata',
               component: ImportExportHSDataView,
               meta: { requiresAuth: true }
           }
       ]
    },

    {
        path: '/hengrui/matchedaddress',
        name: 'HengRuiMatchedAddress',
        redirect: '/hengrui/matchedaddress/batchdata',
        children: [
            {
                path: 'batchdata',
                name: 'batchdata',
                component: HrBatchDataView,
                meta: { requiresAuth: true }
            },
            {
                path: 'importbatch',
                name: 'importbatch',
                component: HrImportBatchDataView,
                meta: { requiresAuth: true }
            },
            {
                path: 'monitoringdata',
                name: 'monitoringdata',
                component: HrMonitoringDataView,
                meta: { requiresAuth: true }
            },
            {
                path: 'relationdata',
                name: 'relationdata',
                component: HrOrgRelationView,
                meta: { requiresAuth: true }
            },
            {
                path: 'importrelation',
                name: 'importrelation',
                component: HrImportOrgRelationView,
                meta: { requiresAuth: true }
            }
        ]
    },

    {
        path: '/maindata/location',
        name: 'MainDataLocation',
        redirect: '/maindata/location/batchdata',
        children: [
            {
                path: 'batchdata',
                name: 'maindatabatchdata',
                component: MdLocationBatchView,
                meta: { requiresAuth: true }
            },
            {
                path: 'upload',
                name: 'maindataupload',
                component: MdLocationUploadView,
                meta: { requiresAuth: true }
            },
            {
                path: 'datalist',
                name: 'maindatalocationdata',
                component: MdLocationView,
                meta: { requiresAuth: true }
            }
        ]
    },
    {
        path: '/maindata/fuzzymatch',
        name: 'MainDataFuzzyMatch',
        children: [
            {
                path: 'batch',
                name: 'mdFuzzyBatchList',
                component: MdFuzzyBatchView,
                meta: { requiresAuth: true }
            },
            {
                path: 'upload',
                name: 'mdFuzzyUpload',
                component: MdFuzzyUploadView,
                meta: { requiresAuth: true }
            },
            {
                path: 'summary',
                name: 'mdFuzzySummary',
                component: MdFuzzySummaryView,
                meta: { requiresAuth: true }
            }
        ]
    }



];

const router = createRouter({
    history: createWebHistory(),
    routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
    const isAuthenticated = localStorage.getItem('token') || null;

    // 需要认证的路由
    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!isAuthenticated) {
            next({
                name: 'Login',
                query: { redirect: to.fullPath }
            });
        } else {
            next();
        }
    }
    // 仅限未登录用户访问的路由
    else if (to.matched.some(record => record.meta.requiresGuest)) {
        if (isAuthenticated) {
            next({ name: 'Home' });
        } else {
            next();
        }
    }
    // 其他路由
    else {
        next();
    }
});


export default router;