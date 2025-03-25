<template>
<!--  <nav> 是一个语义化的 HTML 元素，用于定义导航栏。-->
<!--    <router-link> 是 Vue Router 提供的一个组件，用于创建导航链接。-->
<!--      to="/login" 表示这个链接会导航到路径为 /login 的路由。-->
<!--      如果没有在 <router-link> 中添加任何内容（如文本或图标），它将不会显示任何东西。通常，你会在 <router-link> 中添加一些文本或图标，例如-->

  <div id="app">
    <nav>
      <router-link to="/login"></router-link>
    </nav>
    <router-view />
  </div>


</template>



<style>

</style>