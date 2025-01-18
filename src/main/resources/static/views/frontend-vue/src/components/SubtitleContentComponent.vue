<template>

    <LoaderComponent v-if="loaderStore.isLoading" :isActive="true"></LoaderComponent>

    <!-- <div v-for="(subtitle, key) in subtitleDataStore.subtitleDataTmp.lines" :key="key">
        <textarea class="textarea" rows="2" v-model="subtitle.text" style="resize:none;"></textarea>
    </div> -->

    <div v-for="(subtitle, key) in subtitleDataStore.subtitleDataTmp.lines" :key="key">
        <textarea class="textarea" rows="2" v-model="subtitle.text" style="resize:none;" v-if="false"></textarea>

        <SubtitleLine :subtitle="subtitle"></SubtitleLine>

    </div>

</template>

<script>

import { useSubtitleDataStore } from '@/stores/subtitleDataStore';
import { useLoaderStore } from '@/stores/subtitleContentComponentLoaderStore';
import LoaderComponent from './LoaderComponent.vue';
import SubtitleLine from './SubtitleLine.vue';

export default {

    name: "SubtitleContentComponent",
    components: { LoaderComponent, SubtitleLine },
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