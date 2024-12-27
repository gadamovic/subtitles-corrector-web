const { defineConfig } = require('@vue/cli-service')

var vuePublicPath = '/subtitles/views';
if (process.env.APP_ENVIRONMENT === 'prod') {
  vuePublicPath = "/views";
}

module.exports = defineConfig({
  transpileDependencies: true,
  publicPath: vuePublicPath
})
