<template>

  <div class="pb-20">
    <nav class="navbar py-5" style="margin-left: 70px;">
      <div class="container is-fluid">
        <div class="navbar-brand">
          <a class="navbar-item" href="#">
          </a>
          <a class="navbar-menu-open navbar-burger" role="button" type="button" data-toggle="side-menu" v-if="false">
            <!-- This part is visible on narrow screens -->
            <span aria-hidden="true"></span>
            <span aria-hidden="true"></span>
            <span aria-hidden="true"></span>
          </a>
        </div>
        <div class="navbar-menu" v-if="false">
          <div class="navbar-end">
            <ul class="navbar-item">
              <li><a class="navbar-item" href="#">About</a></li>
              <li><a class="navbar-item" href="#">Company</a></li>
              <li><a class="navbar-item" href="#">Services</a></li>
              <li><a class="navbar-item" href="#">Testimonials</a></li>
            </ul>
            <div class="navbar-item is-hidden-mobile pl-0"><a
                class="button is-primary has-background-danger-light has-text-danger" href="#">Contact Us</a></div>
          </div>
        </div>
      </div>
    </nav>
    <section class="section pb-0">
      <div class="container">
        <div class="columns is-vcentered">
          <div class="column is-7 mb-5 mb-0-desktop">

            <h2 class="has-mw-xl mt-8 mb-8 mb-12-desktop title is-1 is-size-2-mobile">Polish Your Subtitles with Ease.
            </h2>
            <p class="has-mw-lg mb-8 mb-12-desktop is-size-5 has-text-grey-dark">Easily fix character encoding,
              formatting issues, and edit subtitles, ensuring your subtitle files are clean, accurate, and ready for
              use.
            </p>




            <div class="container box mt-5">
              <ul class="content">
                <li class="is-flex is-align-items-center my-3">
                  <span class="icon has-text-success mr-2">
                    <i class="fas fa-check"></i>
                  </span>
                  Fix commonly seen invalid characters like: È -> Č, Æ -> Ć and much more.
                </li>

                <li class="is-flex is-align-items-center my-3">
                  <span class="icon has-text-success mr-2">
                    <i class="fas fa-check"></i>
                  </span>
                  Automatically update your encoding to UTF-8 to support wide range of characters by simply uploading a subtitle file.
                </li>
                <li class="is-flex is-align-items-center my-3">
                  <span class="icon has-text-success mr-2">
                    <i class="fas fa-check"></i>
                  </span>
                  Edit and modify subtitle files directly in your browser.
                </li>
                <li class="is-flex is-align-items-center my-3">
                  <span class="icon has-text-success mr-2">
                    <i class="fas fa-check"></i>
                  </span>
                  Download corrected subtitle files ready for use.
                </li>
                <li class="is-flex is-align-items-center my-3">
                  <span class="icon has-text-success mr-2">
                    <i class="fas fa-check"></i>
                  </span>
                  Everything complitely <span class="has-text-success">&nbsp;free</span>.
                </li>

                <li class="is-flex is-align-items-center my-3">
                  <span class="icon has-text-success mr-2">
                    <i class="fas fa-check"></i>
                  </span>
                  And much more to come...
                </li>
              </ul>



            </div>


            <!-- <div class="buttons mt-5"><a class="button is-primary" href="#">Track your performance</a><a
                class="button is-outlined is-light has-text-dark" href="#">Learn More</a></div> -->


          </div>




          <div class="is-relative column is-5">

            <!-- Main Content Section -->
            <section class="section main p-0">
              <div class="container">
                <div class="box">
                  <FileUploader />
                </div>
                <div class="box mt-5 mb-5">
                  <ContactForm />
                </div>
              </div>
            </section>

          </div>
        </div>
      </div>
    </section>

  </div>

  <FooterComponent></FooterComponent>












</template>

<script>
import ContactForm from './components/ContactForm.vue';
import FileUploader from './components/FileUploader.vue';
import FooterComponent from './components/FooterComponent.vue';

export default {
  name: 'App',
  components: {
    FileUploader,
    FooterComponent,
    ContactForm,
  },
  mounted: async function () {
    if (process.env.VUE_APP_ENVIRONMENT === 'prod') {
      const response = await fetch('https://hutils.loxal.net/whois', {
        method: 'GET',
      });

      if (response.ok) {
        const whoIsResult = await response.json();
        fetch('api/rest/1.0/logUser', {
          method: 'POST',
          body: JSON.stringify(whoIsResult),
          headers: new Headers({
            'Content-Type': 'application/json',
          }),
        });
      }
    }
  },
};
</script>

<style>
.main {
  max-width: 600px;
  margin: auto;
  text-align: center;
}

body:after {
  content: 'beta';
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
