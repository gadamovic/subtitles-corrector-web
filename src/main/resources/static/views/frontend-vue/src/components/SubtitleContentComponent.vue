<template>

    <LoaderComponent v-if="loaderStore.isLoading" :isActive="true"></LoaderComponent>

    <div v-for="(subtitle, key) in subtitleDataStore.subtitleDataTmp.lines" :key="key">

        <SubtitleLine :subtitle="subtitle" :lineIndex="key"></SubtitleLine> <br />

        <div v-if="lineVisibleFlagsStore.isVisibleFlags[key]">
            <textarea :id="'ta-' + key" class="textarea" rows="2" :value="subtitle.text"
                style="resize:none;"></textarea>
            <i class="fas fa-save" @click="saveSubtitleLine(subtitle.textBeforeCorrection, key)"></i>
        </div>

    </div>

</template>

<script>

import { useSubtitleDataStore } from '@/stores/subtitleDataStore';
import { useLoaderStore } from '@/stores/subtitleContentComponentLoaderStore';
import LoaderComponent from './LoaderComponent.vue';
import SubtitleLine from './SubtitleLine.vue';
import '@fortawesome/fontawesome-free/css/all.min.css';
import { useLineVisibleFlagsStore } from '@/stores/subtitleLineVisibleFlagsStore'
import DOMPurify from "dompurify";

export default {

    name: "SubtitleContentComponent",
    components: { LoaderComponent, SubtitleLine },
    data() {
        return {
            subtitleDataStore: useSubtitleDataStore(),
            loaderStore: useLoaderStore(),
            lineVisibleFlagsStore: useLineVisibleFlagsStore()
        }
    },
    methods: {
        async saveSubtitleLine(beforeCorrection, key) {

            let textareaValue = document.getElementById('ta-' + key).value;
            textareaValue = textareaValue.replace('\n', "<br/>")

            console.log(textareaValue)
            textareaValue = DOMPurify.sanitize(textareaValue, {
                ALLOWED_TAGS: ["br"], // Allow only <br> tags
            });
            console.log(textareaValue)

            let response = await fetch(("api/rest/1.0/getStringDifferences?s1=" + beforeCorrection + "&s2=" + textareaValue), {
                method: "GET",
                headers: new Headers({ 'Content-Type': 'application/json' })
            });

            let result = await response.json();

            this.subtitleDataStore.updateSubtitleDataTmpCompositeEditOperationsAtIndex(result, key);
            this.lineVisibleFlagsStore.toggleFlag(key);

            this.subtitleDataStore.updateSubtitleDataTmpLineTextAtIndex(textareaValue, key)

        }
    },

}

</script>