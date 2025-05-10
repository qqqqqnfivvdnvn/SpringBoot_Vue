<template>
  <div ref="chart" style="width: 850px; height: 400px;"></div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import axios from 'axios';

const chart = ref(null);

onMounted(async () => {
  const myChart = echarts.init(chart.value, null, {
    renderer: 'svg',
    devicePixelRatio: window.devicePixelRatio > 1 ? 2 : 1
  });

  try {
    const response = await axios.post('/api/homeData/getCleanBarData');

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

      const option = {
        title: {
          text: '近七天数据清洗趋势',
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
            type: 'shadow',
          },
          textStyle: {
            color: '#333333',
            fontFamily: 'Arial, sans-serif',
          },
        },
        legend: {
          data: ['医院', '药店', '商业', '总计'],
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
          data: dates,
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
        yAxis: [
          {
            type: 'value',
            // name: '数量',
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
          {
            type: 'value',
            // name: '总计',
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
              show: false,
            },
          }
        ],
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
            yAxisIndex: 1,
            data: totalCount,
            smooth: true,  // 保持平滑曲线
            symbol: 'circle',
            symbolSize: 8,
            itemStyle: {
              color: '#9478cc',
            },
            lineStyle: {
              width: 3,
              // 移除了阴影效果
            },
            // label: {
            //   show: true,
            //   position: 'top',
            //   formatter: '{c}',
            //   color: '#9478cc',
            //   fontFamily: 'Arial, sans-serif',
            // },
            emphasis: {
              lineStyle: {
                width: 4
              },
              itemStyle: {
                color: '#427aa4' // 保持悬停颜色一致
              }
            }
          }
        ],
      };

      myChart.setOption(option);
    } else {
      console.error('获取数据失败:', response.data.msg);
    }
  } catch (error) {
    console.error('获取数据失败:', error);
  }
});
</script>