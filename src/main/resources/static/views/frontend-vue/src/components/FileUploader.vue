<template>

  <ModalComponent :modalActive="showModal" @closeModal="onCloseModal" @saveClicked="saveModalClicked"
    :fileProcessingLogs="fileProcessingLogs" :processedPercentage="processedPercentage"
    :lastFileProcessingLogReceived="lastFileProcessingLogReceived" :userId="userId" ref="modalComponentRef">

  </ModalComponent>

  <div class="notification is-success" v-if="showSaved">
    <button class="delete" @click="() => {this.showSaved = false}"></button>
    Changes saved
  </div>

  <form @submit.prevent enctype="multipart/form-data" class="box" style="background-color: #004266;">

    <label class="label has-text-white has-text-centered">Upload a subtitle file:</label> <br />
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

    <div class="box">
      <p class="title is-5 has-text-centered mb-3">HTML options</p>
      <div class="checkboxes">
        <label class="checkbox">
          <input type="checkbox" @click="toggleStripBTags"/>
          Strip &lt;b&gt; tags
        </label>

        <label class="checkbox">
          <input type="checkbox" @click="toggleStripITags"/>
          Strip &lt;i&gt; tags
        </label>

        <label class="checkbox">
          <input type="checkbox" @click="toggleStripUTags"/>
          Strip &lt;u&gt; tags
        </label>

        <label class="checkbox">
          <input type="checkbox" @click="toggleStripFontTags"/>
          Strip &lt;font&gt; tags
        </label>

      <strong>All other html is not relevant for srt files and is always removed.</strong>
    </div>
  </div>

    <div class="box">
      <p class="title is-5 has-text-centered mb-3">Character / encoding options</p>
      <div class="checkboxes">
        <label class="checkbox">
          <input type="checkbox" @click="toggleKeepBOM"/>
          Keep BOM
        </label>

        <label class="checkbox">
          <input type="checkbox" checked @click="toggleaeToTj"/>
          Convert æ to ć
        </label>

        <label class="checkbox">
          <input type="checkbox" checked @click="toggleAEToTJ"/>
          Convert Æ to Ć
        </label>

        <label class="checkbox">
          <input type="checkbox" checked @click="toggleeToch"/>
          Convert è to č
        </label>

        <label class="checkbox">
          <input type="checkbox" checked @click="toggleEToCH"/>
          Convert È to Č
        </label>
      </div>
    </div>

    <GenericButton :loading="loading" button_text="Upload" :enabled="this.upload_button_enabled" @click="handleSubmit">
    </GenericButton>
    <GenericButton :loading="false" button_text="Continue editing" :enabled="true" v-if="showDownloadLink"
      @click="showModalMethod"></GenericButton>


  </form>

  <div class="notification is-link has-text-centered" v-if="this.subtitleFilename && this.showDownloadLink">
    <a @click="downloadFile" style="overflow-wrap: break-word;">
      Download {{this.subtitleFilename}}
    </a>
  </div>

  <!-- Error Message -->
  <div class="notification is-danger has-text-centered" style="margin-bottom: 24px;" v-if="error">
    {{ error }}
  </div>

</template>

<script>

import { useLoaderStore } from '@/stores/subtitleContentComponentLoaderStore';
import { useLineVisibleFlagsStore } from '@/stores/subtitleLineVisibleFlagsStore'
import GenericButton from './GenericButton.vue';
import ModalComponent from './ModalComponent.vue';
import { useSubtitleDataStore } from '@/stores/subtitleDataStore';

export default {
  name: "FileUploader",
  components: {
    GenericButton, ModalComponent
  },
  data() {
    return {
      file: null, // Selected file
      loading: false, // Loading state
      error: null, // Error message
      subtitleDataStore: useSubtitleDataStore(),
      showModal: false,
      fileProcessingLogs: {},
      processedPercentage: 0,
      userId: crypto.randomUUID(),
      upload_button_enabled: true,
      lastFileProcessingLogReceived: false,
      user_acked: false,
      showSaved: false,
      showDownloadLink: false,
      loaderStore: useLoaderStore(),
      lineVisibleFlagsStore: useLineVisibleFlagsStore(),
      stripBTags: false,
      stripITags: false,
      stripUTags: false,
      stripFontTags: false,
      keepBOM: false,
      aeToTj: true,
      AEToTJ: true,
      eToch: true,
      EToCH: true,
    };
  },
  methods: {
    handleFileChange(event) {
      // Capture the selected file

      if(event.target.files == null || event.target.files.length == 0){
        return;
      }

      this.file = event.target.files[0];

      const allowedExtensions = ['.srt'/*, '.sub', '.txt'*/];
      const fileName = this.file.name.toLowerCase();
      const isValid = allowedExtensions.some(ext => fileName.endsWith(ext));

      if (!isValid) {
        this.error = 'Invalid file type. Please upload a .srt file.';
        //this.error = 'Invalid file type. Please upload a .srt, .sub, or .txt file.';
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

      if (!this.file) {
        this.error = "Please select a file.";
        return;
      }

      this.initBeforeUpload();

      const formData = new FormData();
      formData.append("file", this.file);
      formData.append("webSocketUserId", this.userId);

      formData.append("stripBTags", this.stripBTags)
      formData.append("stripITags", this.stripITags)
      formData.append("stripFontTags", this.stripFontTags)
      formData.append("stripUTags", this.stripUTags)

      formData.append("keepBOM", this.keepBOM)
      formData.append("aeToTj", this.aeToTj)
      formData.append("AEToTJ", this.AEToTJ)
      formData.append("eToch", this.eToch)
      formData.append("EToCH", this.EToCH)

      try {
        const response = await fetch("api/rest/1.0/upload", {
          method: "POST",
          body: formData,
        });

        if (response.ok) {
          const result = await response.json();

          this.subtitleDataStore.setSubtitleData(JSON.parse(JSON.stringify(result)))
          this.subtitleDataStore.setSubtitleDataTmp(JSON.parse(JSON.stringify(result)))
          this.lineVisibleFlagsStore.setValue(Array(result.lines.length - 1).fill(false));

          this.loading = false;

        } else {
          const result = await response.json();
          if (result.httpResponseMessage) {
            this.error = result.httpResponseMessage;
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

      this.$refs['modalComponentRef'].resetSubtitleSync();

      this.loaderStore.setIsLoading(false)
      
    },
    initBeforeUpload(){
      this.processedPercentage = 0;
      this.loaderStore.setIsLoading(true)
      this.subtitleDataStore.setSubtitleData(new Object())
      this.subtitleDataStore.setSubtitleDataTmp(new Object())

      this.error = null;
      this.loading = true;
      this.downloadLink = "";

      this.fileProcessingLogs = {};
      this.processingProgress = 0;
      this.lastFileProcessingLogReceived = false;
      this.showDownloadLink = false;
    },
    establishWSConnection() {


      let contextRoot = "/subtitles";
      let hostAddress = "ws://localhost:8080";
      if (process.env.VUE_APP_ENVIRONMENT === 'prod') {
        contextRoot = "";
        hostAddress = "wss://subtitles-corrector.com";
      }
      const socket = new WebSocket(hostAddress + contextRoot + "/sc-ws-connection-entrypoint");

      // Handle connection open
      socket.onopen = () => {
        console.log("WebSocket connected");
        socket.send("USER_ID<" + this.userId + ">");
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
      if (message.correctionDescription || message.processedPercentage) {
        this.showModalMethod();
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
        this.processedPercentage = Number(message.processedPercentage);
      }

      if (message.processedPercentage == 100) {
        this.lastFileProcessingLogReceived = true;
      }
    },
    onCloseModal() {
      this.showModal = false;
    },
    showModalMethod(){
      this.showModal = true;
    },
    isJson(str) {
      try {
        JSON.parse(str);
        return true;
      } catch (err) {
        return false;
      }
    },
    async downloadFile() {
      const response = await fetch("api/rest/1.0/downloadFile?userId=" + this.userId, {
        method: "GET"
      });

      if (response.ok) {
        const blob = await response.blob();

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");

        a.href = url;
        const subtitleDataObj = this.subtitleDataStore.subtitleData;

        a.download = '[subtitles-corrector.com]-' + subtitleDataObj.filename.substr(subtitleDataObj.filename.indexOf("_") + 1, subtitleDataObj.filename.length);
        document.body.appendChild(a);
        a.click();

        // Clean up
        a.remove();
        window.URL.revokeObjectURL(url);
      }
    },
    saveModalClicked() {

      setTimeout(() => {this.showDownloadLink = true}, 700);
      
    },
    toggleStripBTags() {
      this.stripBTags = !this.stripBTags;
    },
    toggleStripITags() {
      this.stripITags = !this.stripITags;
    },
    toggleStripUTags() {
      this.stripUTags = !this.stripUTags;
    },
    toggleStripFontTags() {
      this.stripFontTags = !this.stripFontTags;
    },




    toggleaeToTj() {
      this.aeToTj = !this.aeToTj;
    },
    toggleAEToTJ() {
      this.AEToTJ = !this.AEToTJ;
    },
    toggleeToch() {
      this.eToch = !this.eToch;
    },
    toggleEToCH() {
      this.EToCH = !this.EToCH;
    },
    toggleKeepBOM(){
      this.keepBOM = !this.keepBOM;
    }
  },
  mounted: function () {
    this.establishWSConnection();
  },
  computed: {
    downloadLinkForUser() {
      return "api/rest/1.0/downloadFile?userId=" + this.userId;
    },
    subtitleFilename(){

      const subtitleDataObj = this.subtitleDataStore.subtitleData;

      if(subtitleDataObj != null && subtitleDataObj.filename){
        return subtitleDataObj.filename.substr(subtitleDataObj.filename.indexOf("_") + 1, subtitleDataObj.filename.length);
      }else{
        return "";
      }
    }


  }
}

</script>