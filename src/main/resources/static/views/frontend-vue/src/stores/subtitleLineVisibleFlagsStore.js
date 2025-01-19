
import { defineStore } from 'pinia'

export const useLineVisibleFlagsStore = defineStore('subtitleLineVisibleFlagsStore', {
  state: () => {
    return {
      isVisibleFlags: []
    }
  },
  actions: {
    setValue(newValue) {
      this.isVisibleFlags = newValue;
    },
    toggleFlag(index) {
      this.isVisibleFlags[index] = !this.isVisibleFlags[index];
    }
  }
})

