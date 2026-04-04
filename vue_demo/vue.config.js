const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({

  publicPath: '/', // 使用绝对路径
  outputDir: 'dist',
  assetsDir: 'static', // 静态资源放在 static 目录
  indexPath: 'index.html',
  productionSourceMap: false,

  transpileDependencies: true,
  lintOnSave: false,

  chainWebpack: config => {
    config.plugin('define').tap(args => {
      args[0]['__VUE_PROD_HYDRATION_MISMATCH_DETAILS__'] = false
      return args
    })
  },

  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  }
})
