<template>

    <div v-for="(subtitle, key) in subtitleDataStore.subtitleDataTmp.lines" :key="key">
        <textarea class="textarea" rows="2" v-model="subtitle.text" @change="updateData" style="resize:none;"></textarea>
    </div>

</template>

<script>

import { useSubtitleDataStore } from '@/stores/subtitleDataStore';

export default {

    name: "SubtitleContentComponent",
    // props: {
    //     subtitleData: Object
    // },
    data() {
        return {
            subtitleDataStore: useSubtitleDataStore()
        }
    },

}

</script>