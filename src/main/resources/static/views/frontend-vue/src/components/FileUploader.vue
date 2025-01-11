<template>

  <ModalComponent :content="lines" :modalActive="showModal" @closeModal="onCloseModal" @showModal="onShowModal"
    :fileProcessingLogs="fileProcessingLogs" :processedPercentage="processedPercentage" :lines="lines"
    :lastFileProcessingLogReceived="lastFileProcessingLogReceived">

  </ModalComponent>

  <form @submit.prevent="handleSubmit" enctype="multipart/form-data" class="box" style="background-color: #004266;">

    <label class="label has-text-white">Upload a subtitle file (srt, sub or txt):</label> <br />
    <div class="file has-name is-fullwidth field">
      <label class="file-label">
        <input class="file-input" type="file" name="file" accept=".srt, .sub, .txt" @change="handleFileChange" />
        <span class="file-cta">
          <span class="file-icon">
            <i class="fas fa-upload"></i>
          </span>
          <span class="file-label"> Choose a file… </span>
        </span>
        <span class="file-name" style="background-color: white;">
          <div v-if="this.file">{{ this.file.name }}</div>
        </span>
      </label>
    </div>

    <GenericButton :loading="loading" button_text="Upload" :enabled="this.upload_button_enabled"></GenericButton>
  </form>

  <!-- Download Link (CURRENTLY HIDDEN) -->
  <div v-if="false">
    <div class="has-text-centered" style="margin-bottom: 24px;" v-if="downloadLink">
      <a :href="downloadLink" class="button is-link is-light">
        Download Corrected File
      </a>
    </div>
  </div>

  <!-- Error Message -->
  <div class="notification is-danger" style="margin-bottom: 24px;" v-if="error">
    {{ error }}
  </div>

</template>

<script>

import GenericButton from './GenericButton.vue';
import ModalComponent from './ModalComponent.vue';

export default {
  name: "FileUploader",
  components: {
    GenericButton, ModalComponent
  },
  data() {
    return {
      file: null, // Selected file
      loading: false, // Loading state
      downloadLink: "", // Link for download
      error: null, // Error message
      lines: Object,
      showModal: false,
      fileProcessingLogs: {},
      processedPercentage: 0,
      webSocketUserId: crypto.randomUUID(),
      upload_button_enabled: true,
      lastFileProcessingLogReceived: false,
      user_acked: false
    };
  },
  methods: {
    handleFileChange(event) {
      // Capture the selected file
      this.file = event.target.files[0];

      const allowedExtensions = ['.srt', '.sub', '.txt'];
      const fileName = this.file.name.toLowerCase();
      const isValid = allowedExtensions.some(ext => fileName.endsWith(ext));

      if (!isValid) {
        this.error = 'Invalid file type. Please upload a .srt, .sub, or .txt file.';
        this.upload_button_enabled = false;
        event.target.value = ''; // Clear the input
        this.file = null;
      } else {
        this.error = '';
        this.upload_button_enabled = true;
      }
    },
    async handleSubmit() {

      //wait for the server to ack the client
      let i = 0;
      while (!this.user_acked && i < 100) {
        i++;
        setTimeout(() => { }, 10);
      }

      this.fileProcessingLogs = {};
      this.processingProgress = 0;
      this.lastFileProcessingLogReceived = false;

      if (!this.file) {
        this.error = "Please select a file.";
        return;
      }

      this.error = null;
      this.loading = true;
      this.downloadLink = "";

      const formData = new FormData();
      formData.append("file", this.file);
      formData.append("webSocketUserId", this.webSocketUserId);

      this.fileProcessingLogs = {};
      this.processedPercentage = 0;

      try {
        const response = await fetch("api/rest/1.0/upload", {
          method: "POST",
          body: formData,
        });

        if (response.ok) {
          const result = await response.json();
          this.downloadLink = result;
          this.lines = result;
          this.loading = false;

        } else {
          const result = await response.text();
          if (result) {
            this.error = result;
          } else {
            this.error = "Submission failed!";
          }
          this.loading = false;
        }
      } catch (err) {
        console.error("Error:", err);
        this.error = "An error occurred!";
        this.loading = false;
      }
    },


    establishWSConnection() {


      let contextRoot = "/subtitles";
      let hostAddress = "ws://localhost:8080";
      if (process.env.VUE_APP_ENVIRONMENT === 'prod') {
        contextRoot = "";
        hostAddress = "ws://subtitles-corrector.com";
      }
      const socket = new WebSocket(hostAddress + contextRoot + "/sc-ws-connection-entrypoint");

      // Handle connection open
      socket.onopen = () => {
        console.log("WebSocket connected");
        socket.send("USER_ID<" + this.webSocketUserId + ">");
      };

      // Handle messages
      socket.onmessage = (event) => {

        if (this.isJson(event.data)) {
          this.handleMessage(JSON.parse(event.data))
        } else if (event.data === 'USER_ACK') {
          this.user_acked = true;
        } else {
          console.log("Message: " + event.data);
        }
      };

      // Handle connection close
      socket.onclose = () => {
        console.log("WebSocket disconnected");
      };
    },

    handleMessage(message) {
      
      //when first log message arrives, show the modal
      if (message.correctionDescription || message.processedPercentage){
        this.showModal = true;
      }
      
      if (message.correctionDescription) {


        let correction = this.fileProcessingLogs[message.correctionDescription];
        if (correction) {
          correction++;
          this.fileProcessingLogs[message.correctionDescription] = correction;
        } else {
          this.fileProcessingLogs[message.correctionDescription] = 1;
        }
      }

      if (message.processedPercentage) {
        this.processedPercentage = message.processedPercentage;
      }

      if (message.processedPercentage == '100') {
        this.lastFileProcessingLogReceived = true;
      }
    },
    onCloseModal() {
      this.showModal = false;
    },
    onShowModal() {
      this.showModal = true;
    },
    isJson(str) {
      try {
        JSON.parse(str);
        return true;
      } catch (err) {
        return false;
      }
    }
  },
  mounted: function () {
    //this.establishWSConnection(this.webSocketUserId);
    this.establishWSConnection();
  }
}

</script>