<template>

<div class="box" style="background-color: #004266;">

    <div class="field">
        <label class="label has-text-white">Noticed a subtitle problem that Subtitles Corrector doesn't fix? Have a feedback or suggestion?
            Let us know!</label>
        <input placeholder="Email" class="input" type="email" id="email" name="email"/>
    </div>

    <div class="field">
        <input class="input" placeholder = "Description" type="text" id="description" name="description"/>
    </div>

    <GenericButton button_text="Submit"/>
</div>

</template>

<script>
import GenericButton from './GenericButton.vue';

export default{
    name: "ContactForm",
    components: {
        GenericButton
    }
}
</script>