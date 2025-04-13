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

  const baseData = [
    { value: 335, name: '已清洗完成' },
    { value: 310, name: '申诉中' },
    { value: 234, name: '申诉完成' }
  ];
  
  const colorMap = {
    '已清洗完成': '#6da1e4',
    '申诉中': '#40b786',
    '申诉完成': '#f9a200'
  };
  
  const processedData = baseData.map(item => ({
    ...item,
    itemStyle: { color: colorMap[item.name] }
  }));

  const option = {
    title: {
      text: '数据清洗状态分布',
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
      radius: [30, 150],
      center: ['50%', '55%'],
      roseType: 'radius',  // 设置为南丁格尔玫瑰图
      itemStyle: {
        borderRadius: 5,
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