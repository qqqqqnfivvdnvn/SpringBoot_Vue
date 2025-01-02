<template>
  <div>
    <h1>{{ message }}</h1>
  </div>
</template>

<script>
import { ref } from 'vue';

export default {
  setup() {
    const message = ref('Hello Vue 3!');
    return { message };
  }
};
</script>


<style>
h1 {
  color: #42b983;
}
</style>