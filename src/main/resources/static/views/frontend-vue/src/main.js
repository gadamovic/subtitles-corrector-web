import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'

// Import Bulma CSS
import '../bulma-customizations.css'

const pinia = createPinia()
const app = createApp(App)

app.use(pinia)

app.mount('#app')
