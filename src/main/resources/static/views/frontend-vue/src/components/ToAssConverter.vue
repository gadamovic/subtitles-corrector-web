<template>
    <ToSpecificFormatConverter targetFormat="ASS" :supportedFileFormats="['.srt', '.vtt']"></ToSpecificFormatConverter>
</template>

<script>
import ToSpecificFormatConverter from './ToSpecificFormatConverter.vue';

export default {
    name: "ToAssConverter",
    components: { ToSpecificFormatConverter },
    data: function () {
        return {

        }
    },
    methods: {
      
    }
}
</script>