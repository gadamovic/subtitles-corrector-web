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
      <div class="has-text-centered" v-if="downloadLink">
        <a :href="downloadLink" class="button is-link is-light">
          Download Corrected File
        </a>
      </div>

      <!-- Error Message -->
      <div class="notification is-danger" v-if="error">
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
    }
}
</script>