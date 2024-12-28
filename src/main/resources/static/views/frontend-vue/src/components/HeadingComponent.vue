<template>
<h1 class="title has-text-primary has-text-centered" v-if="first_line">{{ first_line }}</h1>
<p class="subtitle has-text-centered" v-if="second_line">{{second_line}}</p>

</template>

<script>
export default{
    name: "HeadingComponent",
    props: {
        first_line: String,
        second_line: String
    }
}
</script>