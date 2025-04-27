<template>
  <table class="hospital-table">
    <tr v-for="item in hospitalData.content" :key="item.dataId">
      <!-- 主行 -->
      <td @click="toggleExpand(item.dataId)">
        {{ item.name }}
        <span class="toggle-icon">{{ expandedRows[item.dataId] ? '▲' : '▼' }}</span>
      </td>
      <!-- 其他关键字段... -->

      <!-- 可折叠的详细行 -->
      <tr v-if="expandedRows[item.dataId]" class="expanded-row">
        <td colspan="6">
          <div class="detail-grid">
            <div><strong>地址：</strong>{{ item.address }}</div>
            <div><strong>法人：</strong>{{ item.legalperson }}</div>
            <!-- 其他字段以网格形式展示 -->
          </div>
        </td>
      </tr>
    </tr>
  </table>
</template>

<script>
export default {
  data() {
    return {
      expandedRows: {}
    }
  },
  methods: {
    toggleExpand(id) {
      this.$set(this.expandedRows, id, !this.expandedRows[id]);
    }
  }
}
</script>

<style>
.detail-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}
.expanded-row {
  background-color: #f9f9f9;
}
.toggle-icon {
  margin-left: 8px;
  cursor: pointer;
}
</style>