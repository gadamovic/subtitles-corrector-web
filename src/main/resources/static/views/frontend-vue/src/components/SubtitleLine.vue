<template>

    <div v-if="!lineVisibleFlagsStore.isVisibleFlags[lineIndex]" class="box">
        <div class="has-text-grey-light has-text-left is-size-7">{{ timestampFrom.formattedTimestamp }} -> {{ timestampTo.formattedTimestamp }}</div>
        <div class="has-text-centered">
            <span v-if="subtitle.compEditOperations">

                <span v-for="(operation, key) in subtitle.compEditOperations" :key="key">


                    <span v-if="operation.type == 'REPLACE'" style="display: inline-flex;">

                        <span class="has-text-danger"><del v-html="operation.str1"></del></span>
                        <span class="has-text-success" v-html="operation.str2"></span>

                    </span>

                    <span v-if="operation.type == 'DELETE'">

                        <del class="has-text-danger" v-html="operation.str1"></del>

                    </span>


                    <span v-if="operation.type == 'ADD'">

                        <span class="has-text-success" v-html="operation.str1"></span>

                    </span>


                    <span v-if="operation.type == 'KEEP'">

                        <span class="has-text-grey" v-html="operation.str1"></span>

                    </span>
                </span>
            </span>
        </div>
        <div class="mt-2 has-text-right">
            <button class="button is-small is-primary is-light" @click="editSubtitle(lineIndex)">
                <span class="icon">
                    <i class="fas fa-edit"></i>
                </span>
                <span>Edit</span>
            </button>
        </div>

    </div>
</template>

<script>

import { useLineVisibleFlagsStore } from '@/stores/subtitleLineVisibleFlagsStore'
import { useSubtitleDataStore } from '@/stores/subtitleDataStore';

export default {
    name: "SubtitleLine",
    props: {
        subtitle: Object,
        lineIndex: Number
    },
    data: function () {
        return {
            lineVisibleFlagsStore: useLineVisibleFlagsStore(),
            subtitleDataStore: useSubtitleDataStore(),
        }
    },
    methods: {
        editSubtitle(lineIndex) {

            this.lineVisibleFlagsStore.toggleFlag(lineIndex);
            let textValue = this.subtitleDataStore.subtitleDataTmp.lines[lineIndex].text;
            textValue = textValue.replaceAll("<br>", "\n");
            this.subtitleDataStore.updateSubtitleDataTmpLineTextAtIndex(textValue, lineIndex)
        }
    },
    computed: {
        timestampFrom() {
            //let todo = "Fali validacija, reset dugme i editovanje pojedinacnih timestampova, vidi da l pozadina moze da se razvuce";
            if (this.subtitle.timestampFromShifted != null && typeof (this.subtitle.timestampFromShifted) != 'undefined') {
                return this.subtitle.timestampFromShifted;
            } else {
                return this.subtitle.timestampFrom;
            }
        },
        timestampTo() {
            if (this.subtitle.timestampToShifted != null && typeof (this.subtitle.timestampToShifted) != 'undefined') {
                return this.subtitle.timestampToShifted;
            } else {
                return this.subtitle.timestampTo;
            }
        }
    }


}
</script>