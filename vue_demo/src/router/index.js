import { createRouter, createWebHistory } from 'vue-router';
import Register from '@/components/Register.vue';
import Login from '@/components/Login.vue';
import Home from '@/components/Home.vue';
import HaosenHome from '@/components/haosenproject/haosenhome.vue';
import BarData from '@/components/haosenproject/homechart/CleanBarChart.vue';
import MainData from '@/components/haosenproject/homechart/MainDataPieChart.vue';
import BranchBar from '@/components/haosenproject/homechart/BranchBarChart.vue';
import HospitalData from '@/components/haosenproject/maindataview/HospitalDataView.vue';
import HomeDashboard from '@/components/haosenproject/homechart/HomeDashboardView.vue';
import DrugStoreData from '@/components/haosenproject/maindataview/DrugStoreDataView.vue';
import CompanyData from '@/components/haosenproject/maindataview/CompanyDataView.vue';
import AppealDataView from '@/components/haosenproject/appealdataview/AppealDataView.vue';
import ImportAppealData from '@/components/haosenproject/appealdataview/ImportAppealDataView.vue';

import ImportCleanData from '@/components/haosenproject/cleandataview/ImportCleanDataView.vue';

import ImportUpdateData from '@/components/haosenproject/updatedataView/ImportUpdateDataView.vue';


const routes = [
    {
        path: '/',
        redirect: '/home' // 默认重定向到首页
    },
    {
        path: '/register',
        name: 'Register',
        component: Register
    },
    {
        path: '/login',
        name: 'Login',
        component: Login,
        meta: { requiresGuest: true } // 只有未登录用户可访问
    },
    {
        path: '/home',
        name: 'Home',
        component: Home,
        meta: { requiresAuth: true } // 需要登录才能访问
    },
    // 项目跳转路由配置

    {
        path: '/projects',
        children: [
            {
                path: '/project/:id',
                name: 'HaosenHome',
                component: HaosenHome,
                meta: { requiresAuth: true }, // 需要登录才能访问
                props: true
            },

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
        children: [
            {
            path: 'showAppealData',
            name: 'showAppealData',
            component: AppealDataView,
            meta: { requiresAuth: true } // 需要登录才能访问
        },
            {
                path: 'importAppealData',
                name: 'importAppealData',
                component: ImportAppealData,
                meta: { requiresAuth: true } // 需要登录才能访问
            }
        ] // 需要登录才能访问
    },

    {
        path: '/cleanData',
        children: [
            {
                path: 'importCleanData',
                name: 'importCleanData',
                component: ImportCleanData,
                meta: { requiresAuth: true } // 需要登录才能访问
            }

        ] // 需要登录才能访问
    },

    {
        path: '/updateData',
        children: [
            {
                path: 'importUpdateData',
                name: 'importUpdateData',
                component: ImportUpdateData,
                meta: { requiresAuth: true } // 需要登录才能访问
            }

        ] // 需要登录才能访问
    },



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