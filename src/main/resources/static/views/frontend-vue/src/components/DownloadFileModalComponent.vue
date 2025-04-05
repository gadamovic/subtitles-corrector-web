<template>

  <div class="modal" :class="modalActive ? 'is-active' : ''">

    <div class="modal-background" @click="closeModal"></div>
    <div class="modal-content">
      <div class="box">
        <div class="notification is-link has-text-centered">
          <a @click="downloadFile" style="overflow-wrap: break-word;">
            Download {{ this.subtitleFilename }}
          </a>
        </div>
      </div>
    </div>
    <button class="modal-close is-large" aria-label="close" @click="closeModal"></button>

  </div>
</template>

<script>

export default {
  name: "DownloadFileModalComponent",
  components: {

  },
  props: {
    modalActive: { //toggleing of the modal is controlled from the parent
      type: Boolean,
      default: false
    },
    subtitleFilename: String
  },
  data() {
    return {
      loading: false,
    };
  },
  methods: {
    closeModal() {
      this.$emit("closeDownloadModal");
    },
    downloadFile(){
      this.$emit("downloadFileClicked");
    }
  }
}

</script>