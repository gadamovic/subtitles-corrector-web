<template>

    <div v-if="!lineVisibleFlagsStore.isVisibleFlags[lineIndex]">

        <span v-if="subtitle.editOperations">

            <span v-for="(operation, key) in subtitle.compEditOperations" :key="key">


                <span v-if="operation.type == 'REPLACE'" style="display: inline-flex;">

                    <span style="color: red"><del v-html="operation.str1"></del></span>
                    <span style="color: green" v-html="operation.str2"></span>

                </span>

                <span v-if="operation.type == 'DELETE'">

                    <del style="color: red" v-html="operation.str1"></del>

                </span>


                <span v-if="operation.type == 'ADD'">

                    <span style="color: green" v-html="operation.str1"></span>

                </span>


                <span v-if="operation.type == 'KEEP'">

                    <span v-html="operation.str1"></span>

                </span>
            </span>
            <i class="fas fa-edit" @click="lineVisibleFlagsStore.toggleFlag(lineIndex)"></i>
        </span>

        <div v-else>
            <span>
                <span v-html="subtitle.text"></span>
                <i class="fas fa-edit" @click="lineVisibleFlagsStore.toggleFlag(lineIndex)"></i>
            </span>
        </div>
        
    </div>
</template>

<script>

import { useLineVisibleFlagsStore } from '@/stores/subtitleLineVisibleFlagsStore'

export default {
    name: "SubtitleLine",
    props: {
        subtitle: Object,
        lineIndex: Number
    },
    data: function () {
        return {
            lineVisibleFlagsStore: useLineVisibleFlagsStore()
        }
    },
    methods: {
       
    }


}
</script>