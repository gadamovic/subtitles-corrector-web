<template>
  <div id="app">
    <div  class="section">
      <HeadingComponent first_line="Subtitles corrector" second_line="Make your subtitle come to life!"/>
      <FileUploader/>
      <ContactForm/>
    </div>
    <FooterComponent/>
  </div>
</template>


<script>

import ContactForm from './components/ContactForm.vue';
import FileUploader from './components/FileUploader.vue';
import FooterComponent from'./components/FooterComponent.vue';
import HeadingComponent from './components/HeadingComponent.vue';
export default {
  name: "App",
  components: {
    FileUploader,
    FooterComponent,
    HeadingComponent,
    ContactForm,
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