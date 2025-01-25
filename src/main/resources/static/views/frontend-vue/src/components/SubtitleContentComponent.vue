<template>

    <LoaderComponent v-if="loaderStore.isLoading" :isActive="true"></LoaderComponent>

    <div v-for="(subtitle, key) in subtitleDataStore.subtitleDataTmp.lines" :key="key" class="mb-3">

        <SubtitleLine :subtitle="subtitle" :lineIndex="key"></SubtitleLine>

        <transition name="fade">
            <div v-if="lineVisibleFlagsStore.isVisibleFlags[key]" class="box">

                <!-- Textarea and Save Button -->
                <div class="is-align-items-center is-justify-content-space-between">

                    <!-- Editable Textarea -->
                    <textarea :id="'ta-' + key" class="textarea is-small" rows="2" v-model="subtitle.text"
                        style="resize: none; flex-grow: 1; background-color: white; color: black;"
                        placeholder="Edit your subtitle here...">
                    </textarea>

                    <!-- Save Button -->
                    <div class="has-text-right">
                        <button class="button is-small is-success is-light ml-2 mt-2"
                            @click="saveSubtitleLine(subtitle.textBeforeCorrection, key)">
                            <span class="icon">
                                <i class="fas fa-save"></i>
                            </span>
                            <span>Save</span>
                        </button>
                    </div>
                </div>
            </div>
        </transition>
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
            textareaValue = textareaValue.replaceAll("\n", "<br>")

            textareaValue = DOMPurify.sanitize(textareaValue, {
                ALLOWED_TAGS: ["br", "b", "i", "font"], // Allowed html tags
            });

            let response = await fetch(("api/rest/1.0/getStringDifferences?s1=" + encodeURIComponent(beforeCorrection) + "&s2=" + encodeURIComponent(textareaValue)), {
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

<style>
/* Fade transition styles */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}
</style>
