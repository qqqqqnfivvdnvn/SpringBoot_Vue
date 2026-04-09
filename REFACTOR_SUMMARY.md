# 前端重构总结

## 重构目标
消除 `HaoSenHome.vue` 和 `HengRuiHome.vue` 之间的重复代码（约 80% 重复），提取通用的 AdminLayout 布局组件。

---

## 创建的文件

### 1. 通用布局组件
**路径**: `vue_demo/src/components/layout/AdminLayout.vue`

**功能**:
- 顶部导航栏（品牌 Logo、导航按钮、用户信息、退出登录）
- 左侧可折叠侧边栏（动态菜单、子菜单展开/折叠）
- 右侧内容区（多标签页系统、动态组件渲染）
- Toast 消息提示
- 动态主题颜色支持
- 暗黑模式支持

**Props**:
| 属性名 | 类型 | 说明 |
|--------|------|------|
| `project-config` | Object | 项目配置（品牌标题、副标题、默认颜色等） |
| `menu-config` | Object | 菜单配置（菜单项、图标、子菜单） |
| `menu-handlers` | Object | 菜单点击处理函数映射 |
| `default-view` | String | 默认视图组件名称 |

**暴露的方法**:
- `addTab(title, component, hash)` - 添加标签页
- `switchTab(tabId)` - 切换标签页
- `closeTab(tabId)` - 关闭标签页
- `resetToDashboard()` - 重置到仪表盘
- `showToastMessage(message)` - 显示 Toast 消息

---

### 2. 豪森项目菜单配置
**路径**: `vue_demo/src/config/menu/haoSenMenu.js`

**内容**:
- `haoSenProjectConfig` - 项目配置（紫色主题）
- `haoSenMenuConfig` - 菜单配置（主数据、数据清洗、数据申诉、数据更新、数据核查）
- `createHaoSenMenuHandlers(api)` - 菜单点击处理函数工厂

---

### 3. 恒瑞项目菜单配置
**路径**: `vue_demo/src/config/menu/hengRuiMenu.js`

**内容**:
- `hengRuiProjectConfig` - 项目配置（珊瑚橙主题）
- `hengRuiMenuConfig` - 菜单配置（批次管理、数据汇总、数据比对关系）
- `createHengRuiMenuHandlers(api)` - 菜单点击处理函数工厂

---

### 4. 主数据项目菜单配置
**路径**: `vue_demo/src/config/menu/mainDataMenu.js`

**内容**:
- `mainDataProjectConfig` - 项目配置（蓝色主题 `#4a9eff`）
- `mainDataMenuConfig` - 菜单配置（区县经纬度：批次列表、文件上传、汇总数据）
- `createMainDataMenuHandlers(api)` - 菜单点击处理函数工厂

---

### 5. 菜单配置索引
**路径**: `vue_demo/src/config/menu/index.js`

**功能**: 统一导出所有菜单配置

---

## 修改的文件

### 1. HaoSenHome.vue
**重构前**: 1112 行代码（包含大量重复的模板、样式、逻辑）
**重构后**: 70 行代码（仅包含配置和导入）

**代码减少**: 约 94%

---

### 2. HengRuiHome.vue
**重构前**: 1112 行代码
**重构后**: 67 行代码

**代码减少**: 约 94%

---

### 3. main.js
**修改内容**:
- 添加 `faCheckCircle` 图标导入和注册

### 4. MainDataHome.vue
**重构前**: 960 行代码（包含大量重复的模板、样式、逻辑）
**重构后**: 38 行代码（仅包含配置和导入）

**代码减少**: 约 96%

### 5. menu/index.js
**修改内容**:
- 添加 `mainDataMenu` 导出

---

## 技术亮点

### 1. 动态组件解析
使用 Vue 3 的 `resolveComponent` 实现动态组件加载，支持通过字符串名称加载全局注册的组件。

```javascript
const currentViewComponent = computed(() => {
  try {
    return resolveComponent(currentView.value)
  } catch (e) {
    console.warn(`Component "${currentView.value}" not found`)
    return null
  }
})
```

### 2. 动态主题颜色
通过 CSS 变量和 computed 属性实现运行时主题切换：

```javascript
const themeStyle = computed(() => {
  const { primary, dark, light } = themeColors.value
  return {
    '--theme-primary': primary,
    '--theme-gradient': `linear-gradient(135deg, ${primary}, ${dark})`,
    // ... 其他变量
  }
})
```

### 3. 配置驱动菜单
菜单结构和行为完全由配置文件驱动，无需修改组件代码：

```javascript
export const haoSenMenuConfig = {
  mainDataManagement: {
    label: '主数据',
    icon: ['fas', 'th-list'],
    children: {
      hospital: { label: '医院主数据', handler: 'showHospitalData' }
    }
  }
}
```

### 4. 响应式处理函数
使用 computed 确保在组件挂载后创建处理函数：

```javascript
const menuHandlers = computed(() => {
  const api = adminLayoutRef.value || { addTab: () => {}, showToastMessage: () => {} }
  return createHaoSenMenuHandlers(api)
})
```

---

## 使用示例

### 添加新项目（如未来需要）

1. **创建菜单配置** `vue_demo/src/config/menu/newProjectMenu.js`:
```javascript
export const newProjectConfig = {
  brandTitle: '新项目',
  brandSubtitle: '后台管理系统',
  homeRoute: 'NewProjectHome',
  defaultColor: '#7dcf85' // 绿色
}

export const newProjectMenuConfig = {
  dataManagement: {
    label: '数据管理',
    icon: ['fas', 'database'],
    children: {
      list: { label: '数据列表', handler: 'showList' }
    }
  }
}

export function createNewProjectMenuHandlers(api) {
  return {
    showList: (item) => {
      api.addTab('数据列表', 'DataListView', '/newproject/list')
    }
  }
}
```

2. **创建项目首页** `vue_demo/src/components/newproject/NewProjectHome.vue`:
```vue
<template>
  <AdminLayout
    ref="adminLayoutRef"
    :project-config="projectConfig"
    :menu-config="menuConfig"
    :menu-handlers="menuHandlers"
    default-view="DataListView"
  />
</template>

<script setup>
import { ref, computed } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import {
  newProjectConfig,
  newProjectMenuConfig,
  createNewProjectMenuHandlers
} from '@/config/menu/newProjectMenu'

const projectConfig = newProjectConfig
const menuConfig = newProjectMenuConfig
const adminLayoutRef = ref(null)

const menuHandlers = computed(() => {
  const api = adminLayoutRef.value || { addTab: () => {}, showToastMessage: () => {} }
  return createNewProjectMenuHandlers(api)
})
</script>
```

3. **添加路由** `vue_demo/src/router/index.js`:
```javascript
import NewProjectHome from '@/components/newproject/NewProjectHome.vue'

const routes = [
  {
    path: '/newproject',
    name: 'NewProjectHome',
    component: NewProjectHome,
    meta: { requiresAuth: true }
  }
]
```

---

## 重构收益

| 指标 | 重构前 | 重构后 | 改进 |
|------|--------|--------|------|
| 重复代码行数 | ~3300 行 | ~150 行 | 减少 95% |
| 新增项目所需代码 | ~1100 行 | ~40 行 | 减少 96% |
| 样式维护成本 | 高（多处修改） | 低（单点修改） | 显著提升 |
| 功能扩展难度 | 中等 | 低 | 配置驱动 |

---

## 注意事项

1. **全局组件注册**: 所有动态视图组件（如 `BatchDataView`、`HospitalDataView` 等）需要在 `main.js` 中全局注册

2. **图标导入**: 新增的图标需要在 `main.js` 的 `library.add()` 中注册

3. **主题色存储**: 项目主题色通过 `sessionStorage` 存储，key 为 `themeColor_{routeName}`

4. **用户信息**: 用户信息从 `localStorage` 或 `sessionStorage` 读取

---

## 后续优化建议

1. **引入 TypeScript**: 为组件和配置添加类型定义
2. **状态管理**: 考虑使用 Pinia 统一管理应用状态
3. **构建优化**: 考虑使用 lazy loading 优化首屏加载
4. **单元测试**: 为 AdminLayout 组件编写单元测试
