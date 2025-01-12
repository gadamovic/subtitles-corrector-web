<template>

    <div v-for="(subtitle, key) in subtitleData.lines" :key="key">
        <textarea class="textarea" rows="2" v-model="subtitle.text" style="resize:none;"></textarea>
    </div>

</template>

<script>

export default {

    name:"SubtitleContentComponent",
    props: {
        subtitleData: Object
    },

}

</script>