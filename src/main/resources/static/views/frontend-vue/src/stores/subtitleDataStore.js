
import { defineStore } from 'pinia'

export const useSubtitleDataStore = defineStore('subtitleDataStore', {
  state: () => {
    return {
         subtitleData: Object,
         subtitleDataTmp: Object
        }
  },
  actions: {
    setSubtitleData(newValue){
        this.subtitleData = newValue;
    },
    setSubtitleDataTmp(newValue){
        this.subtitleDataTmp = newValue;
    }
  }
})

