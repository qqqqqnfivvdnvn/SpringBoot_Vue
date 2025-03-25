const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave:false,//关闭eslintre语法检查
  devServer: {
    proxy: {

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
