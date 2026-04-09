<template>
  <!--  class="chart"-->
  <!--  style="width: 37rem; height: 16rem;"-->
  <div ref="chart" class="chart" ></div>
</template>

<script setup>
import '@/assets/css/dark-mode.css'
import { ref, onMounted, onUnmounted, computed, defineExpose } from 'vue';
import * as echarts from 'echarts';
import axios from "axios";

const chart = ref(null);
let myChart = null;
let chartData = null;
let lastFetchTime = 0;
const CACHE_DURATION = 5 * 60 * 1000; // 5分钟缓存
let resizeObserver = null; // 存储 ResizeObserver 实例

// 检测当前主题
const isDark = computed(() => document.documentElement.classList.contains('dark'));

// 获取主题相关颜色
const getThemeColors = () => {
  const dark = isDark.value;
  return {
    title: dark ? '#e0e0e0' : '#333333',
    text: dark ? '#e0e0e0' : '#333333',
    tooltipBg: dark ? '#1f1b2e' : '#ffffff',
    tooltipText: dark ? '#e0e0e0' : '#333333',
    legendText: dark ? '#e0e0e0' : '#333333',
    gridBg: dark ? '#1a1625' : '#F5F5F5',
    gridBorder: dark ? '#3d3550' : '#E0E0E0',
    labelLine: dark ? '#3d3550' : '#E0E0E0',
    borderColor: dark ? '#1f1b2e' : '#fff'
  };
};

// 防抖函数
const debounce = (func, wait) => {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
};

// 获取数据
const fetchChartData = async () => {
  const now = Date.now();

  // 检查缓存
  if (chartData && (now - lastFetchTime < CACHE_DURATION)) {
    return chartData;
  }

  try {
    const response = await axios.post('/api/haosen/homeData/getMainPieData');
    if (response.data.code === 200) {
      chartData = response.data.data.map(item => ({
        name: item.name,
        value: Number(item.value) || 0
      }));
      lastFetchTime = now;
      return chartData;
    }
  } catch (error) {
    console.error('获取数据失败:', error);
    // 返回缓存数据或空数组
    return chartData || [];
  }
};

// 颜色映射
const colorMap = {
  '医院': '#6da1e4',
  '药店': '#40b786',
  '商业': '#ffa305'
};

// 更新图表主题（不重新初始化）
const updateChartTheme = () => {
  if (!myChart) return;

  // 获取当前数据
  const currentOption = myChart.getOption();
  if (!currentOption || !currentOption.series || !currentOption.series[0]) return;

  const baseData = currentOption.series[0].data;
  const total = baseData.reduce((sum, item) => sum + (item.value || 0), 0);

  // 获取主题颜色
  const themeColors = getThemeColors();

  // 只更新颜色相关的配置
  myChart.setOption({
    title: {
      textStyle: {
        color: themeColors.title,
      },
    },
    tooltip: {
      backgroundColor: themeColors.tooltipBg,
      borderColor: themeColors.gridBorder,
      formatter: function(params) {
        const percent = total > 0 ? ((params.value / total) * 100).toFixed(1) : '0.0';
        return `
          <div style="font-family:Arial,sans-serif;color:${themeColors.tooltipText};font-size:11px">
            ${params.marker}${params.name}<br/>
            数量: <span style="font-weight:bold">${params.value}</span><br/>
            占比: <span style="font-weight:bold">${percent}%</span>
          </div>
        `;
      },
      textStyle: {
        color: themeColors.tooltipText,
      },
    },
    legend: {
      textStyle: {
        color: themeColors.legendText,
      },
    },
    grid: {
      backgroundColor: themeColors.gridBg,
      borderColor: themeColors.gridBorder,
    },
    series: [{
      itemStyle: {
        borderColor: themeColors.borderColor,
      },
      label: {
        color: themeColors.text,
      },
      emphasis: {
        itemStyle: {
          shadowColor: isDark.value ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.2)'
        }
      },
      labelLine: {
        lineStyle: {
          color: themeColors.labelLine
        },
      }
    }]
  });
};

// 初始化图表
const initChart = async () => {
  if (!chart.value) return;

  // 如果已存在实例，先销毁
  if (myChart) {
    myChart.dispose();
  }

  // 初始化图表
  myChart = echarts.init(chart.value, null, {
    renderer: 'svg',
    devicePixelRatio: Math.min(window.devicePixelRatio, 2) // 限制最大DPI
  });

  // 获取数据
  const baseData = await fetchChartData();

  // 处理数据
  const processedData = baseData.map(item => ({
    ...item,
    itemStyle: { color: colorMap[item.name] }
  }));

  // 计算总数
  const total = baseData.reduce((sum, item) => sum + item.value, 0);

  // 获取主题颜色
  const themeColors = getThemeColors();

  // 配置项 - 支持主题切换
  const option = {
    title: {
      text: '主数据数据占比',
      left: 'center',
      textStyle: {
        color: themeColors.title,
        fontSize: 13,
        fontWeight: 'bold',
        fontFamily: 'Arial, sans-serif',
      },
    },
    tooltip: {
      trigger: 'item',
      backgroundColor: themeColors.tooltipBg,
      borderColor: themeColors.gridBorder,
      formatter: function(params) {
        const percent = total > 0 ? ((params.value / total) * 100).toFixed(1) : '0.0';
        return `
          <div style="font-family:Arial,sans-serif;color:${themeColors.tooltipText};font-size:11px">
            ${params.marker}${params.name}<br/>
            数量: <span style="font-weight:bold">${params.value}</span><br/>
            占比: <span style="font-weight:bold">${percent}%</span>
          </div>
        `;
      },
      textStyle: {
        color: themeColors.tooltipText,
        fontFamily: 'Arial, sans-serif',
        fontSize: 12,
      },
    },
    legend: {
      orient: 'vertical',
      right: 20,
      top: 'center',
      data: baseData.map(item => item.name),
      textStyle: {
        color: themeColors.legendText,
        fontFamily: 'Arial, sans-serif',
        fontSize: 12,
      },
      itemWidth: 20,
      itemHeight: 10,
    },
    grid: {
      backgroundColor: themeColors.gridBg,
      borderColor: themeColors.gridBorder,
    },
    series: [{
      type: 'pie',
      radius: ['45%', '75%'],
      center: ['50%', '55%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: themeColors.borderColor,
        borderWidth: 2,
      },
      label: {
        show: true,
        formatter: '{b}: {d}%',
        color: themeColors.text,
        fontFamily: 'Arial, sans-serif',
        fontSize: 12
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: isDark.value ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.2)'
        },
        label: {
          show: true,
          fontSize: 12
        }
      },
      labelLine: {
        lineStyle: {
          color: themeColors.labelLine
        },
        smooth: 0.2,
        length: 10,
        length2: 15
      },
      data: processedData,
      // 添加性能优化配置
      animation: true,
      animationDuration: 500,
      animationEasing: 'cubicOut',
      animationThreshold: 2000, // 数据量小于2000才执行动画
    }]
  };

  // 设置配置项，使用懒更新
  myChart.setOption(option, {
    lazyUpdate: true, // 启用懒更新
    notMerge: true
  });

  // 立即调整尺寸
  myChart.resize();

  return myChart;
};

// 防抖的resize处理
const handleResize = debounce(() => {
  if (myChart) {
    myChart.resize();
  }
}, 150);

onMounted(async () => {
  try {
    await initChart();

    // 添加resize监听（防抖处理）
    window.addEventListener('resize', handleResize);

    // 监听容器尺寸变化
    if (typeof ResizeObserver !== 'undefined' && chart.value) {
      resizeObserver = new ResizeObserver(handleResize);
      resizeObserver.observe(chart.value);
    }

    // 监听主题变化事件
    const handleThemeChange = () => {
      if (myChart) {
        updateChartTheme();  // 只更新主题，不重新初始化
      }
    };
    window.addEventListener('themeChanged', handleThemeChange);

    // 保存清理函数
    chart.value._themeListener = handleThemeChange;
  } catch (error) {
    console.error('图表初始化失败:', error);
  }
});

onUnmounted(() => {
  // 移除事件监听
  window.removeEventListener('resize', handleResize);

  // 移除ResizeObserver
  if (resizeObserver && chart.value) {
    resizeObserver.unobserve(chart.value);
    resizeObserver.disconnect();
    resizeObserver = null;
  }

  // 移除主题监听
  if (chart.value?._themeListener) {
    window.removeEventListener('themeChanged', chart.value._themeListener);
  }

  // 销毁图表实例
  if (myChart) {
    myChart.dispose();
    myChart = null;
  }
});

// 暴露刷新方法
const refreshChart = async () => {
  chartData = null; // 清除缓存
  if (myChart) {
    await initChart();
  }
};

// 可选：暴露给父组件
defineExpose({
  refreshChart
});
</script>

<style scoped>
.chart {
  background: var(--bg-secondary, #ffffff);
  width: 100%;
  height: 100%;
}
</style>