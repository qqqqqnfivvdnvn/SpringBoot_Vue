<template>
  <div>
    <h1>{{ message }}</h1>
    <p>Name: {{ name }}</p>
    <p>Age: {{ age }}</p>
  </div>
</template>

<script>
import { ref } from 'vue';

export default {
  setup() {
    const message = ref('Hello Vue 3.0!')

    return {
      message: message,
      name: 'John',
      age: 30
    };
  }
};
</script>

<style>
h1 {
  color: #42b983;
}
p {
  font-size: 16px;
}
</style>
