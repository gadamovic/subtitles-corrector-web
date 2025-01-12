<template>
  <div class="content">
    <div class="section">
      <HeadingComponent first_line="Subtitles corrector" 
                        second_line="Easily fix character encoding, formatting issues, and edit subtitles, ensuring your subtitle files are clean, accurate, and ready for use."
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

body:after{
  content: "beta";
  position: fixed;
  width: 80px;
  height: 25px;
  background: #004266;
  top: 7px;
  left: -20px;
  text-align: center;
  font-size: 13px;
  font-family: sans-serif;
  text-transform: uppercase;
  font-weight: bold;
  color: #fff;
  line-height: 27px;
  transform:rotate(-45deg);
}


</style>
