const { defineConfig } = require('@vue/cli-service')

var vuePublicPath = '/subtitles/views';
if (process.env.VUE_APP_ENVIRONMENT === 'prod') {
  vuePublicPath = "/views";
}

const isDevelopmentServerMode = process.env.NODE_ENV === 'development';
if(isDevelopmentServerMode){
  vuePublicPath = '/';
}

module.exports = defineConfig({
  transpileDependencies: true,
  publicPath: vuePublicPath,
  devServer: {
    proxy: {
      '/api/rest/1.0': {
        target: 'http://localhost:8080/subtitles', // Backend server
        changeOrigin: true,
      },
      '/views': {
        target: 'http://localhost:8080/subtitles', // Take views from tomcat server
        changeOrigin: true,
      },
    },
  },
})

