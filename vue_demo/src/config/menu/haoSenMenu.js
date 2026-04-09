/**
 * 豪森项目菜单配置
 */
export const haoSenProjectConfig = {
  brandTitle: '豪森主数据',
  brandSubtitle: '后台管理系统',
  homeRoute: 'HaoSenHome',
  defaultColor: '#9478cc' // 紫色
}

export const haoSenMenuConfig = {
  mainDataManagement: {
    label: '主数据',
    icon: ['fas', 'th-list'],
    children: {
      hospital: {
        label: '医院主数据',
        icon: ['fas', 'hospital-alt'],
        handler: 'showHospitalData'
      },
      drugstore: {
        label: '药店主数据',
        icon: ['fas', 'store-alt'],
        handler: 'showDrugStoreData'
      },
      company: {
        label: '商业主数据',
        icon: ['fas', 'synagogue'],
        handler: 'companyDataView'
      }
    }
  },
  cleanManagement: {
    label: '数据清洗',
    icon: ['fas', 'oil-can'],
    children: {
      selectClean: {
        label: '清洗查看',
        icon: ['fas', 'paw'],
        handler: 'selectCleanData'
      },
      importClean: {
        label: '清洗提交',
        icon: ['fas', 'eraser'],
        handler: 'importCleanData'
      }
    }
  },
  appealManagement: {
    label: '数据申诉',
    icon: ['fas', 'flag'],
    children: {
      showAppeal: {
        label: '申诉查看',
        icon: ['fas', 'book-reader'],
        handler: 'showAppealData'
      },
      importAppeal: {
        label: '申诉提交',
        icon: ['fas', 'cat'],
        handler: 'importAppealData'
      }
    }
  },
  updateManagement: {
    label: '数据更新',
    icon: ['fas', 'file-alt'],
    children: {
      importUpdate: {
        label: '更新提交',
        icon: ['fas', 'snowman'],
        handler: 'importUpdateData'
      }
    }
  },
  repeatDataManagement: {
    label: '数据核查',
    icon: ['fas', 'feather'],
    children: {
      showRepeat: {
        label: '重复数据查看',
        icon: ['fas', 'paper-plane'],
        handler: 'showRepeatData'
      },
      showExportHS: {
        label: '客户确认数据',
        icon: ['fas', 'leaf'],
        handler: 'showExportHSData'
      }
    }
  }
}

/**
 * 豪森项目菜单点击处理函数
 * @param {Object} api - AdminLayout 组件暴露的 API
 * @returns {Object} 处理函数映射
 */
export function createHaoSenMenuHandlers(api) {
  return {
    showHospitalData: (item) => {
      api.addTab('医院主数据', 'HospitalDataView', '/dataBase/hospital')
    },
    showDrugStoreData: (item) => {
      api.addTab('药店主数据', 'DrugStoreDataView', '/dataBase/drugstore')
    },
    companyDataView: (item) => {
      api.addTab('商业主数据', 'CompanyDataView', '/dataBase/company')
    },
    selectCleanData: (item) => {
      api.addTab('清洗查看', 'SelectCleanDataView', '/cleanData/selectCleanData')
    },
    importCleanData: (item) => {
      api.addTab('清洗提交', 'ImportCleanDataView', '/cleanData/ImportCleanData')
    },
    showAppealData: (item) => {
      api.addTab('申诉数据', 'AppealDataView', '/appealData/appealData')
    },
    importAppealData: (item) => {
      api.addTab('申诉提交', 'ImportAppealDataView', '/appealData/importAppealData')
    },
    importUpdateData: (item) => {
      api.addTab('更新提交', 'ImportUpdateDataView', '/updateData/importUpdateData')
    },
    showRepeatData: (item) => {
      api.addTab('重复数据查看', 'GetRepeatDataView', '/repeatData/getRepeatData')
    },
    showExportHSData: (item) => {
      api.addTab('客户确认数据', 'GetExportHSDataView', '/repeatData/exportHSData')
    }
  }
}
