
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
    },
    updateSubtitleDataTmpCompositeEditOperationsAtIndex(newValue, index){
      this.subtitleDataTmp.lines[index].compEditOperations = newValue;
    },
    updateSubtitleDataTmpLineTextAtIndex(newValue, index){
      this.subtitleDataTmp.lines[index].text = newValue;
    }
  }
})

