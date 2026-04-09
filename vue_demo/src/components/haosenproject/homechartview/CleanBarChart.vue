<template>
  <div ref="chart" class="chart"></div>
</template>

<script setup>
import '@/assets/css/dark-mode.css'
import {ref, onMounted, onUnmounted, computed} from 'vue';
import * as echarts from 'echarts';
import axios from 'axios';

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
    yAxis: [
      {
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
      {
        axisLine: {
          lineStyle: {
            color: themeColors.axisLine,
          },
        },
        axisLabel: {
          color: themeColors.text,
        },
      }
    ],
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

  try {
    const response = await axios.post('/api/haosen/homeData/getCleanBarData');

    if (response.data.code === 200) {
      const backendData = response.data.data;
      const dates = backendData.map(item => item.day);
      const companyCount = backendData.map(item => item.companyCount);
      const drugstoreCount = backendData.map(item => item.drugstoreCount);
      const hosCount = backendData.map(item => item.hosCount);

      // 计算每日总和
      const totalCount = backendData.map(item =>
          item.companyCount + item.drugstoreCount + item.hosCount
      );

      // 获取主题颜色
      const themeColors = getThemeColors();

      const option = {
        title: {
          text: '近七天数据清洗趋势',
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
            type: 'shadow',
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
          data: ['医院', '药店', '商业', '总计'],
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
          containLabel: true,
          backgroundColor: themeColors.gridBg,
          borderColor: themeColors.gridBorder,
        },
        xAxis: {
          type: 'category',
          data: dates,
          axisLine: {
            lineStyle: {
              color: themeColors.splitLine,
            },
          },
          axisLabel: {
            interval: 0,  // 关键配置，显示所有标签
            color: themeColors.text,
            fontFamily: 'Arial, sans-serif',
            fontSize: 11,
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
            name: '医院',
            type: 'bar',
            data: hosCount,
            itemStyle: {
              color: '#6da1e4',
              borderRadius: [5, 5, 0, 0],
            },
          },
          {
            name: '药店',
            type: 'bar',
            data: drugstoreCount,
            itemStyle: {
              color: '#40b786',
              borderRadius: [5, 5, 0, 0],
            },
          },
          {
            name: '商业',
            type: 'bar',
            data: companyCount,
            itemStyle: {
              color: '#f9a200',
              borderRadius: [5, 5, 0, 0],
            },
          },
          {
            name: '总计',
            type: 'line',
            data: totalCount,
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            itemStyle: {
              color: '#9478cc',
            },
            lineStyle: {
              width: 3,
            },
            emphasis: {
              lineStyle: {
                width: 4
              },
              itemStyle: {
                color: '#427aa4'
              }
            }
          }
        ],
      };

      myChart.setOption(option);

      // 立即调整尺寸
      myChart.resize();
    } else {
      console.error('获取数据失败:', response.data.msg);
    }
  } catch (error) {
    console.error('获取数据失败:', error);
  }
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
      updateChartTheme();
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
.chart {
  background: var(--bg-secondary, #ffffff);
  width: 100%;
  height: 100%;
}
</style>