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
      '/api': {
        target:
          process.env.VUE_APP_ENVIRONMENT === 'prod'
            ? 'https://subtitles-corrector.com'
            : 'http://localhost:8080', // Backend server
        changeOrigin: true,
        pathRewrite: {
          '^/api': '', // Remove `/api` from the request before sending it to the backend
        },
        onProxyReq: (proxyReq, req) => {
          console.log(`Proxying request: ${req.url}`);
        },
        onProxyRes: (proxyRes, req) => {
          console.log(`Received response for: ${req.url}`);
        },
      },
    },
  },
})

