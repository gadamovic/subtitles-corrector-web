<template>
    <ToSpecificFormatConverter targetFormat="SRT" :supportedFileFormats="['.vtt', '.ass']"></ToSpecificFormatConverter>
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