<template>
    <div class="columns is-centered m-5">
        <div class="box has-text-grey column is-two-thirds">
            <div class="content p-5">

                <h1>Subtitle Converter</h1>

                <form @submit.prevent enctype="multipart/form-data" class="box" style="background-color: #004266;">

                    <label class="label has-text-white has-text-centered title is-5">Upload a subtitle file:</label>
                    <br />
                    <div class="file has-name is-fullwidth field mb-3">
                        <label class="file-label">
                            <input class="file-input" type="file" name="file" accept=".srt, .vtt"
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
                        <p v-if="this.encoding != null">Detected encoding: {{ this.encoding }}</p>

                    </div>

                    <div class="box mb-3" ref="formatsDiv" v-if="showUploadedFileData">
                        <p class="title is-5 has-text-centered mb-3">Convert to:</p>

                        <div class="field">
                            <label class="radio">
                                <input type="radio" v-model="targetFormat" value="SRT" />
                                Srt
                            </label>
                        </div>

                        <div class="field">
                            <label class="radio">
                                <input type="radio" v-model="targetFormat" value="VTT" />
                                VTT
                            </label>
                        </div>
                    </div>

                    <div class="notification is-link is-light has-text-centered mb-5" v-if="this.showUploadedFileData">

                        <a :disabled="this.targetFormat ? null : true"
                            class="button is-link is-medium is-flex is-align-items-center is-justify-content-center"
                            @click="downloadFile" style="max-width: 100%; overflow: hidden;" :title="subtitlesFilename">
                            📥 Download&nbsp;
                            <span style="
                                display: inline-block;
                                max-width: 300px;
                                overflow: hidden;
                                text-overflow: ellipsis;
                                white-space: nowrap;
                                vertical-align: bottom;
                            ">
                                {{ subtitlesFilename }}
                            </span>
                        </a>
                    </div>

                    <GenericButton :loading="loading" button_text="Upload" :enabled="this.upload_button_enabled"
                        @click="upload">
                    </GenericButton>

                </form>

            </div>
        </div>
    </div>
</template>

<script>
import GenericButton from './GenericButton.vue';

export default {
    name: "SubtitleConverters",
    components: { GenericButton },
    data: function () {
        return {
            upload_button_enabled: true,
            loading: false,
            file: null, // Selected file
            userId: crypto.randomUUID(),
            sourceFormat: null,
            targetFormat: null,
            showUploadedFileData: false,
            encoding: null,
            subtitlesFilename: null,
            numberOfSubtitleLines: null

        }
    },
    methods: {
        handleFileChange(event) {
            if (event.target.files == null || event.target.files.length == 0) {
                return;
            }

            this.file = event.target.files[0];

            const allowedExtensions = ['.srt', '.vtt'/*, '.sub', '.txt'*/];
            const fileName = this.file.name.toLowerCase();
            const isValid = allowedExtensions.some(ext => fileName.endsWith(ext));
            console.log(isValid);
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
            formData.append("userId", this.userId);
            formData.append("targetFormat", this.targetFormat);

            try {
                const response = await fetch("api/rest/1.0/upload", {
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
                    // element.disabled = true;

                    // // Add text next to the radio button
                    // const span = document.createElement("span");
                    // span.textContent = " (Source format)";
                    // span.style.marginLeft = "6px";

                    // // Insert after the radio input inside the label
                    // element.parentNode.appendChild(span);

                    element.closest(".field").style.display = "none";
                }
            });

        },
        init() {

            this.sourceFormat = null;
            this.targetFormat = null;
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

                a.download = this.createDownloadedFileName(this.subtitlesFilename, this.targetFormat, this.sourceFormat);
                document.body.appendChild(a);
                a.click();

                // Clean up
                a.remove();
                window.URL.revokeObjectURL(url);
            }
        },
        createDownloadedFileName(filename, targetFormat, sourceFormat){

            if(filename.toLowerCase().endsWith("." + sourceFormat.toLowerCase())){
                const dotIndex = filename.lastIndexOf('.');
                filename = filename.substr(0, dotIndex);
                filename += ("." + targetFormat.toLowerCase());
            }else{
                filename += ("." + targetFormat.toLowerCase());
            }

            return '[subtitles-corrector.com]-' + filename;
        }

    }
}
</script>