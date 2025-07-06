<template>

  <div class="box content white-li-marker" style="background-color: #004266; margin-bottom: 24px;"
    v-if="Object.keys(fileProcessingLogs).length > 0">

    <div class="label has-text-white has-text-centered">
      Changes applied:
    </div>

    <ul>
      <div v-for="(value, key) in fileProcessingLogs" :key="key">

        <li class="has-text-white" v-if="value == 1 && key !== 'ai-processing-started'">
          {{ key }}
        </li>

        <li class="has-text-white" v-if="value != 1 && key !== 'ai-processing-started'">
          {{ key }} &nbsp; x{{ value }}
        </li>

      </div>
    </ul>

    <div class="mb-5" v-if="fileProcessingLogs['ai-processing-started'] == 1">
      <div class="has-text-centered mt-5">
        <span class="icon is-large has-text-info">
          <i class="fas fa-sync fa-spin fa-2x"></i>
        </span>
        <p class="mt-2 has-text-weight-semibold" style="color:white;">AI processing in progress...</p>
      </div>
    </div>

    <progress class="progress is-success" :value="processedPercentage" max="100">{{ processedPercentage }}</progress>

  </div>
</template>

<script>

export default {

  name: "AppliedChanges",
  props: {
    fileProcessingLogs: Object,
    processedPercentage: Number
  },
  data() {
    return {

    }
  }
}

</script>

<style>
.white-li-marker li::marker {
  color: white;
  /* Bullet color */
}
</style>