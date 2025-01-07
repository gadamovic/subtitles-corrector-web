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
        {{ content }}
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
export default {
  name: "ModalComponent",
  props: {
    content: String,
    modalActive: { //toggleing of the modal is controlled from the parent
      type: Boolean,
      default: false
    },
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