const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave:false,//关闭eslintre语法检查
  devServer: {
    proxy: {
      // 在你的 Vue 配置中，当前的代理设置会将所有请求（'/'）都转发到 http://localhost:8080，这可能会导致静态资源（如 HTML、JS 文件）也被错误地代理到后端
      // 匹配所有以 /api 开头的请求
      '/api': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true, // 允许跨域
        pathRewrite: {
          '^/api': '' // 去掉请求路径中的 /api
        }
      }
    }
  }
})
