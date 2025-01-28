<template>

    <div class="box content white-li-marker" style="background-color: #004266; margin-bottom: 24px;"
      v-if="Object.keys(fileProcessingLogs).length > 0">

      <div class="label has-text-white has-text-centered">
        Changes applied:
      </div>

      <ul>
      <li v-for="(value, key) in fileProcessingLogs" :key="key">

        <div class="has-text-white" v-if="value == 1">
          {{ key }}
        </div>

        <div class="has-text-white" v-if="value != 1">
          {{ key }} &nbsp; x{{ value }}
        </div>

      </li>
      </ul>
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

<style>

  .white-li-marker li::marker{
    color: white; /* Bullet color */
  }

</style>