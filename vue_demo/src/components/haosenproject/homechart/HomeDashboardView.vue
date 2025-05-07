

<template>
  <div class="dashboard-view">
    <div class="grid-container">
      <div class="grid-item-container">

        <!-- 五个数据卡片 -->

        <div class="grid-item-small">
          <font-awesome-icon :icon="['fas', 'hospital'] " size="2x"></font-awesome-icon> &nbsp;&nbsp;
          <div style="display: flex; flex-direction: column; align-items: center;  ">
            <span style="font-weight: bold;font-size: 24px;color:#000000 ">医院周清洗</span>
            <span style="font-size: 22px; color:#2575fc;font-weight: bold ">{{ cleanCount.hospitalCount }} </span>
          </div>

        </div>

        <div class="grid-item-small" >
          <font-awesome-icon :icon="['fas', 'store'] " size="2x"></font-awesome-icon> &nbsp;&nbsp;

          <div style="display: flex; flex-direction: column; align-items: center;  ">
            <span style="font-weight: bold;font-size: 24px;color:#000000 ">药店周清洗</span>
            <span style="font-size: 22px; color:#2575fc;font-weight: bold ">{{ cleanCount.drugstoreCount }} </span>
          </div>

        </div>

        <div class="grid-item-small">
          <font-awesome-icon :icon="['fas', 'university'] " size="2x"></font-awesome-icon> &nbsp;&nbsp;
          <div style="display: flex; flex-direction: column; align-items: center;  ">
            <span style="font-weight: bold;font-size: 24px;color:#000000 ">商业周清洗</span>
            <span style="font-size: 22px; color:#2575fc;font-weight: bold ">{{  cleanCount.companyCount }} </span>
          </div>
        </div>

        <div class="grid-item-small">
          <font-awesome-icon :icon="['fas', 'trash'] " size="2x"></font-awesome-icon> &nbsp;&nbsp;
          <div style="display: flex; flex-direction: column; align-items: center;  ">
            <span style="font-weight: bold;font-size: 24px;color:#000000 ">申诉待处理</span>
            <span style="font-size: 22px; color:#2575fc;font-weight: bold ">{{  cleanCount.unappealingCount }} </span>
          </div>
        </div>

        <div class="grid-item-small">
          <font-awesome-icon :icon="['fas', 'clock'] " size="2x"></font-awesome-icon> &nbsp;&nbsp;
          <div style="display: flex; flex-direction: column; align-items: center;  ">
            <span style="font-weight: bold;font-size: 24px;color:#000000 ">待清洗</span>
            <span style="font-size: 22px; color:#2575fc;font-weight: bold ">{{  cleanCount.uncleanedCount }} </span>
          </div>
        </div>


      </div>

      <!-- 图表区域 -->
      <div class="grid-item grid-item-4-6">
        <div class="sub-grid-item">
          <BarChart />
        </div>
        <div class="sub-grid-item">
          <PieChart />
        </div>
      </div>

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


<script>
import BarChart from '@/components/haosenproject/homechart/CleanBarChart.vue';
import PieChart from '@/components/haosenproject/homechart/MainDataPieChart.vue';
import LineChart from '@/components/haosenproject/homechart/AppealUpdateChart.vue';
import RosePieChart from '@/components/haosenproject/homechart/BranchBarChart.vue';
import axios from 'axios';

export default {
  components: {
    BarChart,
    PieChart,
    LineChart,
    RosePieChart
  },

  data() {
    return {
      cleanCount: {
        uncleanedCount: 0,
        unappealingCount: 0,
        companyCount: 0,
        hospitalCount: 0,
        drugstoreCount: 0
      },
      loading: false, // 可选：添加加载状态
      error: null     // 可选：添加错误状态
    };
  },

  async created() {
    await this.fetchHomeData(); // ✅ 现在 this.fetchHomeData 是可用的
  },

  methods: { // 所有方法必须放在 methods 对象里
    async fetchHomeData() {
      try {
        const response = await axios.post('/api/homeData/getHomeData');
        if (response.data.code === 200) {
          this.cleanCount = response.data.data;
        }
      } catch (error) {
        console.error('获取数据失败:', error);
        this.error = error.message; // 可选：存储错误信息
      }
    }
  }
};
</script>


<style scoped>

/* 网格布局 */
.grid-container {
  display: grid;
  grid-template-rows: 150px 1fr 1fr; /* 第一行高度缩小，第二行和第三行平分剩余空间 */
  gap: 10px; /* 网格之间的间距 */
  height: 100%;
}

/* 新增样式：五个盒子的容器 */
.grid-item-container {
  grid-column: 1 / -1; /* 占据整行 */
  display: flex; /* 使用 Flexbox 布局 */
  gap: 10px; /* 盒子之间的间距 */
  height: 150px; /* 高度与原来的 grid-item-1-3 一致 */
}

/* 新增样式：每个小盒子的样式 */
.grid-item-small {
  flex: 1; /* 每个盒子平分宽度 */
  background-color: #fff; /* 背景色为白色 */
  border: 1px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: #333;
  border-radius: 5px;
  transition: box-shadow 0.2s;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* 悬浮效果 */
}

.grid-item-small:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2); /* 悬浮时阴影加深 */
}

/* 第二行和第三行：合并 4、5、6 格和 7、8、9 格，并分为两个格子 */
.grid-item-4-6,
.grid-item-7-9 {
  grid-column: 1 / -1; /* 占据整行 */
  display: grid;
  grid-template-columns: 1fr 1fr; /* 分为两个格子 */
  gap: 10px; /* 子网格之间的间距 */
}

/* 子网格项样式 */
.sub-grid-item {
  background-color: #fff; /* 背景色为白色 */
  border: 1px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: #333;
  border-radius: 5px;
  transition: box-shadow 0.2s;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* 悬浮效果 */
}

.sub-grid-item:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2); /* 悬浮时阴影加深 */
}

</style>