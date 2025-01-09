<template>
  <div class="content">
    <div class="section">
      <HeadingComponent first_line="Subtitles corrector"
        second_line="An app that fixes character encoding and formatting issues in subtitle files, ensuring they're clean and ready for use"
        heading_link="https://subtitles-corrector.com" />
      <FileUploader />
      <ContactForm />
    </div>
    <FooterComponent />
  </div>
</template>


<script>

import ContactForm from './components/ContactForm.vue';
import FileUploader from './components/FileUploader.vue';
import FooterComponent from './components/FooterComponent.vue';
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
  mounted: async function () {


    const response = await fetch("https://hutils.loxal.net/whois", {
      method: "GET"
    });

    if (response.ok) {

      const whoIsResult = await response.json();
      fetch("api/rest/1.0/logUser", {
        method: "POST",
        body: JSON.stringify(whoIsResult),
        headers: new Headers({
          'Content-Type': 'application/json'
        }),
      });

    }

  },
};
</script>

<style>

#app {
  max-width: 600px;
  margin: auto;
  text-align: center;
}

body:after {
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
  transform: rotate(-45deg);
}
</style>
