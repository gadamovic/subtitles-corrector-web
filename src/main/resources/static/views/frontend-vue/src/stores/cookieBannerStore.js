
import { defineStore } from 'pinia'

export const useCookieBannerStore = defineStore('cookieBannerStore', {
  state: () => {
    return {
      showBanner: false
    }
  },
  actions: {
    setValue(newValue) {
      this.showBanner = newValue;
    },
    toggleFlag() {
      this.showBanner = !this.showBanner;
    }
  }
})

