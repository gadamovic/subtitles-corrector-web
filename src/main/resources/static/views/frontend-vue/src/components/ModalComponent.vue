<template>

  <div class="modal" :class="modalActive ? 'is-active' : ''">
    <div class="modal-background" @click="closeModal"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title">Subtitle editor</p>
        <button class="delete" aria-label="close" @click="closeModal"></button>
      </header>
      <section class="modal-card-body">

        <AppliedChanges :fileProcessingLogs="fileProcessingLogs" :processedPercentage="processedPercentage"></AppliedChanges>
        <SubtitleContentComponent v-if="lastFileProcessingLogReceived"></SubtitleContentComponent>

      </section>
      <footer class="modal-card-foot">
        <div class="buttons">
          <button class="button is-success" :class="this.loading ? 'is-loading' : ''" @click="save">Save changes</button>
          <button class="button" @click="closeModal">Cancel</button>
        </div>
      </footer>
    </div>
  </div>
</template>

<script>
import AppliedChanges from './AppliedChanges.vue';
import SubtitleContentComponent from './SubtitleContentComponent.vue';
import { useSubtitleDataStore } from '@/stores/subtitleDataStore';

export default {
  name: "ModalComponent",
  components: {
    AppliedChanges, SubtitleContentComponent
  },
  props: {
    modalActive: { //toggleing of the modal is controlled from the parent
      type: Boolean,
      default: false
    },
    processedPercentage: Number,
    fileProcessingLogs: Object,
    lastFileProcessingLogReceived: Boolean,
    userId: String
  },
  data() {
    return {
      loading: false,
      subtitleDataStore: useSubtitleDataStore(),
    };
  },
  methods: {
    closeModal() {
      this.$emit("closeModal");
      this.subtitleDataStore.setSubtitleDataTmp(JSON.parse(JSON.stringify(this.subtitleDataStore.subtitleData)))
    },
    async save(){

      this.loading = true;

      this.subtitleDataStore.setSubtitleData(JSON.parse(JSON.stringify(this.subtitleDataStore.subtitleDataTmp))); //deep copy subtitleDataTmp
      
      let response = await fetch(("api/rest/1.0/save?userId=" + this.userId), {
          method: "POST",
          body: JSON.stringify(this.subtitleDataStore.subtitleData),
          headers: new Headers({'Content-Type': 'application/json'})
        });

        this.loading = false;

        if(response.ok){
          this.$emit("closeModal");
        }
        
        this.$emit("saveClicked");
        
    },

  },
}

</script>