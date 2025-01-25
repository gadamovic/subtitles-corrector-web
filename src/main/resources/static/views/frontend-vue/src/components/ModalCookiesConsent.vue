<template>
    <div class="column is-half" style="position: fixed; bottom:0; right: 0; z-index: 50;" v-if="modalActive">
      <div class="container">
        <div class="columns is-centered">
          <div class="column">
            <div class="p-5 has-background-light is-rounded">
              <h4 class="is-size-5 has-text-weight-bold">Cookie Policy</h4>
              <p class="has-text-grey-dark mb-3">We use cookies only for analytical purposes to improve your experience.
                You can choose to allow or decline these cookies below.</p>
              <a class="button is-primary mr-2" href="#" @click="allow">Allow</a>
              <a class="button is-outlined is-danger" href="#" @click="decline">Decline</a>
            </div>
          </div>
        </div>
      </div>
    </div>


</template>

<script>

import { setCookie, getCookie } from '@/js/cookies.js'

export default {
  name: "ModalComponent",

  data() {
    return {
      modalActive: (getCookie('_cookieConsent') == null) || (getCookie('_cookieConsent') == "")
    }
  },
  methods: {
    allow() {
      setCookie('_cookieConsent', 'allow', 60);
      this.modalActive = false;

        fetch("api/rest/1.0/cookiesAllowed", {
          method: "GET",
        });

      window.location.reload();
    },
    decline() {
      setCookie('_cookieConsent', 'decline', 60);
      this.modalActive = false;

      fetch("api/rest/1.0/cookiesDeclined", {
          method: "GET",
        });

      window.location.reload();
    }
  },
}

</script>