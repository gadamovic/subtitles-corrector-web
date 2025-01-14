const { defineConfig } = require('@vue/cli-service')

var vuePublicPath = '/subtitles/views';
if (process.env.VUE_APP_ENVIRONMENT === 'prod') {
  vuePublicPath = "/views";
}

module.exports = defineConfig({
  transpileDependencies: true,
  publicPath: vuePublicPath,
  devServer: {
    proxy: {
      '/api/rest/1.0': {
        target: 'http://localhost:8080/', // Backend server
        changeOrigin: true,
        pathRewrite: {
          '^/subtitles/views': '/subtitles', // Remove `//subtitles` from the request before sending it to the backend
        },
      },
    },
  },
})

