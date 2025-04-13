<template>
  <div ref="chart" style="width: 850px; height: 400px;"></div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';

const chart = ref(null);

onMounted(() => {
  const myChart = echarts.init(chart.value, null, {
    renderer: 'svg',
    devicePixelRatio: window.devicePixelRatio > 1 ? 2 : 1
  });

  // 模拟数据
  const data = {
    "dates": ["一号", "二号", "三号", "四号", "五号", "六号", "七号"],
    "update": [45, 52, 38, 65, 49, 72, 60],  // 更新数据
    "appeal": [15, 22, 28, 35, 29, 42, 30]   // 申诉数据
  }

  // ECharts 配置项
  const option = {
    title: {
      text: '近七天数据更新与申诉趋势',
      left: 'center',
      textStyle: {
        color: '#333333',
        fontSize: 18,
        fontWeight: 'bold',
        fontFamily: 'Arial, sans-serif',
      },
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      },
      textStyle: {
        color: '#333333',
        fontFamily: 'Arial, sans-serif',
      },
    },
    legend: {
      data: ['更新', '申诉'],
      bottom: 10,
      textStyle: {
        color: '#333333',
        fontFamily: 'Arial, sans-serif',
      },
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true,
      backgroundColor: '#F5F5F5',
      borderColor: '#E0E0E0',
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.dates,
      axisLine: {
        lineStyle: {
          color: '#E0E0E0',
        },
      },
      axisLabel: {
        color: '#333333',
        fontFamily: 'Arial, sans-serif',
      },
    },
    yAxis: {
      type: 'value',
      name: '条数',
      axisLine: {
        lineStyle: {
          color: '#000000',
        },
      },
      axisLabel: {
        color: '#333333',
        fontFamily: 'Arial, sans-serif',
      },
      splitLine: {
        lineStyle: {
          color: '#E0E0E0',
        },
      },
    },
    series: [
      {
        name: '更新',
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
          color: '#6DA9E4'
        }
      },
      {
        name: '申诉',
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
      }
    ]
  };

  myChart.setOption(option);

  // 响应式调整
  window.addEventListener('resize', function() {
    myChart.resize();
  });
});
</script>

<style scoped>
/* 可以在这里添加样式 */
</style>