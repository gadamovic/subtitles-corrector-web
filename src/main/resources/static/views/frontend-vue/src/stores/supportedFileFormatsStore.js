
import { defineStore } from 'pinia'

export const useSupportedFileFormats = defineStore('supportedFileFormatsInEditorStore', {
  state: () => {
    return {
      supportedFileFormatsInEditor: ['.srt', '.vtt'],
      supportedFileFormatsInTranslator: ['.srt', '.vtt', '.ass']
    }
  }
})

