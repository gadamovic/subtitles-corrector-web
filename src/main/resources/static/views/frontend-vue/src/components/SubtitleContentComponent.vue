<template>

    <div v-for="(subtitle, key) in content" :key="key">
        <input class="input" :value="subtitle.text"/>
    </div>

</template>

<script>

export default {

    name:"SubtitleContentComponent",
    props: {
        content: Object
    }

}

</script>