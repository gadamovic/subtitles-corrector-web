
import { defineStore } from 'pinia'

export const useSupportedFileFormatsInEditorStore = defineStore('supportedFileFormatsInEditorStore', {
  state: () => {
    return {
      supportedFileFormatsInEditor: ['.srt', '.vtt'],
    }
  }
})

