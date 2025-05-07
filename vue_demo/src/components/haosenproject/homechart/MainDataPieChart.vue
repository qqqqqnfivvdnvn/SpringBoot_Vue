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

  const response = await axios.post('/api/homeData/getMainPieData'); // 替换为你的后端 API 地址
  let baseData;
  try {
    if (response.data.code === 200){
      // 2. 强制转换数值类型
       baseData = response.data.data.map(item => ({
        name: item.name,
        value: Number(item.value) || 0 // 确保为数字
      }));
    }
  } catch (error) {
    console.error(error);
  }



  const colorMap = {
    '医院': '#6da1e4',
    '药店': '#40b786',
    '商业': '#ffa305'
  };

  const processedData = baseData.map(item => ({
    ...item,
    itemStyle: { color: colorMap[item.name] }
  }));

  const option = {
    title: {
      text: '主数据数据占比',
      left: 'center',
      textStyle: {
        color: '#333333',
        fontSize: 18,
        fontWeight: 'bold',
        fontFamily: 'Arial, sans-serif',
      },
    },
    tooltip: {
      trigger: 'item',
      formatter: function(params) {
        const total = baseData.reduce((sum, item) => sum + item.value, 0);
        const percent = ((params.value / total) * 100).toFixed(1);
        return `
          <div style="font-family:Arial,sans-serif;color:#333333">
            ${params.name}<br/>
            数量: <span style="font-weight:bold">${params.value}</span><br/>
            占比: <span style="font-weight:bold">${percent}%</span>
          </div>
        `;
      },
      textStyle: {
        color: '#333333',
        fontFamily: 'Arial, sans-serif',
      },
    },
    legend: {
      orient: 'vertical',
      right: 20,
      top: 'center',
      data: baseData.map(item => item.name),
      textStyle: {
        color: '#333333',
        fontFamily: 'Arial, sans-serif',
      },
    },
    grid: {
      backgroundColor: '#F5F5F5',
      borderColor: '#E0E0E0',
    },
    series: [{
      type: 'pie',
      radius: ['45%', '75%'],
      center: ['50%', '55%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2,
      },
      label: {
        show: true,
        formatter: '{b}: {d}%',
        color: '#333333',
        fontFamily: 'Arial, sans-serif',
        fontSize: 12
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.2)'
        },
        label: {
          show: true,
          fontSize: 14
        }
      },
      labelLine: {
        lineStyle: {
          color: '#E0E0E0'
        },
        smooth: 0.2,
        length: 10,
        length2: 15
      },
      data: processedData
    }]
  };

  myChart.setOption(option);
  window.addEventListener('resize', () => myChart.resize());
});
</script>

<style scoped>
/* 样式 */
</style>