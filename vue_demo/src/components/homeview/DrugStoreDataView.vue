
<template>
  <div class="hospital-data-view">
    <div class="view-header">
      <button @click="$emit('back')" class="back-button">
        <font-awesome-icon :icon="['fas', 'arrow-left']" />
        返回首页
      </button>
      <h2>药店主数据</h2>

    </div>


    <!-- 这里放置你的医院数据表格和查询组件 -->
    <div class="data-container">
      <el-table :data="hospitalData" style="width: 100%">
        <!-- 表格列定义 -->
        <el-table-column prop="name" label="药店名称"></el-table-column>
        <el-table-column prop="code" label="药店编码"></el-table-column>
        <el-table-column prop="address" label="地址"></el-table-column>
        <!-- 更多列... -->
      </el-table>
    </div>



  </div>
</template>

<script>
export default {
  data() {
    return {
      hospitalData: [], // 从API获取的数据
      loading: false
    };
  },
  mounted() {
    this.fetchHospitalData();
  },
  methods: {
    async fetchHospitalData() {
      this.loading = true;
      try {
        const res = await axios.get('/api/hospitals');
        this.hospitalData = res.data;
      } catch (error) {
        console.error('获取药店数据失败:', error);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.hospital-data-view {
  display: flex; /* 将容器设置为弹性布局 */
  flex: 1;       /* 让容器自身也能灵活分配空间 */
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.view-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.back-button {
  background: #af96e6;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.back-button:hover {
  background: #9478cc;
}

.data-container {
  margin-top: 20px;
}
</style>