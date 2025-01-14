<template>

    <LoaderComponent v-if="isLoading" :isActive="true"></LoaderComponent>

    <div v-for="(subtitle, key) in subtitleDataStore.subtitleDataTmp.lines" :key="key">
        <textarea class="textarea" rows="2" v-model="subtitle.text" @change="updateData"
            style="resize:none;"></textarea>
    </div>

</template>

<script>

import { useSubtitleDataStore } from '@/stores/subtitleDataStore';
import { nextTick } from 'vue';
import LoaderComponent from './LoaderComponent.vue';

export default {

    name: "SubtitleContentComponent",
    components: { LoaderComponent },
    data() {
        return {
            subtitleDataStore: useSubtitleDataStore(),
            isLoading: true,
            tickCounter: 1
        }
    },
    methods: {

    },
    mounted: function () {
        nextTick(() => {
            console.log("Tick counter: " + this.tickCounter)
            this.tickCounter++;
            this.isLoading = false; // Hide loader after rendering

        });
    }

}

</script>