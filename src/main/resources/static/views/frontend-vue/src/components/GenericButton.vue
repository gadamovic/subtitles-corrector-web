<template>
<div class="field">
    <div class="control">
      <button
        type="button"
        class="button is-success is-fullwidth"
        :class="loading ? 'is-loading' : ''"
        :disabled="loading"
      >
        {{ button_text }}
      </button>
    </div>
  </div>
</template>

<script>
export default{
    props: {
        loading: Boolean,
        button_text: String,
        enabled: Boolean
    },
    name: "GenericButton",
    loading: false, // Loading state
}


</script>