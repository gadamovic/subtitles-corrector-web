<template>

  <div class="modal" :class="modalActive ? 'is-active' : ''">
    <div class="modal-background" @click="closeModal"></div>
    <div class="modal-content">

      <div class="box" style="max-width: 600px; margin: auto;">

        <!-- Download Section -->
        <div class="notification is-link is-light has-text-centered mb-5">
          <p class="mb-3"><strong>Your subtitles are ready!</strong></p>
          <a class="button is-link is-medium is-flex is-align-items-center is-justify-content-center"
            @click="downloadFile" style="max-width: 100%; overflow: hidden;" :title="subtitleFilename">
            📥 Download&nbsp;
            <span style="
        display: inline-block;
        max-width: 300px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        vertical-align: bottom;
      ">
              {{ subtitleFilename }}
            </span>
          </a>
        </div>

        <!-- Feedback Form -->
        <form ref="contact-form" class="box" style="background-color: #004266; color: white;"
          @submit.prevent="submitContactForm">
          <div class="field has-text-centered mb-4">
            <label class="label has-text-white">
              Subtitle not fixed? This is your chance to roast us and explain why.
            </label>
          </div>

          <div class="field">
            <div class="control">
              <textarea class="textarea" placeholder="What were you trying to do?" id="description" name="description"
                v-model="this.description" required>
            </textarea>
            </div>
          </div>

          <div class="field is-grouped is-justify-content-center mt-4">
            <div class="control">
              <GenericButton @click="submitContactForm" button_text="Submit Feedback" :loading="this.loading" />
            </div>
          </div>
        </form>

        <div class="notification mt-4 has-text-centered" :class="this.error ? 'is-danger' : 'is-success'"
          v-if="contactFormConfirmation != ''">
          {{ this.contactFormConfirmation }}
        </div>

      </div>
    </div>

    <button class="modal-close is-large" aria-label="close" @click="closeModal"></button>
  </div>
</template>

<script>

import GenericButton from './GenericButton.vue';

export default {
  name: "DownloadFileModalComponent",
  components: {
    GenericButton
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
      description: "",
      contactFormConfirmation: '',
      error: false
    };
  },
  methods: {
    closeModal() {
      this.$emit("closeDownloadModal");
      this.contactFormConfirmation = '';
      this.description = '';
    },
    downloadFile() {
      this.$emit("downloadFileClicked");
    },
    async submitContactForm() {

      if (!this.$refs['contact-form'].reportValidity()) {
        return
      }

      try {

        this.loading = true;

        const formData = new FormData();
        formData.append("description", this.description);


        const response = await fetch("api/rest/1.0/submitFeedback", {
          method: "POST",
          body: formData,
        });

        //const result = await response.text();
        const result = await response.json();

        if (response.ok) {
          this.contactFormConfirmation = "Thank you for your valuable feedback!"
          this.error = false;
        } else {

          if (result) {
            this.error = true;
            if (result == 'FAILURE_EMAIL_SEND_RATE_LIMIT') {
              this.contactFormConfirmation = 'Temporary quotas exceeded, form not submitted. Try again later.';
            } else if (result == 'DEVELOPMENT_NOT_SENT') {
              this.contactFormConfirmation = 'Email not sent because we are in development.';
            } else {
              this.contactFormConfirmation = 'Internal server error!';
            }
          } else {
            this.contactFormConfirmation = 'Internal server error!';
            this.error = true;
          }
        }
      } catch (err) {
        console.error("Error:", err);
        this.contactFormConfirmation = err;
        this.error = true;
      } finally {
        this.loading = false;
      }

    }
  }
}

</script>