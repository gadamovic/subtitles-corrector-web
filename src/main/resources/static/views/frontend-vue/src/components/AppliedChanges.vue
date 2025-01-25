<template>

    <div class="box has-text-centered" style="background-color: #004266; margin-bottom: 24px;"
      v-if="Object.keys(fileProcessingLogs).length > 0">

      <div class="label has-text-white">
        Changes applied:
      </div>

      <div class="label" v-for="(value, key) in fileProcessingLogs" :key="key">

        <div class="label has-text-white" v-if="value == 1">
          {{ key }}
        </div>

        <div class="label has-text-white" v-if="value != 1">
          {{ key }} &nbsp; x{{ value }}
        </div>

      </div>
      <progress class="progress is-success" :value="processedPercentage" max="100">{{ processedPercentage }}</progress>
    </div>
</template>

<script>

export default{

    name: "AppliedChanges",
    props: {
        fileProcessingLogs: Object,
        processedPercentage: Number
    },
    data() {
        return{

        }
    }
}

</script>
