<template>

        <div class="loader-wrapper" style="width:100%; height:80px" :class="isActive ? 'is-active' : ''">
            <div style="width:80px; height:80px" class="loader is-loading"></div>
        </div>

</template>

<script>
export default {
    name: "LoaderComponent",
    props:{
        customStyle: String,
        isActive: Boolean
    },
    data: function () {
        return {
            
        }
    }
}

</script>

<style>
.loader-wrapper {
    height: 100%;
    width: 100%;
    background: #fff;
    opacity: 0;
    z-index: -1;
    transition: opacity .3s;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 6px;

    &.is-active {
        opacity: 1;
        z-index: 1;
    }
}

.is-loading {
    position: relative;
}

</style>