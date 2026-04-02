<template>
  <web-view :src="src" />
</template>

<script>
export default {
  data() {
    return {
      src: ''
    };
  },
  onLoad(options) {
    this.src = decodeURIComponent((options && options.url) || '');
  }
};
</script>
