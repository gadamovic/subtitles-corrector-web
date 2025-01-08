<template>

  <ModalComponent 
    :content="lines"
    :modalActive="showModal"
    @closeModal="onCloseModal"
    @showModal="onShowModal"
    :fileProcessingLogs="fileProcessingLogs"
    :processedPercentage="processedPercentage"
    :lines="lines"
    :lastFileProcessingLogReceived="lastFileProcessingLogReceived">
  
  </ModalComponent>

    <form @submit.prevent="handleSubmit" enctype="multipart/form-data" class="box" style="background-color: #004266;">

      <label class="label has-text-white">Upload a subtitle file (srt, sub or txt):</label> <br/>
      <div class="file has-name is-fullwidth field">
        <label class="file-label">
          <input class="file-input" type="file" name="file" accept=".srt, .sub, .txt" @change="handleFileChange" />
          <span class="file-cta">
            <span class="file-icon">
              <i class="fas fa-upload"></i>
            </span>
            <span class="file-label"> Choose a file… </span>
          </span>
          <span class="file-name" style="background-color: white;"><div v-if="this.file">{{ this.file.name }}</div></span>
        </label>
      </div>

      <GenericButton :loading="loading" button_text="Upload" :enabled="this.upload_button_enabled"></GenericButton>
    </form>

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

</template>

<script>

import GenericButton from './GenericButton.vue';
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
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
      lines: String,
      showModal: false,
      fileProcessingLogs: {},
      processedPercentage: 0,
      webSocketUserId: crypto.randomUUID(),
      upload_button_enabled: true,
      lastFileProcessingLogReceived: false
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
      }else{
        this.error = '';
        this.upload_button_enabled = true;
      }
    },
    async handleSubmit() {

      this.fileProcessingLogs = {};
      this.processingProgress = 0;
      this.lastFileProcessingLogReceived = false;

      if (!this.file) {
        this.error = "Please select a file.";
        return;
      }

      this.showModal = true;
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

      if(message.correctionDescription){
        let correction = this.fileProcessingLogs[message.correctionDescription];
          if(correction){
            correction ++;
            this.fileProcessingLogs[message.correctionDescription] = correction;
          }else{
            this.fileProcessingLogs[message.correctionDescription] = 1;
          }
      }

      if(message.processedPercentage){
        this.processedPercentage = message.processedPercentage;
      }

      if(message.processedPercentage == '100'){
        this.lastFileProcessingLogReceived = true;
      }
    },
    onConnected() {
      console.log("Connected to WebSocket");

      
      const regex = /.*\/(.*)\/websocket/
      let webSocketSessionId = "";
      try{
        webSocketSessionId = regex.exec(this.stompClient.webSocket._transport.url)[1];
      }catch(errormsg){
        console.error(errormsg);
        console.log(this.stompClient.webSocket._transport.url);
      }
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
    onCloseModal(){
      this.showModal = false;
    },
    onShowModal(){
      this.showModal = true;
    }
  },
  mounted: function(){
      this.establishWSConnection(this.webSocketUserId);
  }
}

</script>