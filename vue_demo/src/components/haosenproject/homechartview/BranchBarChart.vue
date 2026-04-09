<template>
  <!--  class="chart"-->
  <!--  style="width: 37rem; height: 16rem;-->
  <div ref="chart" class="chart"></div>
</template>

<script setup>
import '@/assets/css/dark-mode.css'
import { ref, onMounted, onUnmounted, computed } from 'vue';
import * as echarts from 'echarts';
import axios from "axios";

const chart = ref(null);
let myChart = null;

// 检测当前主题
const isDark = computed(() => document.documentElement.classList.contains('dark'));

// 获取主题相关颜色
const getThemeColors = () => {
  const dark = isDark.value;
  return {
    title: dark ? '#e0e0e0' : '#333333',
    text: dark ? '#e0e0e0' : '#333333',
    tooltipText: dark ? '#e0e0e0' : '#333333',
    legendText: dark ? '#e0e0e0' : '#333333',
    gridBg: dark ? '#1a1625' : '#F5F5F5',
    gridBorder: dark ? '#3d3550' : '#E0E0E0',
    axisLine: dark ? '#3d3550' : '#E0E0E0',
    splitLine: dark ? '#3d3550' : '#E0E0E0'
  };
};

// 更新图表主题（不重新初始化）
const updateChartTheme = () => {
  if (!myChart) return;

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
      backgroundColor: isDark.value ? '#1f1b2e' : '#ffffff',
      borderColor: themeColors.gridBorder,
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
    xAxis: {
      axisLine: {
        lineStyle: {
          color: themeColors.axisLine,
        },
      },
      axisLabel: {
        color: themeColors.text,
      },
    },
    yAxis: {
      axisLine: {
        lineStyle: {
          color: themeColors.axisLine,
        },
      },
      axisLabel: {
        color: themeColors.text,
      },
      splitLine: {
        lineStyle: {
          color: themeColors.splitLine,
        },
      },
    },
  });
};

const initChart = async() => {
  if (!chart.value) return;

  // 如果已存在实例，先销毁
  if (myChart) {
    myChart.dispose();
  }

  myChart = echarts.init(chart.value, null, {
    renderer: 'svg',
    devicePixelRatio: window.devicePixelRatio > 1 ? 2 : 1
  });

  let data;
  try {
    const response = await axios.post('/api/haosen/homeData/getBranchBarData');
    if (response.data.code === 200){
      data = response.data.data;
    }
  } catch (error) {
    console.error(error);
  }

  const names = data.map(item => item.name);
  const values = data.map(item => item.value);

  // 获取主题颜色
  const themeColors = getThemeColors();

  // ECharts 配置项 - 支持主题切换
  const option = {
    title: {
      text: '总分店/院编码待补充',
      left: 'center',
      textStyle: {
        color: themeColors.title,
        fontSize: 13,
        fontWeight: 'bold',
        fontFamily: 'Arial, sans-serif',
      },
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      backgroundColor: isDark.value ? '#1f1b2e' : '#ffffff',
      borderColor: themeColors.gridBorder,
      textStyle: {
        color: themeColors.tooltipText,
        fontFamily: 'Arial, sans-serif',
        fontSize: 12,

      },
    },
    legend: {
      data: ['编码待补充'],
      bottom: 10,
      textStyle: {
        color: themeColors.legendText,
        fontFamily: 'Arial, sans-serif',
        fontSize: 12,
      },
      itemWidth: 20,
      itemHeight: 10,
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '20%',
      containLabel: true,
      backgroundColor: themeColors.gridBg,
      borderColor: themeColors.gridBorder,
    },
    xAxis: {
      type: 'category',
      data: names,
      axisLine: {
        lineStyle: {
          color: themeColors.axisLine,
        },
      },
      axisLabel: {
        color: themeColors.text,
        fontFamily: 'Arial, sans-serif',
        fontSize: 12,
      },
    },
    yAxis: {
      type: 'value',
      axisLine: {
        lineStyle: {
          color: themeColors.axisLine,
        },
      },
      axisLabel: {
        color: themeColors.text,
        fontFamily: 'Arial, sans-serif',
        fontSize: 12,
      },
      splitLine: {
        lineStyle: {
          color: themeColors.splitLine,
        },
      },
    },
    series: [
      {
        name: '编码待补充',
        type: 'bar',
        data: values.map((value) => ({
          value: value,
          itemStyle: {
            color: '#6da1e4'
          }
        })),
        itemStyle: {
          borderRadius: [2, 2, 0, 0],
          color: '#6da1e4'
        },
        // 移除了 emphasis 配置项，这样鼠标悬浮时颜色就不会变化
        label: {
          show: false,
          position: 'top',
          fontFamily: 'Arial, sans-serif',
          fontSize: 12,
          fontWeight: 'bold'
        },
        barWidth: '35%'
      }
    ]
  };

  myChart.setOption(option);

  // 立即调整尺寸
  myChart.resize();
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

const handleResize = debounce(() => {
  if (myChart) {
    myChart.resize();
  }
}, 150);

onMounted(async() => {
  await initChart();
  window.addEventListener('resize', handleResize);

  // 监听主题变化事件
  const handleThemeChange = () => {
    if (myChart) {
      updateChartTheme();  // 只更新主题，不重新初始化
    }
  };
  window.addEventListener('themeChanged', handleThemeChange);
  
  // 保存清理函数
  if (chart.value) {
    chart.value._themeListener = handleThemeChange;
  }
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  
  // 移除主题监听
  if (chart.value?._themeListener) {
    window.removeEventListener('themeChanged', chart.value._themeListener);
  }

  if (myChart) {
    myChart.dispose();
    myChart = null;
  }
});
</script>

<style scoped>
/* 可以在这里添加样式 */
.chart {
  background: var(--bg-secondary, #ffffff);
  width: 100%;
  height: 100%;
}
</style>