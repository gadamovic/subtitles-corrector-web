<template>

  <div class="container">

    <form @submit.prevent="handleSubmit" enctype="multipart/form-data" class="box" style="background-color: #004266;">
      <div class="field">
        <label class="label has-text-white">Choose a subtitle file:</label>
        <div class="control">
          <input class="input" type="file" id="file_upload" name="file" accept=".srt, .sub, .txt"
            @change="handleFileChange" />
        </div>
      </div>

      <GenericButton :loading="loading" button_text="Upload"></GenericButton>
    </form>

    <!-- Download Link -->
    <div class="has-text-centered" style="margin-bottom: 24px;" v-if="downloadLink">
      <a :href="downloadLink" class="button is-link is-light">
        Download Corrected File
      </a>
    </div>

    <div class="box" style="background-color: #004266; margin-bottom: 24px;" v-if="Object.keys(fileProcessingLogs).length > 0">

      <div class="label has-text-white">
          Changes applied:
      </div>

      <div class="label" v-for="(value, key) in fileProcessingLogs" :key="key">

        <div class="label has-text-white" v-if="value == 1">
          {{ key }}
        </div>

        <div class="label has-text-white" v-if="value != 1">
          {{ key }} &nbsp; x{{ value }}
        </div>

      </div>
      <progress class="progress is-success" :value="this.processedPercentage" max="100">{{ processedPercentage }}</progress>
    </div>

    <!-- Error Message -->
    <div class="notification is-danger" style="margin-bottom: 24px;" v-if="error">
      {{ error }}
    </div>

  </div>

</template>

<script>

import GenericButton from './GenericButton.vue';
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export default {
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
      fileProcessingLogs: {},
      processedPercentage: 0,
      webSocketUserId: crypto.randomUUID(),
    };
  },
  methods: {
    handleFileChange(event) {
      // Capture the selected file
      this.file = event.target.files[0];
    },
    async handleSubmit() {

      this.fileProcessingLogs = {};
      this.processingProgress = 0;

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
          const result = await response.text();
          this.downloadLink = result;
        } else {
          const result = await response.text();
          if (result) {
            this.error = result;
          } else {
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
    
    establishWSConnection(webSocketUserId) {

      let contextRoot = "/subtitles";
      let hostAddress = "http://localhost:8080";
      if (process.env.VUE_APP_ENVIRONMENT === 'prod') {
        contextRoot = "";
        hostAddress = "https://subtitles-corrector.com";
      }

      

      const socket = new SockJS(hostAddress + contextRoot + "/sc-ws-connection-entrypoint"); // WebSocket endpoint
      
      this.stompClient = new Client({
        webSocketFactory: () => socket,
        reconnectDelay: 5000,
        onConnect: this.onConnected,
        onStompError: this.onError,
        connectHeaders: {'webSocketUserId': webSocketUserId}
      });

      this.stompClient.activate()
    },
    handleMessage(message){
      let correction = this.fileProcessingLogs[message.correctionDescription];
        if(correction){
          correction ++;
          this.fileProcessingLogs[message.correctionDescription] = correction;
        }else{
          this.fileProcessingLogs[message.correctionDescription] = 1;
        }

        this.processedPercentage = message.processedPercentage;
    },
    onConnected() {
      console.log("Connected to WebSocket");

      const regex = /.*\/(.*)\/websocket/
      let webSocketSessionId = regex.exec(this.stompClient.webSocket._transport.url)[1];

      this.stompClient.subscribe("/user/" + webSocketSessionId + "/subtitles-processing-log", (message) => {
        
        this.handleMessage(JSON.parse(message.body));
      });
    },
    onError(error) {
      console.error("WebSocket error:", error);
    },
    sendMessage(message) {
      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.publish({
          destination: "/app/ws/1.0/upload",
          body: JSON.stringify({ message }),
        });
      }
    },
  },
  mounted: function(){
      //TODO: check if maybe there's no need to create a new socket connection every time user hits upload (but once in a http session)
      //TODO: also maybe we need await here
      this.establishWSConnection(this.webSocketUserId);
  }
}

</script>