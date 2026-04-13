import { createRouter, createWebHistory } from 'vue-router';
import RegisterView from '@/components/RegisterView.vue';
import LoginView from '@/components/LoginView.vue';
import HomeView from '@/components/HomeView.vue';
import HaoSenHome from '@/components/haosenproject/HaoSenHome.vue';
import BarData from '@/components/haosenproject/homechartview/CleanBarChart.vue';
import MainData from '@/components/haosenproject/homechartview/MainDataPieChart.vue';
import BranchBar from '@/components/haosenproject/homechartview/BranchBarChart.vue';
import HospitalData from '@/components/haosenproject/maindataview/HospitalDataView.vue';
import HomeDashboard from '@/components/haosenproject/homechartview/HomeDashboardView.vue';
import DrugStoreData from '@/components/haosenproject/maindataview/DrugStoreDataView.vue';
import CompanyData from '@/components/haosenproject/maindataview/CompanyDataView.vue';
import AppealDataView from '@/components/haosenproject/appealdataview/AppealDataView.vue';
import ImportAppealData from '@/components/haosenproject/appealdataview/ImportAppealDataView.vue';
import ImportCleanData from '@/components/haosenproject/cleandataview/ImportCleanDataView.vue';
import ImportUpdateData from '@/components/haosenproject/updatedataView/ImportUpdateDataView.vue';
import SelectCleanData from '@/components/haosenproject/cleandataview/SelectCleanDataView.vue';
import GetRepeatData from '@/components/haosenproject/repeatdataview/GetRepeatDataView.vue';
import GetExportHSData from "@/components/haosenproject/repeatdataview/GetExportHSDataView.vue";
import ImportExportHSData from "@/components/haosenproject/repeatdataview/ImportExportHSDataView.vue";

// 主数据项目模糊匹配
import MdFuzzyBatchView from '@/components/maindataproject/fuzzyview/MdFuzzyBatchView.vue';
import MdFuzzyUploadView from '@/components/maindataproject/fuzzyview/MdFuzzyUploadView.vue';
import MdFuzzySummaryView from '@/components/maindataproject/fuzzyview/MdFuzzySummaryView.vue';

import  HengRuiHome from '@/components/hengruiproject/HengRuiHome.vue';
import BatchDataView from '@/components/hengruiproject/batchdataview/BatchDataView.vue';
import ImportBatchDataView from '@/components/hengruiproject/batchdataview/ImportBatchDataView.vue';
import MonitoringDataView from '@/components/hengruiproject/monitoringdataview/MonitoringDataView.vue';
import OrgRelationView from '@/components/hengruiproject/orgrelationview/OrgRelationView.vue';
import ImportOrgRelationView from '@/components/hengruiproject/orgrelationview/ImportOrgRelationView.vue';

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
        path: '/getCleanBarData',
        name: 'getCleanBarData',
        component: BarData,
        meta: { requiresAuth: true } // 需要登录才能访问
    },
    {
        path: '/getMainPieData',
        name: 'getMainPieData',
        component: MainData,
        meta: {requiresAuth: true} // 需要登录才能访问
    },
    {
        path: '/getBranchBarData',
        name: 'getBranchBarData',
        component: BranchBar,
        meta: { requiresAuth: true } // 需要登录才能访问
    },

    {
        path: '/homeDashboard',
        name: 'homeDashboard',
        component: HomeDashboard,
        meta: { requiresAuth: true } // 需要登录才能访问
    },

    {
        path: '/database',
        name: 'Database',
        children: [
            {
                path: 'hospital',
                name: 'hospitalData',
                component: HospitalData,
                meta: { requiresAuth: true } // 需要登录才能访问
            },
            {
                path: 'drugstore',
                name: 'drugStoreData',
                component: DrugStoreData,
                meta: { requiresAuth: true } // 需要登录才能访问
            },
            {
                path: 'company',
                name: 'CompanyData',
                component: CompanyData,
                meta: { requiresAuth: true } // 需要登录才能访问
            }

        ]
    },

    {
        path: '/appealData',
        name: 'AppealData',
        redirect: '/appealData/list',
        children: [
            {
                path: 'list',
                name: 'appealDataList',
                component: AppealDataView,
                meta: { requiresAuth: true }
            },
            {
                path: 'importAppealData',
                name: 'importAppealData',
                component: ImportAppealData,
                meta: { requiresAuth: true }
            }
        ]
    },

    {
        path: '/cleanData',
        name: 'CleanData',
        children: [
            {
                path: 'importCleanData',
                name: 'importCleanData',
                component: ImportCleanData,
                meta: { requiresAuth: true } // 需要登录才能访问
            },
            {
                path: 'selectCleanData',
                name: 'selectCleanData',
                component: SelectCleanData,
                meta: { requiresAuth: true } // 需要登录才能访问
            },

        ] // 需要登录才能访问
    },

    {
        path: '/updateData',
        name: 'UpdateData',
        children: [
            {
                path: 'importUpdateData',
                name: 'importUpdateData',
                component: ImportUpdateData,
                meta: { requiresAuth: true } // 需要登录才能访问
            }

        ] // 需要登录才能访问
    },

    {
        path: '/repeatData',
        name: 'RepeatData',
        children: [
           {
               path: 'getRepeatData',
               name: 'getRepeatData',
               component: GetRepeatData,
               meta: { requiresAuth: true } // 需要登录才能访问
           },
           {
               path: 'getExportHSData',
               name: 'getExportHSData',
               component: GetExportHSData,
               meta: { requiresAuth: true } // 需要登录才能访问
           },
           {
               path: 'importExportHSData',
               name: 'importExportHSData',
               component: ImportExportHSData,
               meta: { requiresAuth: true } // 需要登录才能访问
           }
       ]
    },

    {
        path: '/hengrui',
        name: 'HengRui',
        children: [
            {
                path: 'batch',
                name: 'batchData',
                component: BatchDataView,
                meta: { requiresAuth: true }
            },
            {
                path: 'importBatch',
                name: 'importBatch',
                component: ImportBatchDataView,
                meta: { requiresAuth: true }
            },
            {
                path: 'monitoring',
                name: 'monitoringData',
                component: MonitoringDataView,
                meta: { requiresAuth: true }
            },
            {
                path: 'relation',
                name: 'relationData',
                component: OrgRelationView,
                meta: { requiresAuth: true }
            },
            {
                path: 'importRelation',
                name: 'importRelation',
                component: ImportOrgRelationView,
                meta: { requiresAuth: true }
            }
        ]
    },

    {
        path: '/maindata',
        name: 'MainData',
        children: [
            {
                path: 'batch',
                name: 'maindataBatchData',
                component: MdLocationBatchView,
                meta: { requiresAuth: true }
            },
            {
                path: 'upload',
                name: 'maindataUpload',
                component: MdLocationUploadView,
                meta: { requiresAuth: true }
            },
            {
                path: 'location',
                name: 'maindataLocationData',
                component: MdLocationView,
                meta: { requiresAuth: true }
            },
            {
                path: 'fuzzyMatch/batch',
                name: 'mdFuzzyBatchList',
                component: MdFuzzyBatchView,
                meta: { requiresAuth: true }
            },
            {
                path: 'fuzzyMatch/upload',
                name: 'mdFuzzyUpload',
                component: MdFuzzyUploadView,
                meta: { requiresAuth: true }
            },
            {
                path: 'fuzzyMatch/summary',
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