const { defineConfig } = require('@vue/cli-service')

var vuePublicPath = '/subtitles/views';
if (process.env.SUBTITLES_APPLICATION_ENVI === 'prod') {
  vuePublicPath = "/views";
}

module.exports = defineConfig({
  transpileDependencies: true,
  publicPath: vuePublicPath
})
