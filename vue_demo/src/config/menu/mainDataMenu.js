// 主数据项目配置（蓝色主题）
export const mainDataProjectConfig = {
  brandTitle: '主数据管理',
  brandSubtitle: '后台管理系统',
  homeRoute: 'MainDataHome',
  defaultColor: '#4a9eff'
}

// 主数据菜单配置
export const mainDataMenuConfig = {
  locationData: {
    label: '区县经纬度',
    icon: ['fas', 'map-marked-alt'],
    children: {
      batch: { label: '批次列表', icon: ['fas', 'list'], handler: 'showBatchList' },
      upload: { label: '文件上传', icon: ['fas', 'upload'], handler: 'showUpload' },
      location: { label: '汇总数据', icon: ['fas', 'database'], handler: 'showLocationData' }
    }
  }
}

// 菜单点击处理函数工厂
export function createMainDataMenuHandlers(api) {
  return {
    showBatchList: (item) => {
      api.addTab('批次列表', 'MdLocationBatchView', '/maindata/batch')
    },
    showUpload: (item) => {
      api.addTab('文件上传', 'MdLocationUploadView', '/maindata/upload')
    },
    showLocationData: (item) => {
      api.addTab('汇总数据', 'MdLocationView', '/maindata/location')
    }
  }
}
