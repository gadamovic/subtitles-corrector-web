<template>

<div class="container">

      <form
        @submit.prevent="handleSubmit"
        enctype="multipart/form-data"
        class="box"
        style="background-color: #004266;"
      >
        <div class="field">
          <label class="label has-text-white">Choose a subtitle file:</label>
          <div class="control">
            <input
              class="input"
              type="file"
              id="file_upload"
              name="file"
              accept=".srt, .sub, .txt"
              @change="handleFileChange"
            />
          </div>
        </div>

        <GenericButton :loading="loading" button_text="Upload"></GenericButton>
      </form>

      <!-- Loading Indicator -->
      <div class="has-text-centered" v-if="loading">
        <img
          src="img/icons/loading_icon.gif"
          id="loading_icon"
          width="40px"
          alt="Loading..."
        />
        <p class="has-text-white">Processing your file...</p>
      </div>

      <!-- Download Link -->
      <div class="has-text-centered" style="margin-bottom: 24px;" v-if="downloadLink">
        <a :href="downloadLink" class="button is-link is-light">
          Download Corrected File
        </a>
      </div>

      <!-- Error Message -->
      <div class="notification is-danger" style="margin-bottom: 24px;" v-if="error">
        {{ error }}
      </div>

    </div>

</template>

<script>

import GenericButton from './GenericButton.vue';

export default{
    name: "FileUploader",
    components: {
        GenericButton,
    },
    data() {
    return {
      file: null, // Selected file
      loading: false, // Loading state
      downloadLink: "", // Link for download
      error: null, // Error message
    };
  },
  methods: {
    handleFileChange(event) {
      // Capture the selected file
      this.file = event.target.files[0];
    },
    async handleSubmit() {
      if (!this.file) {
        this.error = "Please select a file.";
        return;
      }

      this.error = null;
      this.loading = true;
      this.downloadLink = "";

      const formData = new FormData();
      formData.append("file", this.file);

      try {
        const response = await fetch("api/rest/1.0/upload", {
          method: "POST",
          body: formData,
        });

        if (response.ok) {
          const result = await response.text();
          this.downloadLink = result;
        } else {
          const result = await response.text();
          if(result){
            this.error = result;
          }else{
            this.error = "Submission failed!";
          }
        }
      } catch (err) {
        console.error("Error:", err);
        this.error = "An error occurred!";
      } finally {
        this.loading = false;
      }
    },
  },
}
</script>