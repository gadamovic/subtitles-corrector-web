<template>
  <div class="modal" :class="modalActive ? 'is-active' : ''">
    <div class="modal-background"></div>
    <div style="position: fixed; bottom:0; left: 0; right: 0; z-index: 50;">
      <div class="container">
        <div class="columns is-multiline is-centered">
          <div class="column is-10 mb-5">
            <div class="p-6 has-background-light is-rounded">
              <h4 class="mb-2 is-size-5 has-text-weight-bold">Cookie Policy</h4>
              <p class="mb-4 has-text-grey-dark">We use cookies only for analytical purposes to improve your experience.
                You can choose to allow or decline these cookies below.</p>
              <a class="button is-primary mr-2" href="#" @click="allow">Allow</a>
              <a class="button is-outlined is-danger" href="#" @click="decline">Decline</a>
            </div>
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
      window.location.reload();
    },
    decline() {
      setCookie('_cookieConsent', 'decline', 60);
      this.modalActive = false;
      window.location.reload();
    }
  },
}

</script>