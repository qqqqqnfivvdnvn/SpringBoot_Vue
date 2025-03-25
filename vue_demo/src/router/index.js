import { createRouter, createWebHistory } from 'vue-router';
import Register from '@/components/Register.vue';
import Login from '@/components/Login.vue';
import Home from '@/components/Home.vue';

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