<template>
  <div id="app" class="section">
    <div class="container">
      <!-- Title -->
      <h1 class="title has-text-primary has-text-centered">Subtitle File Upload</h1>
      <p class="subtitle has-text-centered">
        Upload subtitle files for correction.
      </p>

      <!-- Upload Form -->
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
      <div class="has-text-centered" v-if="downloadLink">
        <a :href="downloadLink" class="button is-link is-light">
          Download Corrected File
        </a>
      </div>

      <!-- Error Message -->
      <div class="notification is-danger" v-if="error">
        {{ error }}
      </div>

      <FooterComponent/>
    </div>
  </div>
</template>


<script>
import GenericButton from './components/GenericButton.vue';
import FooterComponent from'./components/FooterComponent.vue';
export default {
  name: "App",
  components: {
    GenericButton,
    FooterComponent,
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
          this.error = "Submission failed!";
        }
      } catch (err) {
        console.error("Error:", err);
        this.error = "An error occurred!";
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style>
/* Add some basic styling */
#app {
  max-width: 600px;
  margin: auto;
  text-align: center;
}

img {
  margin: 10px auto;
}

form {
  margin-bottom: 20px;
}
</style>