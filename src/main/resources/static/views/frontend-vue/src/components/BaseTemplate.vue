<template>

  <div class="pb-20">
    <nav class="navbar py-5" style="margin-left: 70px; background-color: unset;">
      <div class="container is-fluid">
        <div class="navbar-brand">

          <a class="navbar-menu-open navbar-burger" role="button" type="button" data-toggle="side-menu"
            aria-expanded="false" @click="() => { this.isNarrowMenuActive = !this.isNarrowMenuActive;}">

            <span aria-hidden="true"></span>
            <span aria-hidden="true"></span>
            <span aria-hidden="true"></span>
            <span aria-hidden="true"></span>

          </a>
        </div>
        <div class="navbar-menu" :class="isNarrowMenuActive ? 'is-active' : ''">
          <div class="navbar-end">
            <ul class="navbar-item box is-hidden-mobile is-hidden-tablet-only">
              <li><RouterLink class="navbar-item has-text-success" to="/"><strong>Home</strong></RouterLink></li>
              <!-- <li><RouterLink class="navbar-item" to="/converters"><strong>Converters</strong></RouterLink></li> -->
              <li><RouterLink class="navbar-item" to="/to-srt-converter"><strong>To srt converter</strong></RouterLink></li>
              <li><RouterLink class="navbar-item" to="/to-vtt-converter"><strong>To vtt converter</strong></RouterLink></li>
              <li><RouterLink class="navbar-item" to="/to-ass-converter"><strong>To ass converter</strong></RouterLink></li>
              <li><RouterLink class="navbar-item" to="/privacy-policy"><strong>Privacy Policy</strong></RouterLink></li>
              <li><RouterLink class="navbar-item" to="/terms-of-service"><strong>Terms of service</strong></RouterLink></li>
            </ul>
            <ul class="navbar-item is-hidden-desktop">
              <li><RouterLink class="navbar-item" to="/"><strong>Home</strong></RouterLink></li>
              <!-- <li><RouterLink class="navbar-item" to="/converters"><strong>Converters</strong></RouterLink></li> -->
              <li><RouterLink class="navbar-item" to="/to-srt-converter"><strong>To srt converter</strong></RouterLink></li>
              <li><RouterLink class="navbar-item" to="/to-vtt-converter"><strong>To vtt converter</strong></RouterLink></li>
              <li><RouterLink class="navbar-item" to="/to-ass-converter"><strong>To ass converter</strong></RouterLink></li>
              <li><RouterLink class="navbar-item" to="/privacy-policy"><strong>Privacy Policy</strong></RouterLink></li>
              <li><RouterLink class="navbar-item" to="/terms-of-service"><strong>Terms of service</strong></RouterLink></li>
            </ul>
            <!-- <div class="navbar-item is-hidden-mobile pl-0"><a
                class="button is-primary has-background-danger-light has-text-danger" href="#">Contact Us</a></div> -->
          </div>
        </div>
      </div>
    </nav>

    <!-- This is the content part that changes with different urls -->
    <RouterView />

  </div>

  <FooterComponent style="background-color: unset;"></FooterComponent>
  <ModalCookiesConsent></ModalCookiesConsent>

</template>

<script>
import FooterComponent from './FooterComponent.vue'
import ModalCookiesConsent from './ModalCookiesConsent.vue';



export default {
  name: "BaseTemplate",
  components: {FooterComponent, ModalCookiesConsent},
  data: function () {
    return {
      isNarrowMenuActive: false
    }
  },
}

</script>