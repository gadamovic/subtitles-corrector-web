<template>

    <div class="field has-addons">
        <div class="control">
            <button class="button is-primary is-light is-small mr-1" @click="decrementValue">
                <span class="icon is-small">
                    <i class="fas fa-minus"></i>
                </span>
            </button>
        </div>
        <div class="control">
            <input class="input is-small" v-model="this.value" style="width:6rem" placeholder="Shift subtitle" />
            <p class="help is-danger" v-if="showInvalidSyncInput">Invalid input</p>
        </div>
        <div class="control">
            <button class="button is-primary is-light is-small ml-1" @click="incrementValue">
                <span class="icon is-small">
                    <i class="fas fa-plus"></i>
                </span>
            </button>
        </div>

        <div class="control">
            <button class="button is-primary is-light is-small ml-1" :class="isApplySyncLoading ? 'is-loading' : ''"
                @click="applySync">
                Apply
            </button>
        </div>

        <div class="control" v-if="this.showResetButton">
            <button class="button is-primary is-light is-small ml-1" @click="reset">
                Reset
            </button>
        </div>
    </div>

</template>

<script>

import { useSubtitleDataStore } from '@/stores/subtitleDataStore';

export default {
    name: "NumericalStepInputComponent",
    data: function () {
        return {
            value: "",
            subtitleDataStore: useSubtitleDataStore(),
            isApplySyncLoading: false,
            showInvalidSyncInput: false,
            lastAppliedSyncChange: null
        }
    },
    methods: {
        incrementValue() {
            if (this.value == "") {
                this.value = "0"
            }

            let floatValue = parseFloat(this.value);
            floatValue += 0.1;
            floatValue = Math.round(floatValue * 10) / 10
            if (floatValue == 0) {
                this.value = ""
            } else if (floatValue > 0) {
                this.value = "+" + floatValue.toString();
            } else {
                this.value = floatValue.toString();
            }
        },

        decrementValue() {
            if (this.value == "") {
                this.value = "0"
            }

            let floatValue = parseFloat(this.value);
            floatValue -= 0.1;
            floatValue = Math.round(floatValue * 10) / 10
            if (floatValue == 0) {
                this.value = ""
            } else if (floatValue > 0) {
                this.value = "+" + floatValue.toString();
            } else {
                this.value = floatValue.toString();
            }
        },
        applySync() {
            if(!this.validateSyncInput()){
                this.showInvalidSyncInput = true;
                return;
            }

            this.showInvalidSyncInput = false;

            this.isApplySyncLoading = true;
            let stateCopy = JSON.parse(JSON.stringify(this.subtitleDataStore.subtitleDataTmp));

            for (let i = 0; i < stateCopy.lines.length; i++) {
                let from = this.updateTimestamp(stateCopy.lines[i].timestampFrom)
                let to = this.updateTimestamp(stateCopy.lines[i].timestampTo);

                stateCopy.lines[i].timestampFromShifted = from;
                stateCopy.lines[i].timestampToShifted = to

            }

            this.lastAppliedSyncChange = this.value;
            this.subtitleDataStore.setSubtitleDataTmp(stateCopy);

            this.isApplySyncLoading = false;

        },
        updateTimestamp(base) {

            if (this.value == null || this.length == 0) {
                this.value = 0;
            }

            const fromSplit = base.split(":");

            let fromHour = fromSplit[0];
            let fromMinute = fromSplit[1];
            let fromSecondAndMillisecond = fromSplit[2];

            let timestampFrom = new Date();
            timestampFrom.setHours(fromHour);
            timestampFrom.setMinutes(fromMinute);
            timestampFrom.setSeconds(fromSecondAndMillisecond.split(",")[0])
            timestampFrom.setMilliseconds(parseFloat(fromSecondAndMillisecond.split(",")[1]) + (parseFloat(this.value) * 1000))


            let newFromHour = timestampFrom.getHours().toString();
            let newFromMinute = timestampFrom.getMinutes().toString();
            let newFromSecond = timestampFrom.getSeconds().toString();
            let newFromMillisecond = timestampFrom.getMilliseconds().toString();

            let finalTimestamp = "";
            finalTimestamp += newFromHour.length == 1 ? ("0" + newFromHour) : newFromHour;
            finalTimestamp += ":";

            finalTimestamp += newFromMinute.length == 1 ? ("0" + newFromMinute) : newFromMinute;
            finalTimestamp += ":";

            finalTimestamp += newFromSecond.length == 1 ? ("0" + newFromSecond) : newFromSecond;
            finalTimestamp += ",";

            finalTimestamp += newFromMillisecond.length == 1 ? ("0" + newFromMillisecond) : newFromMillisecond;

            return finalTimestamp;

        },
        reset (){
            this.value = "0";
            this.applySync();
            this.value = "";
            this.lastAppliedSyncChange = null;
        },
        validateSyncInput(){

            if(this.value == null || this.value == ""){
                return false;
            }
            let valueTmp;

            if(this.value.startsWith("+") || this.value.startsWith("-")){
                valueTmp = this.value.substring(1);    
            }else{
                valueTmp = this.value;
            }

            if(!isNaN(valueTmp) && valueTmp < (60 * 60 * 2) && valueTmp > (0 - 60 * 60 * 2)){
                return true;
            }else{
                return false;
            }

        },
    },
    computed: {
        showResetButton(){
            return this.lastAppliedSyncChange != null &&
                    this.lastAppliedSyncChange != "" &&
                    this.lastAppliedSyncChange != "0";
        }
    }
}

</script>