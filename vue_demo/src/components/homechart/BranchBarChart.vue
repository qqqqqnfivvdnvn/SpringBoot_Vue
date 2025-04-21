<template>
  <div ref="chart" style="width: 850px; height: 400px;"></div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import axios from "axios";

const chart = ref(null);

onMounted(async() => {
  const myChart = echarts.init(chart.value, null, {
    renderer: 'svg',
    devicePixelRatio: window.devicePixelRatio > 1 ? 2 : 1
  });

  const response = await axios.post('/api/homeData/getBranchBarData');
  let baseData;
  try {
    if (response.data.code === 200){
      baseData = response.data.data.map(item => ({
        name: item.name,
        value: Number(item.value) || 0
      }));
    }
  } catch (error) {
    console.error(error);
  }

  const colorMap = {
    '总分店编码待补充': '#6da1e4',
    '总分院编码待补充': '#f9a200',
    '申诉数据待处理': '#40b786',
  };

  const option = {
    title: {
      text: '总分店、院编码待补充',
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
        type: 'shadow'
      },
      textStyle: {
        color: '#333333',
        fontFamily: 'Arial, sans-serif',
      },
    },
    // legend: {
    //   data: ['数量'], // 与 series 的 name 一致
    //   textStyle: {
    //     color: '#333333',
    //     fontFamily: 'Arial, sans-serif',
    //   },
    //   top: 30
    // },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
      backgroundColor: '#F5F5F5',
      borderColor: '#E0E0E0',
    },
    xAxis: {
      type: 'category',
      data: baseData.map(item => item.name),
      axisLabel: {
        color: '#333333',
        fontFamily: 'Arial, sans-serif',
        interval: 0 // 确保所有标签都显示
      },
      axisLine: {
        lineStyle: {
          color: '#E0E0E0'
        }
      },
      axisTick: {
        alignWithLabel: true // 刻度与标签对齐
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#333333',
        fontFamily: 'Arial, sans-serif',
      },
      axisLine: {
        lineStyle: {
          color: '#E0E0E0'
        }
      },
      splitLine: {
        lineStyle: {
          color: '#E0E0E0'
        }
      }
    },
    series: [{
      name: '数量',
      type: 'bar',
      data: baseData.map(item => ({
        value: item.value,
        name: item.name, // 添加 name 属性，用于 tooltip 和 legend 的显示
        itemStyle: {
          color: colorMap[item.name] || '#6da1e4'
        }
      })),
      barWidth: '30%',
      itemStyle: {
        borderRadius: [5, 5, 0, 0],
        borderColor: '#fff',
        borderWidth: 1
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.2)'
        }
      }
    }]
  };

  myChart.setOption(option);
  window.addEventListener('resize', () => myChart.resize());
});
</script>

<style scoped>
/* 样式 */
</style>