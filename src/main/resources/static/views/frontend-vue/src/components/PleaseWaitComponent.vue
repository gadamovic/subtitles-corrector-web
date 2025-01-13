<template>

    <div><p class="label">{{ this.textActual }}</p></div>

</template>

<script>
export default{

    name:"PleaseWaitComponent",
    data: function() {
        return{
            textBase: 'Please wait',
            textActual: ''
        }
    },
    mounted: function(){

        setInterval(() => {
            if(this.textActual.endsWith('...')){
                this.textActual = this.textBase;
            }else{
                if(this.textActual.length == 0){
                    this.textActual = this.textBase;
                }else{
                    this.textActual += '.';
                }
            }
        }, 200);

    }

}


</script>