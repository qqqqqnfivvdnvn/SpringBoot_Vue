<template>
  <!--  class="chart"-->
  <!--  style="width: 37rem; height: 16rem;"-->
  <div ref="chart" class="chart"></div>
</template>

<script setup>
import {ref, onMounted, onUnmounted, computed} from 'vue';
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
    axisLine: dark ? '#000000' : '#000000',
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
          color: themeColors.splitLine,
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

const initChart = async () => {
  if (!chart.value) return;

  // 如果已存在实例，先销毁
  if (myChart) {
    myChart.dispose();
  }

  myChart = echarts.init(chart.value, null, {
    renderer: 'svg',
    devicePixelRatio: window.devicePixelRatio > 1 ? 2 : 1
  });

  const response = await axios.post('/api/haosen/homedata/getappealupdatedata'); // 替换为你的后端 API 地址
  let data;
  try {
    if (response.data.code === 200) {
      const dates = response.data.data.map(item => item.day);
      const appeal = response.data.data.map(item => item.appealCount);
      const update = response.data.data.map(item => item.updateCount);
      const hscode = response.data.data.map(item => item.hscodeCount);
      data = {
        dates: dates,
        appeal: appeal,
        update: update,
        hscode: hscode
      };
    }
  } catch (error) {
    console.error(error);
  }

  // 获取主题颜色
  const themeColors = getThemeColors();

  // ECharts 配置项 - 支持主题切换
  const option = {
    title: {
      text: '近七天数据更新与申诉趋势',
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
        type: 'line',
        label: {
          backgroundColor: '#6a7985'
        }
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
      data: ['豪森更新', '豪森申诉', '豪森编码'],
      bottom: 10,
      textStyle: {
        color: themeColors.legendText,
        fontFamily: 'Arial, sans-serif',
        fontSize: 12,
      },
      itemWidth: 20, // 设置图标宽度
      itemHeight: 10, // 设置图标高度

    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true,
      backgroundColor: themeColors.gridBg,
      borderColor: themeColors.gridBorder,
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.dates,
      axisLine: {
        lineStyle: {
          color: themeColors.splitLine,
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
      // name: '条数',
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
        name: '豪森更新',
        type: 'line',
        smooth: true,
        lineStyle: {
          width: 3,
          color: '#6da1e4' // 蓝色
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: 'rgba(109, 169, 228, 0.5)'
            },
            {
              offset: 1,
              color: 'rgba(109, 169, 228, 0.1)'
            }
          ])
        },
        data: data.update,
        symbol: 'circle',
        symbolSize: 8,
        itemStyle: {
          color: '#6DA9E4',
        }
      },
      {
        name: '豪森申诉',
        type: 'line',
        smooth: true,
        lineStyle: {
          width: 3,
          color: '#f06d6d' // 红色
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: 'rgba(244, 122, 122, 0.5)'
            },
            {
              offset: 1,
              color: 'rgba(244, 122, 122, 0.1)'
            }
          ])
        },
        data: data.appeal,
        symbol: 'circle',
        symbolSize: 8,
        itemStyle: {
          color: '#F47A7A'
        }
      },
      {
        name: '豪森编码',
        type: 'line',
        smooth: true,
        lineStyle: {
          width: 3,
          color: '#40b786'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: 'rgb(64, 183, 134, 0.5)'
            },
            {
              offset: 1,
              color: 'rgb(64, 183, 134, 0.1)'
            }
          ])
        },
        data: data.hscode,
        symbol: 'circle',
        symbolSize: 8,
        itemStyle: {
          color: '#40b786',
          fontSize: 12
        }
      },
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

onMounted(async () => {
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