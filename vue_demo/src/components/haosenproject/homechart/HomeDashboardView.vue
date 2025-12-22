<template>
  <div class="dashboard-view">
    <div class="grid-container">
      <!-- 顶部五个数据卡片（保留原 Font Awesome 图标） -->
      <div class="grid-item-container">
        <div class="grid-item-small">
          <font-awesome-icon :icon="['fas', 'hospital']" size="2x" />&nbsp;
          <span class="label">医院周清洗</span>&nbsp;
          <span class="value">{{ cleanCount.hospitalCount }}</span>
        </div>
        <div class="grid-item-small">
          <font-awesome-icon :icon="['fas', 'store']" size="2x" />&nbsp;
          <span class="label">药店周清洗</span>&nbsp;
          <span class="value">{{ cleanCount.drugstoreCount }}</span>
        </div>
        <div class="grid-item-small">
          <font-awesome-icon :icon="['fas', 'university']" size="2x" />&nbsp;
          <span class="label">商业周清洗</span>&nbsp;
          <span class="value">{{ cleanCount.companyCount }}</span>
        </div>
        <div class="grid-item-small">
          <font-awesome-icon :icon="['fas', 'trash']" size="2x" />&nbsp;
          <span class="label">申诉待处理</span>&nbsp;
          <span class="value">{{ cleanCount.unappealingCount }}</span>
        </div>
        <div class="grid-item-small">
          <font-awesome-icon :icon="['fas', 'clock']" size="2x" />&nbsp;
          <span class="label">待清洗</span>&nbsp;
          <span class="value">{{ cleanCount.uncleanedCount }}</span>
        </div>
      </div>

      <!-- 第二行：柱状图 + 饼图 -->
      <div class="grid-item grid-item-4-6">
        <div class="sub-grid-item">
          <BarChart />
        </div>
        <div class="sub-grid-item">
          <PieChart />
        </div>
      </div>

      <!-- 第三行：玫瑰图 + 折线图 -->
      <div class="grid-item grid-item-7-9">
        <div class="sub-grid-item">
          <RosePieChart />
        </div>
        <div class="sub-grid-item">
          <LineChart />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'



// 导入四个图表组件（路径根据你的项目调整）
import BarChart from '@/components/haosenproject/homechart/CleanBarChart.vue'
import PieChart from '@/components/haosenproject/homechart/MainDataPieChart.vue'
import RosePieChart from '@/components/haosenproject/homechart/BranchBarChart.vue'
import LineChart from '@/components/haosenproject/homechart/AppealUpdateChart.vue'


// 数据
const cleanCount = ref({
  uncleanedCount: 0,
  unappealingCount: 0,
  companyCount: 0,
  hospitalCount: 0,
  drugstoreCount: 0
})

// 获取首页数据
const fetchHomeData = async () => {
  try {
    const response = await axios.post('/api/homeData/getHomeData')
    if (response.data.code === 200) {
      cleanCount.value = response.data.data
    } else {
      ElMessage.error('获取数据失败')
    }
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

onMounted(() => {
  fetchHomeData()
})
</script>

<style scoped>
.dashboard-view {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  max-width: min(1250px, 95vw);
  padding: 12px;
  box-sizing: border-box;
  background: white;
  font-size: 12px;
  margin: 0 auto;
}

/* 2K屏幕优化 */
@media (min-width: 2000px) and (max-width: 2600px) {
  .dashboard-view {
    max-width: min(2200px, 96vw);
  }
}

/* 超宽屏幕 */
@media (min-width: 2600px) {
  .dashboard-view {
    max-width: min(2400px, 95vw);
  }
}

/* 网格布局 */
.grid-container {
  display: grid;
  grid-template-rows: 100px 1fr 1fr;
  gap: 6px;
  height: 100%;
  width: 100%;
}

/* 顶部五个卡片容器 */
.grid-item-container {
  grid-column: 1 / -1;
  display: flex;
  gap: 6px;
  height: 100px;
}

.grid-item-small {
  flex: 1;
  background-color: #fff;
  border: 1px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 3px;
  transition: box-shadow 0.2s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.grid-item-small:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.grid-item-small .label {
  font-weight: bold;
  font-size: 16px;
  color: #000000;
}

.grid-item-small .value {
  font-size: 16px;
  color: #2575fc;
  font-weight: bold;
}

/* 第二、三行大区域 */
.grid-item-4-6,
.grid-item-7-9 {
  grid-column: 1 / -1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px;
}

.sub-grid-item {
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 3px;
  transition: box-shadow 0.2s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.sub-grid-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 图表充满容器 */
.sub-grid-item :deep(.chart-container),
.sub-grid-item :deep(canvas) {
  width: 100% !important;
  height: 100% !important;
  position: absolute;
  top: 0;
  left: 0;
}
</style>