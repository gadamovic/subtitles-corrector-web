<template>

    <div v-if="subtitle.editOperations">


    <span v-for="(operationGroup, key2) in subtitle.editOperations" :key="key2">


        <!-- Replace operation strikethrough (all consecutive characters) -->
        <span v-for="(operation, key) in operationGroup" :key="key">

            <span v-if="operation.type == 'REPLACE'" style="display: inline-flex;">

                <span style="color: red"><del v-html="operation.char1"></del></span>

            </span>

        </span>


        <!-- Replace operation replaced characters (all consecutive characters) -->
        <span v-for="(operation, key) in operationGroup" :key="key">

            <span v-if="operation.type == 'REPLACE'" style="display: inline-flex;">

                <span style="color: green" v-html="operation.char2"></span>

            </span>

        </span>

        <span v-for="(operation, key) in operationGroup" :key="key">
            <span v-if="operation.type == 'DELETE'">

                <span style="color: red" v-html="operation.char1"></span>

            </span>


            <span v-if="operation.type == 'ADD'">

                <span style="color: green" v-html="operation.char1"></span>

            </span>


            <span v-if="operation.type == 'KEEP'">

                <span v-html="operation.char1"></span>

            </span>


        </span>
    </span>

    </div>

    <div v-else>
    <p v-html="subtitle.text"></p>
    </div>

</template>

<script>
export default{
    name: "SubtitleLine",
    props: {
        subtitle: Object
    },
    data: function() {
        return{

        }
    },
    computed:{
        // subtitleComp(){

        //     let subtitleObj = this.subtitle;
        //     let text = subtitleObj.text;
        //     let i = 0;
        //     for(let editOperationGroup in subtitleObj.editOperations){
                
        //         if(editOperationGroup[0].type == 'REPLACE'){
        //             text.
        //         }

        //         if(editOperationGroup[0].type == 'KEEP'){
        //             i++;
        //         }

        //         i++;
        //     }

        //     return "";
        // }
    }


}
</script>