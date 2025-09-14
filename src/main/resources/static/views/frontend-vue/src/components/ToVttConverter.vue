<template>
    <ToSpecificFormatConverter targetFormat="VTT" :supportedFileFormats="['.srt', '.ass']"></ToSpecificFormatConverter>
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