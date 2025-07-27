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


                    <div class="box mb-3">
                        <p class="title is-5 has-text-centered mb-3">AI options (experimental)</p>
                        <div class="checkboxes">
                            <label class="checkbox">
                                <input type="checkbox" @click="console.log('aa')" />
                                AI correction
                            </label>
                            <i>Give AI a try at correcting your file! Keep in mind that AI can make mistakes and this
                                feature is still in experimental phase.</i>
                        </div>
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

export default{
    name: "SubtitleConverters",
    components: {GenericButton},
    data: function(){
        return{
            upload_button_enabled:true,
            loading:false,
            file: null, // Selected file
            userId: crypto.randomUUID(),
        }
    },
    methods:{
        handleFileChange(){

        },
        upload(){

        }
    }
}
</script>