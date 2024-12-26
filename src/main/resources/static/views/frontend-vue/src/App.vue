<template>
  <div id="app">
    <h1>Subtitle File Upload</h1>
    <form @submit.prevent="handleSubmit" enctype="multipart/form-data">
      <input
        type="file"
        id="file_upload"
        name="file"
        accept=".srt, .sub, .txt"
        @change="handleFileChange"
      />
      <input type="submit" value="Upload" />
    </form>
    <br /><br />
    <img
      src="img/icons/loading_icon.gif"
      id="loading_icon"
      width="40px"
      v-if="loading"
    />
    <div id="download_link" v-if="downloadLink">
      <a :href="downloadLink">Download</a>
    </div>
    <div v-if="error" style="color: red;">{{ error }}</div>
  </div>
</template>

<script>
export default {
  name: "App",
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
        const response = await fetch("/subtitles/api/rest/1.0/upload", {
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