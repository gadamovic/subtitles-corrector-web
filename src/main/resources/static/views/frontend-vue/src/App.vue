<script setup>

import BaseTemplate from './components/BaseTemplate.vue'

</script>

<template>

  <BaseTemplate></BaseTemplate>

</template>

<style>
body {
  background-image: url('assets/poly-background-3.png');
  min-height: 100vh;
  background-size: cover;
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
