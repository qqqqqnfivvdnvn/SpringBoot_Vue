/**
 * 恒瑞项目菜单配置
 */
export const hengRuiProjectConfig = {
  brandTitle: '恒瑞主数据',
  brandSubtitle: '后台管理系统',
  homeRoute: 'HengRuiHome',
  defaultColor: '#ff9e7d' // 珊瑚橙
}

export const hengRuiMenuConfig = {
  batchManagement: {
    label: '批次管理',
    icon: ['fas', 'layer-group'],
    children: {
      showBatch: {
        label: '批次列表',
        icon: ['fas', 'list'],
        handler: 'showBatchData'
      },
      importBatch: {
        label: '批次导入',
        icon: ['fas', 'upload'],
        handler: 'importBatchData'
      }
    }
  },
  monitoringManagement: {
    label: '数据汇总',
    icon: ['fas', 'table'],
    children: {
      showMonitoring: {
        label: '汇总数据',
        icon: ['fas', 'database'],
        handler: 'showMonitoringData'
      }
    }
  },
  relationManagement: {
    label: '数据比对关系',
    icon: ['fas', 'link'],
    children: {
      showRelation: {
        label: '比对关系',
        icon: ['fas', 'exchange-alt'],
        handler: 'showRelationData'
      },
      importRelation: {
        label: '关系导入',
        icon: ['fas', 'upload'],
        handler: 'importRelationData'
      }
    }
  }
}

/**
 * 恒瑞项目菜单点击处理函数
 * @param {Object} api - AdminLayout 组件暴露的 API
 * @returns {Object} 处理函数映射
 */
export function createHengRuiMenuHandlers(api) {
  return {
    showBatchData: (item) => {
      api.addTab('批次列表', 'BatchDataView', '/hengrui/batch')
    },
    importBatchData: (item) => {
      api.addTab('批次导入', 'ImportBatchDataView', '/hengrui/importBatch')
    },
    showMonitoringData: (item) => {
      api.addTab('数据汇总', 'MonitoringDataView', '/hengrui/monitoring')
    },
    showRelationData: (item) => {
      api.addTab('比对关系', 'OrgRelationView', '/hengrui/relation')
    },
    importRelationData: (item) => {
      api.addTab('关系导入', 'ImportOrgRelationView', '/hengrui/importRelation')
    }
  }
}
