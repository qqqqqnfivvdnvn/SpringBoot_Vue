<template>
  <div ref="chart" style="width: 850px; height: 400px;"></div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import axios from 'axios'; // 引入 axios

// 定义图表 DOM 元素的引用
const chart = ref(null);

// 在组件挂载后初始化图表
onMounted(async () => {
  // 初始化 ECharts 实例
  // const myChart = echarts.init(chart.value);
  const myChart = echarts.init(chart.value, null, {
    renderer: 'svg',
    devicePixelRatio: window.devicePixelRatio > 1 ? 2 : 1
  });


    // 从后端获取数据
  const response = await axios.post('/api/homeData/getCleanBarData'); // 替换为你的后端 API 地址

  let formattedData;

  try {
      if (response.data.code === 200) {
        // 提取数据
      const backendData = response.data.data; // 获取嵌套的data
        const dates = response.data.data.map(item => item.day);
        const companyCount = response.data.data.map(item => item.companyCount);
        const drugstoreCount = response.data.data.map(item => item.drugstoreCount);
        const hosCount = response.data.data.map(item => item.hosCount);

        // 构建前端需要的数据格式
       formattedData = {
          dates: dates,
          hospital: hosCount,
          drugstore: drugstoreCount,
          company: companyCount
        };

      } else {
        console.error('Failed to fetch data:', response.data.msg);
      }

    // const data ={
    //   "dates": ["一号", "二号", "三号", "四号", "五号", "六号", "七号"],
    //   "hospital": [30, 40, 50, 23, 30, 40, 88],
    //   "drugstore": [20, 30, 40, 50, 40, 66, 77],
    //   "company": [10, 20, 30, 45, 69, 90, 100]
    // }

    // ECharts 配置项
    const option = {
      title: {
        text: '近七天数据清洗趋势',
        left: 'center',
        textStyle: {
          color: '#333333', // 标题字体颜色
          fontSize: 18,
          fontWeight: 'bold',
          fontFamily: 'Arial, sans-serif',
        },
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow', // 鼠标悬停时显示阴影
        },
        textStyle: {
          color: '#333333', // 提示文字颜色
          fontFamily: 'Arial, sans-serif',
        },
      },
      legend: {
        data: ['医院', '药店', '商业'], // 图例
        bottom: 10, // 图例位置
        textStyle: {
          color: '#333333', // 图例字体颜色
          fontFamily: 'Arial, sans-serif',
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        containLabel: true,
        backgroundColor: '#F5F5F5', // 背景颜色
        borderColor: '#E0E0E0', // 边框颜色
      },
      xAxis: {
        type: 'category',

        data: formattedData.dates, // 从后端获取的日期数据
        axisLine: {
          lineStyle: {
            color: '#E0E0E0', // x 轴线颜色
          },
        },
        axisLabel: {
          color: '#333333', // x 轴标签颜色
          fontFamily: 'Arial, sans-serif',
        },
      },
      yAxis: {
        type: 'value',
        // name: '条数',
        axisLine: {
          lineStyle: {
            color: '#000000', // y 轴线颜色
          },
        },
        axisLabel: {
          color: '#333333', // y 轴标签颜色
          fontFamily: 'Arial, sans-serif',
        },
        splitLine: {
          lineStyle: {
            color: '#E0E0E0', // 网格线颜色
          },
        },
      },
      series: [
        {
          name: '医院',
          type: 'bar',
          data:  formattedData.hospital, // 从后端获取的医院数据
          itemStyle: {
            color: '#6da1e4', // 浅蓝色
            borderRadius: [5, 5, 0, 0], // 顶部圆角
          },
        },
        {
          name: '药店',
          type: 'bar',
          data: formattedData.drugstore, // 从后端获取的药店数据
          itemStyle: {
            color: '#40b786', // 浅绿色
            borderRadius: [5, 5, 0, 0], // 顶部圆角
          },
        },
        {
          name: '商业',
          type: 'bar',
          data: formattedData.company, // 从后端获取的商业数据
          itemStyle: {
            color: '#f9a200', // 浅橙色
            borderRadius: [5, 5, 0, 0], // 顶部圆角
          },
        },
      ],
    };

    // 设置配置项并渲染图表
    myChart.setOption(option);
  } catch (error) {
    console.error('Failed to fetch data:', error);
  }
});
</script>

<style scoped>
/* 可以在这里添加样式 */
</style>