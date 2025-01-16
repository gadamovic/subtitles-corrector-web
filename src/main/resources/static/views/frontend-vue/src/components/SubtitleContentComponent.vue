<template>

    <LoaderComponent v-if="loaderStore.isLoading" :isActive="true"></LoaderComponent>

    <div v-for="(subtitle, key) in subtitleDataStore.subtitleDataTmp.lines" :key="key">
        <textarea class="textarea" rows="2" v-model="subtitle.text" style="resize:none;"></textarea>
    </div>

</template>

<script>

import { useSubtitleDataStore } from '@/stores/subtitleDataStore';
import { useLoaderStore } from '@/stores/subtitleContentComponentLoaderStore';
import LoaderComponent from './LoaderComponent.vue';

export default {

    name: "SubtitleContentComponent",
    components: { LoaderComponent },
    data() {
        return {
            subtitleDataStore: useSubtitleDataStore(),
            loaderStore: useLoaderStore(),
        }
    },
    methods: {

    },

}

</script>