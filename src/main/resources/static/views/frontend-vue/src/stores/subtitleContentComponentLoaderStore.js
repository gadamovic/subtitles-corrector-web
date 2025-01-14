
import { defineStore } from 'pinia'

export const useLoaderStore = defineStore('subtitleContentLoaderStore', {
  state: () => {
    return {
         isLoading: false,
        }
  },
  actions: {
    setIsLoading(newValue){
        this.isLoading = newValue;
    },
  }
})

