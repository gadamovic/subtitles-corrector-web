
import { defineStore } from 'pinia'

export const useSupportedFileFormatsStore = defineStore('supportedFileFormatsStore', {
  state: () => {
    return {
      supportedFileFormats: ['.srt', '.vtt'/*, '.sub', '.txt'*/],
    }
  }
})

