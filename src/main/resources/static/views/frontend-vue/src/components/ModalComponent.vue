<template>

  <button @click="openModal">
    Open JS example modal
  </button>

  <div class="modal" :class="modalActive ? 'is-active' : ''">
    <div class="modal-background" @click="closeModal"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title">Modal title</p>
        <button class="delete" aria-label="close" @click="closeModal"></button>
      </header>
      <section class="modal-card-body">

        <AppliedChanges :fileProcessingLogs="fileProcessingLogs" :processedPercentage="processedPercentage"></AppliedChanges>
        <SubtitleContentComponent :content="lines" v-if="lastFileProcessingLogReceived"></SubtitleContentComponent>

      </section>
      <footer class="modal-card-foot">
        <div class="buttons">
          <button class="button is-success">Save changes</button>
          <button class="button">Cancel</button>
        </div>
      </footer>
    </div>
  </div>
</template>

<script>
import AppliedChanges from './AppliedChanges.vue';
import SubtitleContentComponent from './SubtitleContentComponent.vue';

export default {
  name: "ModalComponent",
  components: {
    AppliedChanges, SubtitleContentComponent
  },
  props: {
    content: String,
    modalActive: { //toggleing of the modal is controlled from the parent
      type: Boolean,
      default: false
    },
    processedPercentage: Number,
    fileProcessingLogs: Object,
    lines: String,
    lastFileProcessingLogReceived: Boolean
  },
  data() {
    return {

    };
  },
  methods: {
    openModal() {
      this.$emit("showModal");
    },
    closeModal() {
      this.$emit("closeModal");
    }
  }
}

</script>