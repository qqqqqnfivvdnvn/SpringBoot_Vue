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

  // 基础数据
  const baseData = [
    { value: 335, name: '医院' },
    { value: 310, name: '药店' },
    { value: 234, name: '商业' }
  ];
  
  // 颜色配置
  const colorMap = {
    '医院': '#6DA9E4',
    '药店': '#7DCEA0',
    '商业': '#f8c181'
  };
  
  // 动态合并
  const processedData = baseData.map(item => ({
    ...item,
    itemStyle: { color: colorMap[item.name] }
  }));

  const option = {
    // 问题1: 这里使用了未定义的colors变量，应该删除或替换为color配置
    // color: colors, // 错误：colors未定义
    
    title: {
      text: '主数据数据占比',
      left: 'center',
      textStyle: {
        fontSize: 18,
        fontWeight: 'bold'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 20,
      top: 'center',
      // 问题2: 需要添加data属性显示图例
      data: baseData.map(item => item.name)
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
        borderJoin: 'round'
      },
      label: {
        show: true,
        formatter: '{b}: {d}%',
        fontSize: 12
      },
      emphasis: {
        scale: true,
        scaleSize: 5,
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.2)'
        }
      },
      labelLine: {
        smooth: 0.2,
        length: 10,
        length2: 15
      },
      data: processedData
    }]
  };

  myChart.setOption(option);

  // 响应式调整
  window.addEventListener('resize', () => myChart.resize());
});
</script>

<style scoped>
/* 可以在这里添加样式 */
</style>