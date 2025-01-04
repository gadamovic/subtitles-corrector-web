<template>

<div class="container" style="margin-bottom: 24px;">
    <form @submit.prevent="submitContactForm()" class="box" style="background-color: #004266;">
        <div class="field">
            <label class="label has-text-white">Noticed a subtitle problem that Subtitles Corrector doesn't fix? Have a feedback or suggestion?
                Let us know!</label> <br/>
            <input placeholder="Email" class="input" type="email" id="email" name="email" @change="handleEmailChange" required/>
        </div>

        <div class="field">
            <textarea class="textarea" placeholder = "Description" type="text" id="description" name="description" @change="handleDescriptionChange" required></textarea>
        </div>

        <GenericButton button_text="Submit" :loading="this.loading"/>
    </form>

</div>

<div class="notification" :class="this.error ? 'is-danger' : 'is-success'" v-if="contactFormConfirmation != ''">
    {{ this.contactFormConfirmation }}
</div>

</template>

<script>
import GenericButton from './GenericButton.vue';

export default{
    name: "ContactForm",
    components: {
        GenericButton
    },
    data() {
        return{
            email: String,
            description: String,
            contactFormConfirmation: '',
            error: false,
            loading: false
        }
    }, 
    methods:{
        handleEmailChange(event){
            this.email = event.target.value;
        },

        handleDescriptionChange(event){
            this.description = event.target.value;
        },
        async submitContactForm() {

            try {
                
                this.loading = true;

                const formData = new FormData();
                formData.append("email", this.email);
                formData.append("description", this.description);


                const response = await fetch("api/rest/1.0/submitContactForm", {
                    method: "POST",
                    body: formData,
                });

                //const result = await response.text();
                const result = await response.json();

                if (response.ok) {
                    this.contactFormConfirmation = "Form successfully submitted!"
                    this.error = false;
                } else {

                    if (result) {
                        this.error = true;
                        if(result == 'FAILURE_EMAIL_SEND_RATE_LIMIT'){
                            this.contactFormConfirmation = 'Temporary quotas exceeded, form not submitted. Try again later.';
                        }else if (result == 'DEVELOPMENT_NOT_SENT'){
                            this.contactFormConfirmation = 'Email not sent because we are in development.';
                        } else { 
                            this.contactFormConfirmation = 'Internal server error!';
                        }
                    } else {
                        this.contactFormConfirmation = 'Internal server error!';
                        this.error = true;
                    }
                }
            } catch (err) {
                console.error("Error:", err);
                this.contactFormConfirmation = err;
                this.error = true;
            } finally {
                this.loading = false;
            }

        }
    }
}
</script>