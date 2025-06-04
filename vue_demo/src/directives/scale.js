import { nextTick } from 'vue';

export default {
    mounted(el, binding) {
        const { width = 1920, height = 1080 } = binding.value || {};

        // 设置基础样式
        el.style.position = 'absolute';
        el.style.transformOrigin = '0 0';
        el.style.left = '50%';
        el.style.top = '50%';
        el.style.transition = 'transform 0.3s ease'; // 添加平滑过渡

        const resize = () => {
            // 计算缩放比例
            const scaleX = window.innerWidth / width;
            const scaleY = window.innerHeight / height;
            const scale = Math.min(scaleX, scaleY); // 取最小比例保证完整显示

            // 应用缩放
            el.style.transform = `scale(${scale}) translate(-50%, -50%)`;
            el.style.width = `${width}px`;
            el.style.height = `${height}px`;
        };

        // 初始执行
        nextTick(resize);

        // 监听窗口变化（添加防抖）
        let resizeTimer = null;
        const resizeHandler = () => {
            clearTimeout(resizeTimer);
            resizeTimer = setTimeout(resize, 300);
        };

        window.addEventListener('resize', resizeHandler);
        el._resizeHandler = resizeHandler;
    },
    unmounted(el) {
        window.removeEventListener('resize', el._resizeHandler);
    }
};








