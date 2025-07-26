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
            <input class="input is-small" v-model="this.shiftValue" style="width:6rem" placeholder="Shift subtitle" />
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
            shiftValue: "",
            subtitleDataStore: useSubtitleDataStore(),
            isApplySyncLoading: false,
            showInvalidSyncInput: false,
            lastAppliedSyncChange: null
        }
    },
    methods: {
        incrementValue() {
            if (this.shiftValue == "") {
                this.shiftValue = "0"
            }

            let floatValue = parseFloat(this.shiftValue);
            floatValue += 0.1;
            floatValue = Math.round(floatValue * 10) / 10
            if (floatValue == 0) {
                this.shiftValue = ""
            } else if (floatValue > 0) {
                this.shiftValue = "+" + floatValue.toString();
            } else {
                this.shiftValue = floatValue.toString();
            }
        },

        decrementValue() {
            if (this.shiftValue == "") {
                this.shiftValue = "0"
            }

            let floatValue = parseFloat(this.shiftValue);
            floatValue -= 0.1;
            floatValue = Math.round(floatValue * 10) / 10
            if (floatValue == 0) {
                this.shiftValue = ""
            } else if (floatValue > 0) {
                this.shiftValue = "+" + floatValue.toString();
            } else {
                this.shiftValue = floatValue.toString();
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

                from.formattedTimestamp = this.formatSubtitleTimestamp(from);
                to.formattedTimestamp = this.formatSubtitleTimestamp(to);

                stateCopy.lines[i].timestampFromShifted = from;
                stateCopy.lines[i].timestampToShifted = to

            }

            this.lastAppliedSyncChange = this.shiftValue;
            this.subtitleDataStore.setSubtitleDataTmp(stateCopy);

            this.isApplySyncLoading = false;

        },
        updateTimestamp(baseTimestamp) {

            if (this.shiftValue == null || this.length == 0) {
                this.shiftValue = 0;
            }

            let timestampFrom = new Date();
            timestampFrom.setHours(baseTimestamp.hour);
            timestampFrom.setMinutes(baseTimestamp.minute);
            timestampFrom.setSeconds(baseTimestamp.second)
            timestampFrom.setMilliseconds(baseTimestamp.millisecond + (parseFloat(this.shiftValue) * 1000))


            let newFromHour = timestampFrom.getHours().toString();
            let newFromMinute = timestampFrom.getMinutes().toString();
            let newFromSecond = timestampFrom.getSeconds().toString();
            let newFromMillisecond = timestampFrom.getMilliseconds().toString();

            let timestampShifted = {};
            timestampShifted.hour = newFromHour;
            timestampShifted.minute = newFromMinute;
            timestampShifted.second = newFromSecond;
            timestampShifted.millisecond = newFromMillisecond;

            return timestampShifted;
        },
        formatSubtitleTimestamp(subtitleTimestamp){

            let hour = subtitleTimestamp.hour;
            let minute = subtitleTimestamp.minute;
            let second = subtitleTimestamp.second;
            let millisecond = subtitleTimestamp.millisecond;

           let finalTimestamp = "";
            finalTimestamp += hour.length == 1 ? ("0" + hour) : hour;
            finalTimestamp += ":";

            finalTimestamp += minute.length == 1 ? ("0" + minute) : minute;
            finalTimestamp += ":";

            finalTimestamp += second.length == 1 ? ("0" + second) : second;
            finalTimestamp += ",";

            
            if(millisecond.length == 1){
                finalTimestamp += ("00" + millisecond);
            }else if (millisecond.length == 2){
                finalTimestamp += ("0" + millisecond);
            }else{
                finalTimestamp += millisecond;
            }

            return finalTimestamp;

        },
        reset (){
            this.shiftValue = "0";
            this.applySync();
            this.shiftValue = "";
            this.lastAppliedSyncChange = null;
        },
        validateSyncInput(){

            if(this.shiftValue == null || this.shiftValue == ""){
                return false;
            }
            let valueTmp;

            if(this.shiftValue.startsWith("+") || this.shiftValue.startsWith("-")){
                valueTmp = this.shiftValue.substring(1);    
            }else{
                valueTmp = this.shiftValue;
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