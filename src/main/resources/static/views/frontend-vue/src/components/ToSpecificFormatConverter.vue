<template>
    <div class="columns is-centered m-5">
        <div class="box has-text-grey column is-two-thirds">
            <div class="content p-5">

                <h1>{{targetFormat}} Subtitle Converter</h1>

                <form @submit.prevent enctype="multipart/form-data" class="box" style="background-color: #004266;">

                    <label class="label has-text-white has-text-centered title is-5">Step 1: Upload subtitle file:</label>
                    <br />
                    <div class="file has-name is-fullwidth field mb-3">
                        <label class="file-label">
                            <input class="file-input" type="file" name="file" :accept="this.supportedFileFormats"
                                @change="handleFileChange" />
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


                    <div class="box mb-3" ref="formatsDiv" v-if="showUploadedFileData">
                        <p class="title is-5 has-text-centered mb-3">Uploaded file's info</p>

                        <p v-if="this.subtitlesFilename != null">Filename: {{ this.subtitlesFilename }}</p>
                        <p>
                            <span v-if="this.encoding != null">File encoding: {{ this.encoding }}
                                <span v-if="this.encoding == 'UTF-8'" class="icon has-text-success fas fa-check"></span>
                            </span>
                        </p>
                        <p v-if="this.encoding != null">Source format: {{ this.sourceFormat }}</p>

                    </div>


                    <div v-if="showUploadedFileData">
                        <label class="label has-text-white has-text-centered title is-5 mb-5">Step 2: Download!</label>
                        <div class="notification is-link is-light has-text-centered mb-5">
                            <a class="button is-link is-medium is-flex is-align-items-center is-justify-content-center"
                                @click="downloadFile" style="max-width: 100%; overflow: hidden;"
                                :title="createSubtitleFileName(subtitlesFilename, targetFormat, sourceFormat)">
                                📥 Download&nbsp;
                                <span style="
                                display: inline-block;
                                overflow: hidden;
                                text-overflow: ellipsis;
                                white-space: nowrap;
                                vertical-align: bottom;
                            ">
                                    {{ createSubtitleFileName(subtitlesFilename, targetFormat, sourceFormat) }}
                                </span>
                            </a>
                        </div>
                    </div>


                    <GenericButton :loading="loading" button_text="Upload" :enabled="this.upload_button_enabled"
                        @click="upload">
                    </GenericButton>
                    <p class="has-text-white mt-2 is-size-7 has-text-centered">
                        Supported formats: {{this.supportedFileFormats}}
                    </p>
                    <!-- Error Message -->
                    <div class="notification is-danger has-text-centered" style="margin-bottom: 24px;" v-if="error">
                        {{ error }}
                    </div>
                </form>

            </div>
        </div>
    </div>
</template>

<script>
import GenericButton from './GenericButton.vue';
import { createSubtitleFileName, createDownloadedFileName } from '@/js/util.js'

export default {
    name: "ToSrtConverter",
    components: { GenericButton },
    data: function () {
        return {
            error: null, // Error message
            upload_button_enabled: true,
            loading: false,
            file: null, // Selected file
            userId: crypto.randomUUID(),
            sourceFormat: null,
            showUploadedFileData: false,
            encoding: null,
            subtitlesFilename: null,
            numberOfSubtitleLines: null,
            createSubtitleFileName,
        }
    },
    props:{
        targetFormat: String,
        supportedFileFormats: {
            type: Array
        }
    },
    methods: {
        handleFileChange(event) {
            if (event.target.files == null || event.target.files.length == 0) {
                return;
            }

            this.file = event.target.files[0];

            const fileName = this.file.name.toLowerCase();
            const isValid = this.supportedFileFormats.some(ext => fileName.endsWith(ext));

            if (!isValid) {
                this.error = 'Invalid file type. Allowed types are: ' + this.supportedFileFormats;
                this.upload_button_enabled = false;
                event.target.value = ''; // Clear the input
                this.file = null;
            } else {
                this.error = '';
                this.upload_button_enabled = true;
            }

        },
        async upload() {

            if (!this.file) {
                this.error = "Please select a file.";
                return;
            }

            this.loading = true;
            //at the beginning of the upload, format chooser should be hidden
            if (this.showUploadedFileData) {
                this.showUploadedFileData = false;
            }

            this.init();

            const formData = new FormData();

            formData.append("businessOperation", "conversion");
            formData.append("file", this.file);
            formData.append("webSocketUserId", this.userId);
            formData.append("targetFormat", this.targetFormat);

            try {
                const response = await fetch("api/rest/1.0/conversions/upload", {
                    method: "POST",
                    body: formData,
                });

                if (response.ok) {

                    const result = await response.json();
                    this.sourceFormat = result.detectedSourceFormat;
                    this.encoding = result.detectedEncoding;
                    this.subtitlesFilename = result.filename;
                    this.numberOfSubtitleLines = result.numberOfLines;

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
            } finally {
                this.showUploadedFileData = true;
                this.$nextTick(() => {
                    this.disableSourceFormat();
                });
            }
        },
        disableSourceFormat() {

            const radios = this.$refs.formatsDiv.querySelectorAll('input[type="radio"]');
            radios.forEach(element => {
                if (element.value === this.sourceFormat) {
                    element.closest(".field").style.display = "none";
                }
            });

        },
        init() {

            this.sourceFormat = null;
            this.showUploadedFileData = false;
            this.encoding = null;
            this.subtitlesFilename = null;
            this.numberOfSubtitleLines = null;


            if (!this.showUploadedFileData) {
                return;
            }

            const radios = this.$refs.formatsDiv.querySelectorAll('input[type="radio"]');
            radios.forEach(element => {
                if (element.closest(".field").style.display == "none") {
                    element.closest(".field").style.display = "block";
                }
            })
        },
        async downloadFile() {
            const response = await fetch("api/rest/1.0/downloadConvertedFile?userId=" + this.userId + "&targetFormat=" + this.targetFormat, {
                method: "GET"
            });

            if (response.ok) {
                const blob = await response.blob();

                const url = window.URL.createObjectURL(blob);
                const a = document.createElement("a");

                a.href = url;

                a.download = createDownloadedFileName(this.subtitlesFilename, this.targetFormat, this.sourceFormat);
                document.body.appendChild(a);
                a.click();

                // Clean up
                a.remove();
                window.URL.revokeObjectURL(url);
            }
        },

    }
}
</script>