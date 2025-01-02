<template>
  <div class="content">
    <div  class="section">
      <HeadingComponent first_line="Subtitles corrector" 
                        second_line="An app that fixes character encoding and formatting issues in subtitle files, ensuring they're clean and ready for use"
                        heading_link="https://subtitles-corrector.com"/>
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
import '@fortawesome/fontawesome-free/css/all.css';
export default {
  name: "App",
  components: {
    FileUploader,
    FooterComponent,
    HeadingComponent,
    ContactForm,
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